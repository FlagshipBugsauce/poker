package com.poker.poker.services;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.documents.GameDocument;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.enums.GameState;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.models.game.GetGameModel;
import com.poker.poker.models.game.PlayerModel;
import com.poker.poker.validation.exceptions.BadRequestException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@AllArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GameService {
  /** A map of active games, keyed by the games ID. */
  private Map<UUID, GameDocument> activeGames;

  /** A map of SSE emitter's, keyed by the ID of the user associated with the emitter. */
  private Map<UUID, SseEmitter> gameEmitters;

  /**
   * A map of game UUID's, keyed by user UUID's, to identify which game a user is currently in, and
   * also to identify whether a user is currently in a game, in order to impose a one game limit at
   * any given time.
   */
  private Map<UUID, UUID> userIdToGameIdMap;

  private AppConstants appConstants;
  private UuidService uuidService;

  /**
   * Throws a BadRequestException if there is a user with the user ID provided currently in a game.
   *
   * @param userId User ID that is checked.
   */
  private void checkIfUserIsInGameAndThrow(UUID userId) {
    if (userIdToGameIdMap.get(userId) != null) {
      throw appConstants.getJoinGamePlayerAlreadyJoinedException();
    }
  }

  /**
   * Sends out a game document to all players in the game associated with the game document
   * argument.
   *
   * @param gameDocument GameDocument representing the game that is being updated.
   */
  private void sendGameDocumentToAllPlayers(GameDocument gameDocument) {
    for (PlayerModel player : gameDocument.getPlayers()) {
      try {
        gameEmitters.get(player.getId()).send(gameDocument);
        log.debug(appConstants.getJoinGameSendingUpdate(), player);
      } catch (IOException | NullPointerException e) {
        log.error(appConstants.getJoinGameSendingUpdateFailed(), player);
      }
    }
  }

  /**
   * Updates the game document associated with the game a player is in, to indicate that the player
   * is ready for the game to start.
   *
   * @param user The user who is ready.
   * @return ApiSuccessModel indicating the request was successful.
   */
  public ApiSuccessModel ready(UserDocument user) {
    if (userIdToGameIdMap.get(user.getId()) == null
        || activeGames.get(userIdToGameIdMap.get(user.getId())) == null) {
      log.error(appConstants.getPlayerReadyUnsuccessfulLog(), user.getId().toString());
      throw appConstants.getUserNotInGameException();
    }

    boolean found = false;
    for (PlayerModel player : activeGames.get(userIdToGameIdMap.get(user.getId())).getPlayers()) {
      if (player.getId().equals(user.getId())) {
        player.setReady(true);
        found = true;
      }
    }
    if (!found) {
      log.error(appConstants.getPlayerReadyUnsuccessfulLog(), user.getId().toString());
      throw appConstants.getUserNotInGameException();
    }
    log.debug(appConstants.getPlayerReadySuccessfulLog(), user.getId().toString());
    sendGameDocumentToAllPlayers(activeGames.get(userIdToGameIdMap.get(user.getId())));
    return new ApiSuccessModel(appConstants.getPlayerReadySuccessful());
  }

  /**
   * Sends client an emitter which will send them updated game documents as events occur.
   *
   * @param userId User ID of the player requesting the emitter.
   * @return SseEmitter which will send updated game documents.
   */
  public SseEmitter getGameEmitter(UUID userId) {
    //    try { // Need to pause for a short time, was getting exception otherwise.
    //      Thread.sleep(10); // TODO: See if exception still happens after changing router redirect
    // to occur after join response is received.
    //    } catch (InterruptedException e) {
    //      e.printStackTrace();
    //    }
    // Check that user is actually in a game before giving them an emitter.
    if (userIdToGameIdMap.get(userId) == null) {
      throw appConstants.getUserNotInGameException();
    }

    // Check the game document to make sure they are considered a player.
    GameDocument gameDocument = activeGames.get(userIdToGameIdMap.get(userId));
    if (gameDocument == null) {
      log.error("");
      throw appConstants.getInvalidUuidException();
    }

    boolean found = false;
    for (PlayerModel player : gameDocument.getPlayers()) {
      if (player.getId().equals(userId)) {
        found = true;
        break;
      }
    }
    if (!found) {
      throw appConstants.getGetGameEmitterPlayerNotInGameException();
    }

    SseEmitter sseEmitter = new SseEmitter(appConstants.getGameEmitterDuration());
    gameEmitters.put(userId, sseEmitter);

    return sseEmitter;
  }

  /**
   * Sometimes a client will want to manually request an updated game document. In cases such as
   * these, this service will send an update to the client.
   *
   * @param userId ID of the user in need of the updated game document.
   * @return ApiSuccessModel indicating that the request was successful.
   */
  public ApiSuccessModel getGameDocumentUpdate(UUID userId) {
    SseEmitter emitter = gameEmitters.get(userId);
    GameDocument gameDocument = activeGames.get(userIdToGameIdMap.get(userId));
    if (emitter == null || gameDocument == null) {
      log.error(appConstants.getGetGameDocumentErrorLog(), userId.toString());
      throw appConstants.getEmitterFailToSendException();
    }
    try {
      emitter.send(gameDocument);
    } catch (IOException e) {
      e.printStackTrace();
      log.error(appConstants.getGetGameDocumentErrorLog(), userId.toString());
      throw new BadRequestException(
          appConstants.getEmitterFailToSendExceptionErrorType(),
          Arrays.toString(e.getStackTrace()));
    }
    return new ApiSuccessModel(appConstants.getUpdatedGameDocumentSentSuccessfully());
  }

  /**
   * Creates a new game document based on attributes given in createGameModel.
   *
   * @param createGameModel A model containing: name, maximum players, and buy in.
   * @param user the UUID of the client.
   * @return a UUID, the unique id for the game document created in this method.
   */
  public ApiSuccessModel createGame(CreateGameModel createGameModel, UserDocument user) {
    checkIfUserIsInGameAndThrow(user.getId());
    GameDocument gameDocument =
        new GameDocument(
            UUID.randomUUID(),
            user.getId(),
            createGameModel.getName(),
            createGameModel.getMaxPlayers(),
            createGameModel.getBuyIn(),
            new ArrayList<>(
                Arrays.asList(
                    new PlayerModel(
                        user.getId(),
                        user.getEmail(),
                        user.getGroup(),
                        user.getFirstName(),
                        user.getLastName(),
                        false,
                        true))),
            new ArrayList<>(),
            GameState.PreGame);
    log.info(appConstants.getGameCreation(), user.getId());
    activeGames.put(gameDocument.getId(), gameDocument);
    userIdToGameIdMap.put(user.getId(), gameDocument.getId());
    return new ApiSuccessModel(gameDocument.getId().toString());
  }

  /**
   * Gets the list of active game models that are currently in the PreGame state.
   *
   * @return An ActiveGameModel which is a subset of a game document.
   */
  public List<GetGameModel> getGameList() {
    List<GetGameModel> activeGameModels = new ArrayList<>();
    for (GameDocument gd : activeGames.values()) {
      // Games are only join-able in the PreGame game state
      if (gd.getCurrentGameState() == GameState.PreGame) {
        activeGameModels.add(
            new GetGameModel(
                gd.getId(),
                gd.getName(),
                gd.getHost(),
                gd.getPlayers().size(),
                gd.getMaxPlayers(),
                gd.getBuyIn()));
      }
    }
    return activeGameModels;
  }

  /**
   * Join a game by adding the clients UUID to the list of player ids and updating all the other
   * players game documents in the game.
   *
   * @param gameId The UUID of the game the player wishes to join.
   * @param user The UserDocument associated with the player attempting to join.
   * @return An ApiSuccessModel containing a message which indicates the attempt to join was
   *     successful.
   */
  public ApiSuccessModel joinGame(String gameId, UserDocument user) {
    // Make sure the game ID is a valid UUID.
    uuidService.checkIfValidAndThrowBadRequest(gameId);

    // Check if user is already in the current game.
    if (userIdToGameIdMap.get(user.getId()) != null
        && userIdToGameIdMap.get(user.getId()).equals(UUID.fromString(gameId))) {
      return new ApiSuccessModel(appConstants.getJoinGamePlayerAlreadyInGameResponse());
    }
    // If user isn't in the game with ID = gameId, then throw.
    checkIfUserIsInGameAndThrow(user.getId());

    // Find the active game you wish to join
    GameDocument gameDocument = activeGames.get(UUID.fromString(gameId));

    // Return a bad request status if there is no game with ID provided.
    if (gameDocument == null) {
      throw appConstants.getInvalidUuidException();
    }

    // Add new player to list of players in currently in the game.
    gameDocument
        .getPlayers()
        .add(
            new PlayerModel(
                user.getId(),
                user.getEmail(),
                user.getGroup(),
                user.getFirstName(),
                user.getLastName(),
                false,
                false));
    userIdToGameIdMap.put(user.getId(), gameDocument.getId());

    // Update all players copy of gameDocument who are in the game via SSE
    for (PlayerModel player : gameDocument.getPlayers()) {
      if (!player.getId().equals(user.getId())) {
        SseEmitter emitter = gameEmitters.get(player.getId());
        try {
          log.debug(appConstants.getJoinGameSendingUpdate(), player);
          emitter.send(gameDocument);
        } catch (IOException | NullPointerException e) {
          log.error(appConstants.getJoinGameSendingUpdateFailed(), player);
        }
      }
    }

    return new ApiSuccessModel(appConstants.getJoinGameJoinSuccessful());
  }
}
