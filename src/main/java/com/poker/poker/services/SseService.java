package com.poker.poker.services;

import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.models.enums.EmitterType;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SseService {

  private final GameConstants gameConstants;

  /**
   * A map of emitter maps, keyed by the emitter type. Each emitter map is keyed by UUID's which
   * are associated to a specific user. The getEmitterMap method can be used to retrieve the
   * appropriate map and a UUID can be used to send data to a specific user.
   */
  private Map<EmitterType, Map<UUID, SseEmitter>> emitterMaps;

  /**
   * Constructor that injects game constants and sets up the emitter map appropriately.
   * @param gameConstants Constants required for events which occur in the game.
   */
  public SseService(GameConstants gameConstants) {
    this.gameConstants = gameConstants;
    emitterMaps = new HashMap<>();
    emitterMaps.put(EmitterType.GameList, new HashMap<>());
    emitterMaps.put(EmitterType.Lobby, new HashMap<>());
  }

  /**
   * Helper which will return the appropriate emitter map using the type provided, or throw if the
   * type is invalid.
   * @param type The type of emitter map specified.
   * @return Emitter map with the specified type.
   */
  private Map<UUID, SseEmitter> getEmitterMap(EmitterType type) {
    final Map<UUID, SseEmitter> emitterMap = emitterMaps.get(type);
    if (emitterMaps.get(type) == null) {
      log.error("Invalid emitter type specified.");
      throw gameConstants.getInvalidEmitterTypeException();
    }
    return emitterMap;
  }

  /**
   * Helper which returns the emitter timeout value from constants class based on the type
   * specified.
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
   * @param type The type of emitter being sought.
   * @param userId The user the emitter being sought is associated with.
   * @return An SseEmitter of the specified type, associated with the specified user, provided such
   * an emitter exists. If no such emitter exists, then a BadRequestException is thrown.
   */
  private SseEmitter getEmitter(EmitterType type, UUID userId) {
    log.debug("Attempting to retrieved {} emitter for {}.", type, userId);
    final Map<UUID, SseEmitter> map = getEmitterMap(type);
    final SseEmitter emitter = map.get(userId);
    if (emitter == null) {
      log.error("There is no {} emitter associated with the user ID: {}.", type, userId);
      throw gameConstants.getNoEmitterForIdException();
    }
    log.debug("{} emitter retrieved for {} successfully.", type, userId);
    return emitter;
  }

  /**
   * Creates, stores and returns an emitter which will be used to send the client updates.
   *
   * @param type      The type of the emitter. There are several types, such as a lobby emitter,
   *                  which will send the client updates whenever the state of the game lobby
   *                  changes.
   * @param userId    The UUID of the user requesting an emitter.
   * @param validator A lambda supplied by the caller to ensure an emitter is only sent when certain
   *                  preconditions have been satisfied.
   * @return An SSE emitter which will send updates to the client that requested the emitter.
   */
  public SseEmitter createEmitter(EmitterType type, UUID userId, Runnable validator) {
    log.debug("Attempting to create {} emitter for {}.", type, userId);
    // Caller provides a lambda that will throw if pre-conditions are not satisfied.
    validator.run();

    final SseEmitter emitter;
    final Map<UUID, SseEmitter> emitterMap = getEmitterMap(type);

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

    log.debug("Sending {} emitter to {}.", type, userId);
    return emitter;
  }

  /**
   * Sends data to the client associated with the userId specified, using the type of emitter
   * specified.
   * @param type The type of emitter to use in order to send the client data.
   * @param userId The ID of the user the data should be sent to.
   * @param data The data that should be sent to the client.
   */
  public void sendUpdate(EmitterType type, UUID userId, Object data) {
    log.debug("Attempting to use {} emitter to send data to {}.", type, userId);
    try {
      getEmitter(type, userId).send(data);
      log.debug("{} data appears to have been sent successfully to {}.", type, userId);
    } catch (IOException e) {
      log.error("{} emitter failed to send data to {} due to IOException.", type, userId);
    }
  }

  /**
   * Calls the complete method on the emitter, provided the emitter type specified is valid and
   * there is an emitter of the specified type, associated with the user ID provided.
   *
   * @param type   The type of emitter being destroyed.
   * @param userId The user ID of the user the emitter is associated with.
   */
  public void completeEmitter(EmitterType type, UUID userId) {
    log.debug("Attempting to call complete() on {} emitter for {}.", type, userId);
    getEmitter(type, userId).complete();
  }
}
