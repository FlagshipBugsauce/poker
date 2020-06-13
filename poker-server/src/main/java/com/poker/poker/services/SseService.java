package com.poker.poker.services;

import com.poker.poker.config.constants.EmitterConstants;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.EmitterModel;
import com.poker.poker.models.enums.EmitterType;
import com.poker.poker.validation.exceptions.BadRequestException;
import com.poker.poker.validation.exceptions.ForbiddenException;
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
public final class SseService {

  private final EmitterConstants emitterConstants;

  /**
   * A map of emitter maps, keyed by the emitter type. Each emitter map is keyed by UUID's which are
   * associated to a specific user. The getEmitterMap method can be used to retrieve the appropriate
   * map and a UUID can be used to send data to a specific user.
   */
  private final Map<EmitterType, Map<UUID, EmitterModel>> emitterMaps;

  /**
   * Constructor that injects game constants and sets up the emitter map appropriately.
   *
   * @param emitterConstants Constants required for events which occur in the game.
   */
  public SseService(final EmitterConstants emitterConstants) {
    this.emitterConstants = emitterConstants;
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
   * @throws BadRequestException If no emitter map for the specified type exists.
   */
  private Map<UUID, EmitterModel> getEmitterMap(final EmitterType type) throws BadRequestException {
    final Map<UUID, EmitterModel> emitterMap = emitterMaps.get(type);
    if (emitterMaps.get(type) == null) {
      log.error("Invalid emitter type specified.");
      throw emitterConstants.getInvalidEmitterTypeException();
    }
    return emitterMap;
  }

  /**
   * Helper which returns the emitter timeout value from constants class based on the type
   * specified.
   *
   * @param type The type of the emitter the timeout value is being sought for.
   * @return The appropriate timeout value for the emitter type specified.
   * @throws BadRequestException If no emitter of the specified type exists.
   */
  private long getEmitterTimeout(final EmitterType type) throws BadRequestException {
    final long timeout;
    switch (type) {
      case GameList:
        timeout = emitterConstants.getJoinGameEmitterDuration();
        break;
      case Lobby:
      case Game:
      case Hand:
        timeout = emitterConstants.getGameEmitterDuration();
        break;
      default:
        log.error("Invalid emitter type specified.");
        throw emitterConstants.getInvalidEmitterTypeException();
    }
    return timeout;
  }

  /**
   * Helper that retrieves an emitter of a specified type for a specified user. Handles the case
   * where there is no such emitter so that there is no chance of unhandled null pointer exceptions.
   *
   * @param type The type of emitter being sought (GameList, Lobby, etc...).
   * @param userId The user the emitter being sought is associated with.
   * @return An SseEmitter of the specified type, associated with the specified user.
   * @throws BadRequestException If no emitter of the specified type for the specified user exists.
   */
  public final SseEmitter getEmitter(EmitterType type, UUID userId) throws BadRequestException {
    final SseEmitter emitter = getEmitterModel(type, userId).getEmitter();
    if (emitter == null) {
      throw emitterConstants.getNoEmitterForIdException();
    }
    return emitter;
  }

  /**
   * Helper that retrieves the emitter model of a specified type for a specified user. Handles the
   * case where there is no such emitter so that a BadRequestException will be thrown with the
   * appropriate message.
   *
   * @param type The type of emitter model being sought (GameList, Lobby, etc...).
   * @param userId The user the emitter being sought is associated with.
   * @return An emitter model of the specified type, associated with the specified user.
   * @throws BadRequestException If no emitter model matching the parameters is found.
   */
  public final EmitterModel getEmitterModel(EmitterType type, UUID userId)
      throws BadRequestException {
    final Map<UUID, EmitterModel> map = getEmitterMap(type);
    final EmitterModel emitterModel = map.get(userId);
    if (emitterModel == null) {
      log.error("There is no {} emitter associated with the user ID: {}.", type, userId);
      throw emitterConstants.getNoEmitterModelForIdException();
    }
    return emitterModel;
  }

  /**
   * Updates the emitter model of the specified type for the specified user.
   *
   * @param type The type of emitter model being updated.
   * @param userId The ID of the user the emitter belongs to.
   * @param time The time the last new data was sent to the user.
   * @param data The data that was last sent to the user.
   * @throws BadRequestException If the model cannot be retrieved.
   */
  private void updateEmitterModel(EmitterType type, UUID userId, DateTime time, Object data)
      throws BadRequestException {
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
   * not manually destroyed. Since it is no longer needed, there's no reason to keep it.
   */
  @Scheduled(cron = "0 0/1 * * * ?") // Runs at the start of every minute
  public void emitterManagement() {
    for (Map<UUID, EmitterModel> map : emitterMaps.values()) {
      for (UUID userId : map.keySet()) {
        EmitterModel emitterModel = map.get(userId);
        // Now minus refresh rate.
        DateTime t = DateTime.now().minusMinutes(emitterConstants.getEmitterRefreshRateInMinutes());
        if (emitterModel.getLastSendTime().isBefore(t)) {
          try {
            // Re-send whatever was sent last.
            emitterModel.getEmitter().send(emitterModel.getLastSent());
          } catch (IOException e) {
            log.error(
                "Scheduled emitter updater failed to send to an emitter, calling complete() "
                    + "on this emitter.");
            emitterModel.getEmitter().complete();
          }
        }
        // Check if the emitter should be destroyed due to inactivity.
        t = DateTime.now().minusMinutes(emitterConstants.getEmitterInactiveExpirationInMinutes());
        if (emitterModel.getLastSendTime().isBefore(t)) {
          log.info(
              "Emitter for user {} was destroyed due to {} minutes of inactivity.",
              userId,
              emitterConstants.getEmitterInactiveExpirationInMinutes());
          emitterModel.getEmitter().complete();
        }
      }
    }
  }

  public SseEmitter createEmitter(EmitterType type, UUID userId) {
    return createEmitter(type, userId, null);
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
   * @throws BadRequestException If the validator fails, this exception could be thrown.
   * @throws ForbiddenException If the validator fails, this exception could be thrown.
   */
  public SseEmitter createEmitter(EmitterType type, UUID userId, Runnable validator)
      throws BadRequestException, ForbiddenException {
    log.debug("Attempting to create {} emitter for {}.", type, userId);
    // Caller provides a lambda that will throw if pre-conditions are not satisfied.
    if (validator != null) {
      validator.run();
    }

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
    final DateTime now = DateTime.now();
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
  public final ApiSuccessModel sendUpdate(
      final EmitterType type, final UUID userId, final Object data) {
    try {
      getEmitter(type, userId).send(data);
      log.debug("{} data was sent to {}.", type, userId);
      updateEmitterModel(type, userId, DateTime.now(), data);
    } catch (IOException e) {
      log.error("{} emitter failed to send data to {} due to IOException.", type, userId);
    } catch (BadRequestException e) {
      log.error("{} emitter failed to send data to {} due to BadRequestException.", type, userId);
    } catch (ForbiddenException e) {
      log.error("{} emitter failed to send data to {} due to ForbiddenException.", type, userId);
    }
    return new ApiSuccessModel("Data sent successfully.");
  }

  /**
   * Sends data to all clients associated with emitters of the specified type.
   *
   * @param type The type of emitter to broadcast to.
   * @param data The data to send.
   */
  public final void sendToAll(final EmitterType type, final Object data) {
    getEmitterMap(type).keySet().forEach(id -> sendUpdate(type, id, data));
  }

  /**
   * Calls the complete method on the emitter, provided the emitter type specified is valid and
   * there is an emitter of the specified type, associated with the user ID provided.
   *
   * @param type The type of emitter being destroyed.
   * @param userId The user ID of the user the emitter is associated with.
   */
  public final ApiSuccessModel completeEmitter(final EmitterType type, final UUID userId) {
    try {
      getEmitter(type, userId).complete();
      // TODO: Investigate why the onComplete() lambda doesn't run (at least during unit tests).
      if (emitterMaps.get(type).get(userId) != null) {
        emitterMaps.get(type).remove(userId);
      }
    } catch (final Exception e) {
      log.error("Something went wrong removing {} emitter for {}.", type, userId);
    }
    return new ApiSuccessModel("Emitter has been removed.");
  }
}
