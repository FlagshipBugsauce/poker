package com.poker.poker.services;

import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.EmitterModel;
import com.poker.poker.models.enums.EmitterType;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SseService {
  /*
       TODO: It seems like the .complete() is running on a different thread, and thus, the
        emitter is not being removed from the hash map before the log which outputs all the
        emitters in the hash map, is outputted. The emitter is being removed, it's just
        occurring AFTER the log. Should investigate some way of ensuring the emitter is removed
        before logging. Probably can use a semaphore or some kind of loop with a thread.sleep.
        Update: No longer outputting a hash map but this problem should still be investigated.
  */

  private final GameConstants gameConstants;

  /**
   * A map of emitter maps, keyed by the emitter type. Each emitter map is keyed by UUID's which are
   * associated to a specific user. The getEmitterMap method can be used to retrieve the appropriate
   * map and a UUID can be used to send data to a specific user.
   */
  private Map<EmitterType, Map<UUID, EmitterModel>> emitterMaps;

  /**
   * Constructor that injects game constants and sets up the emitter map appropriately.
   *
   * @param gameConstants Constants required for events which occur in the game.
   */
  public SseService(GameConstants gameConstants) {
    this.gameConstants = gameConstants;
    emitterMaps = new HashMap<>();
    emitterMaps.put(EmitterType.GameList, new HashMap<>());
    emitterMaps.put(EmitterType.Lobby, new HashMap<>());
    emitterMaps.put(EmitterType.Game, new HashMap<>());
    emitterMaps.put(EmitterType.Hand, new HashMap<>());
  }

  /**
   * Helper which will return the appropriate emitter map using the type provided, or throw if the
   * type is invalid.
   *
   * @param type The type of emitter map specified.
   * @return Emitter map with the specified type.
   */
  private Map<UUID, EmitterModel> getEmitterMap(EmitterType type) {
    final Map<UUID, EmitterModel> emitterMap = emitterMaps.get(type);
    if (emitterMaps.get(type) == null) {
      log.error("Invalid emitter type specified.");
      throw gameConstants.getInvalidEmitterTypeException();
    }
    return emitterMap;
  }

  /**
   * Helper which returns the emitter timeout value from constants class based on the type
   * specified.
   *
   * @param type The type of the emitter the timeout value is being sought for.
   * @return The appropriate timeout value for the emitter type specified.
   */
  private long getEmitterTimeout(EmitterType type) {
    final long timeout;
    switch (type) {
      case GameList:
        timeout = gameConstants.getJoinGameEmitterDuration();
        break;
      case Lobby:
      case Game:
      case Hand:
        timeout = gameConstants.getGameEmitterDuration();
        break;
      default:
        log.error("Invalid emitter type specified.");
        throw gameConstants.getInvalidEmitterTypeException();
    }
    return timeout;
  }

  /**
   * Helper that retrieves an emitter of a specified type for a specified user. Handles the case
   * where there is no such emitter so that there is no chance of unhandled null pointer exceptions.
   *
   * @param type The type of emitter being sought (GameList, Lobby, etc...).
   * @param userId The user the emitter being sought is associated with.
   * @return An SseEmitter of the specified type, associated with the specified user, provided such
   *     an emitter exists. If no such emitter exists, then a BadRequestException is thrown.
   */
  private SseEmitter getEmitter(EmitterType type, UUID userId) {
    log.debug("Attempting to retrieve {} emitter for {}.", type, userId);
    final SseEmitter emitter = getEmitterModel(type, userId).getEmitter();
    if (emitter == null) {
      log.error("There is no {} emitter associated with the user ID: {}.", type, userId);
      throw gameConstants.getNoEmitterForIdException();
    }
    log.debug("{} emitter retrieved for {} successfully.", type, userId);
    return emitter;
  }

  /**
   * Helper that retrieves the emitter model of a specified type for a specified user. Handles the
   * case where there is no such emitter so that a BadRequestException will be thrown with the
   * appropriate message.
   *
   * @param type The type of emitter model being sought (GameList, Lobby, etc...).
   * @param userId The user the emitter being sought is associated with.
   * @return An emitter model of the specified type, associated with the specified user, provided
   *     such an emitter model exists. If no such emitter model exists, then a BadRequestException
   *     is thrown.
   */
  private EmitterModel getEmitterModel(EmitterType type, UUID userId) {
    // Note: This logging is a bit overkill, but we can set debug level logs to be ignored later.
    log.debug("Attempting to retrieve {} emitter model for {}.", type, userId);
    final Map<UUID, EmitterModel> map = getEmitterMap(type);
    final EmitterModel emitterModel = map.get(userId);
    if (emitterModel == null) {
      log.error("There is no {} emitter associated with the user ID: {}.", type, userId);
      throw gameConstants.getNoEmitterModelForIdException();
    }
    log.debug("{} emitter model retrieved for {} successfully.", type, userId);
    return emitterModel;
  }

  private void updateEmitterModel(EmitterType type, UUID userId, DateTime time, Object data) {
    final EmitterModel emitterModel = getEmitterModel(type, userId);
    emitterModel.setLastSendTime(time);
    emitterModel.setLastSent(data);
    log.debug("{} emitter model for user {} was updated successfully.", type, userId);
  }

  /**
   * A scheduled task that will re-send whatever data was last sent, to each emitter, in order to
   * prevent browsers from deeming the emitter to be inactive. This task will also check if no new
   * data has been sent for a specified period of time (specified in game constants) and if this is
   * the case, the emitter's complete() method will be called in order to destroy it. A period of
   * inactivity this long most likely indicates something went wrong and even though the emitter was
   * not manually destroyed, it is no longer needed and should not be kept.
   */
  @Scheduled(cron = "0 0/1 * * * ?") // Runs at the start of every minute
  public void emitterManagement() {
    log.debug("Running scheduled task to keep emitters alive.");
    for (Map<UUID, EmitterModel> map : emitterMaps.values()) {
      for (UUID userId : map.keySet()) {
        EmitterModel emitterModel = map.get(userId);
        // Now minus refresh rate.
        DateTime t = DateTime.now().minusMinutes(gameConstants.getEmitterRefreshRateInMinutes());
        if (emitterModel.getLastSendTime().isBefore(t)) {
          try {
            // Re-send whatever was sent last.
            emitterModel.getEmitter().send(emitterModel.getLastSent());
            log.debug("Emitter for user {} was refreshed.", userId);
          } catch (IOException e) {
            log.error(
                "Scheduled emitter updater failed to send to an emitter, calling complete() "
                    + "on this emitter.");
            emitterModel.getEmitter().complete();
          }
        }
        // Check if the emitter should be destroyed due to inactivity.
        t = DateTime.now().minusMinutes(gameConstants.getEmitterInactiveExpirationInMinutes());
        if (emitterModel.getLastSendTime().isBefore(t)) {
          log.info(
              "Emitter for user {} was destroyed due to {} minutes of inactivity.",
              userId,
              gameConstants.getEmitterInactiveExpirationInMinutes());
          emitterModel.getEmitter().complete();
        }
      }
    }
  }

  /**
   * Creates, stores and returns an emitter which will be used to send the client updates.
   *
   * @param type The type of the emitter. There are several types, such as a lobby emitter, which
   *     will send the client updates whenever the state of the game lobby changes.
   * @param userId The UUID of the user requesting an emitter.
   * @param validator A lambda supplied by the caller to ensure an emitter is only sent when certain
   *     preconditions have been satisfied.
   * @return An SSE emitter which will send updates to the client that requested the emitter.
   */
  public SseEmitter createEmitter(EmitterType type, UUID userId, Runnable validator) {
    log.debug("Attempting to create {} emitter for {}.", type, userId);
    // Caller provides a lambda that will throw if pre-conditions are not satisfied.
    validator.run();

    // If the user requesting an emitter already has one, then return that.
    if (emitterMaps.get(type).get(userId) != null
        && emitterMaps.get(type).get(userId).getEmitter() != null) {
      return emitterMaps.get(type).get(userId).getEmitter();
    }

    final SseEmitter emitter;
    final Map<UUID, EmitterModel> emitterMap = getEmitterMap(type);

    // Create the emitter.
    emitter = new SseEmitter(getEmitterTimeout(type));
    emitter.onCompletion(
        () -> {
          log.debug("{} emitter for user {} is complete.", type, userId);
          emitterMap.remove(userId);
        });
    emitter.onTimeout(
        () -> {
          log.debug("{} for user {} is timed out.", type, userId);
          emitterMap.remove(userId);
          emitter.complete();
        });
    emitter.onError(
        (ex) -> {
          log.error("{} for user {} encountered an error.", type, userId);
          log.error(Arrays.toString(ex.getStackTrace()));
          emitterMap.remove(userId);
        });

    // Save the emitter to the map.
    DateTime now = DateTime.now();
    emitterMap.put(userId, new EmitterModel(emitter, now, now, now));

    // Return the emitter.
    log.debug("Sending {} emitter to {}.", type, userId);
    return emitter;
  }

  /**
   * Sends data to the client associated with the userId specified, using the type of emitter
   * specified.
   *
   * @param type The type of emitter to use in order to send the client data.
   * @param userId The ID of the user the data should be sent to.
   * @param data The data that should be sent to the client.
   */
  public ApiSuccessModel sendUpdate(EmitterType type, UUID userId, Object data) {
    log.debug("Attempting to use {} emitter to send data to {}.", type, userId);
    try {
      getEmitter(type, userId).send(data);
      log.debug("{} data appears to have been sent successfully to {}.", type, userId);
      updateEmitterModel(type, userId, DateTime.now(), data);
    } catch (IOException e) {
      log.error("{} emitter failed to send data to {} due to IOException.", type, userId);
    }
    return new ApiSuccessModel("Data sent successfully.");
  }

  /**
   * Sends data to all clients associated with emitters of the specified type.
   *
   * @param type The type of emitter to broadcast to.
   * @param data The data to send.
   */
  public void sendToAll(EmitterType type, Object data) {
    getEmitterMap(type).keySet().forEach(id -> sendUpdate(type, id, data));
  }

  /**
   * Calls the complete method on the emitter, provided the emitter type specified is valid and
   * there is an emitter of the specified type, associated with the user ID provided.
   *
   * @param type The type of emitter being destroyed.
   * @param userId The user ID of the user the emitter is associated with.
   */
  public ApiSuccessModel completeEmitter(EmitterType type, UUID userId) {
    log.debug("Attempting to call complete() on {} emitter for {}.", type, userId);
    getEmitter(type, userId).complete();
    return new ApiSuccessModel("Emitter was destroyed successfully.");
  }
}
