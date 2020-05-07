package com.poker.poker.services;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.documents.GameDocument;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.enums.GameAction;
import com.poker.poker.models.enums.GameState;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.models.game.GameActionModel;
import com.poker.poker.models.game.GetGameModel;
import com.poker.poker.models.game.PlayerModel;
import com.poker.poker.validation.exceptions.BadRequestException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

  /**
   * A map of active games, keyed by the games ID.
   */
  private Map<UUID, GameDocument> activeGames;

  /**
   * A map of SSE emitters, keyed by the ID of the user associated with the emitter. These emitters
   * are used to send an updated GameDocument to players in a game, whenever something occurs in the
   * game.
   */
  private Map<UUID, SseEmitter> gameEmitters;

  /**
   * A map of game UUID's, keyed by user UUID's, to identify which game a user is currently in, and
   * also to identify whether a user is currently in a game, in order to impose a one game limit at
   * any given time.
   */
  private Map<UUID, UUID> userIdToGameIdMap;

  /**
   * A map of SSE emitters, keyed by user UUID's. These emitters are used to send an updated list of
   * GetGameModels whenever a player joins/leaves a game.
   */
  private Map<UUID, SseEmitter> joinGameEmitters;

  private AppConstants appConstants;
  private UuidService uuidService;

  private void cleanUpEmitters() {
  }

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
        log.debug(appConstants.getJoinGameSendingUpdate(), player.getId());
      } catch (IOException e) {
        log.error(appConstants.getJoinGameSendingUpdateFailed(), player.getId());
        log.error("Removing game emitter for player with ID: {}.",
            player.getId()); // TODO: make constant
        gameEmitters.remove(player.getId());
      } catch (NullPointerException e) {
        log.info("There is no game emitter for user with ID: {}.", player.getId());
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

    // Get the game document for the game now that we're sure it exists.
    GameDocument gameDocument = activeGames.get(userIdToGameIdMap.get(user.getId()));

    // Find the playerModel, throw if not found, otherwise, remove player from game.
    final Optional<PlayerModel> player = gameDocument
        .getPlayers()
        .stream()
        .filter(playerModel -> playerModel.getId().equals(user.getId()))
        .findFirst();
    if (!player.isPresent()) {
      log.error(appConstants.getPlayerReadyUnsuccessfulLog(), user.getId().toString());
      throw appConstants.getUserNotInGameException();
    }

    // Set players status to ready.
    player.get().setReady(true);

    // Add the appropriate game action model.
    gameDocument.getGameActions().add(new GameActionModel(
        player.get(),
        GameAction.Ready,
        String.format("%s %s is ready.", player.get().getFirstName(), player.get().getLastName())
    ));

    log.debug(appConstants.getPlayerReadySuccessfulLog(), user.getId().toString());

    sendGameDocumentToAllPlayers(gameDocument);
    return new ApiSuccessModel(appConstants.getPlayerReadySuccessful());
  }

  /**
   * Sends client an emitter which will send them updated game documents as events occur.
   *
   * @param userId User ID of the player requesting the emitter.
   * @return SseEmitter which will send updated game documents.
   */
  public SseEmitter getGameEmitter(UUID userId) {
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
    sseEmitter.onCompletion(() -> {
      log.debug("Game emitter for user {} is complete.", userId);
      gameEmitters.remove(userId);
    });
    sseEmitter.onTimeout(() -> {
      log.debug("Game emitter for user {} is timed out.", userId);
      gameEmitters.remove(userId);
      sseEmitter.complete();
    });
    sseEmitter.onError((ex) -> {
      log.debug("Game emitter for user {} encountered an error.", userId);
      activeGames.remove(userId);
      ex.printStackTrace();
    });

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

  // TODO: Add docs.
  public ApiSuccessModel getGameListUpdate(UUID userId) {
    SseEmitter emitter = joinGameEmitters.get(userId);
    if (emitter == null) {
      throw appConstants.getEmitterFailToSendException();
    }
    try {
      emitter.send(getGameList());
    } catch (IOException e) {
      log.error(appConstants.getFailedToSendGameListLog());
      log.error("Removing emitter for player with ID: {}.", userId);
      joinGameEmitters.remove(userId);
    }
    // TODO: Add app constant.
    return new ApiSuccessModel("Updated game lists sent successfully");
  }

  /**
   * Creates a new game document based on attributes given in createGameModel.
   *
   * @param createGameModel A model containing: name, maximum players, and buy in.
   * @param user            the user document of the player creating the game.
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
            new ArrayList<>(Arrays.asList(
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
    sendUpdatedGetGameLists();
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
                gd.getPlayers()
                    .stream()
                    .filter(p -> p.getId().equals(gd.getHost()))
                    .findFirst()
                    .get(),
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
    final PlayerModel playerModel = new PlayerModel(user, false, false);
    gameDocument.getPlayers().add(playerModel);

    // Add the appropriate game action model.
    gameDocument.getGameActions().add(new GameActionModel(
        playerModel,
        GameAction.Join,
        String.format(
            "%s %s has joined the game.",
            playerModel.getFirstName(),
            playerModel.getLastName())
    ));
    userIdToGameIdMap.put(user.getId(), gameDocument.getId());

    // Update all players copy of gameDocument who are in the game via SSE
    for (PlayerModel player : gameDocument.getPlayers()) {
      if (!player.getId().equals(user.getId())) {
        SseEmitter emitter = gameEmitters.get(player.getId());
        try {
          log.debug(appConstants.getJoinGameSendingUpdate(), player.getId());
          emitter.send(gameDocument);
        } catch (IOException e) {
          log.error(appConstants.getJoinGameSendingUpdateFailed(), player);
          log.error("Removing game emitter for player with ID: {}.", player.getId());
          gameEmitters.remove(player.getId());
        } catch (NullPointerException e) {
          log.info("There is no game emitter for user with ID: {}.", player.getId());
        }
      }
    }

    sendUpdatedGetGameLists();
    return new ApiSuccessModel(appConstants.getJoinGameJoinSuccessful());
  }

  /**
   * Removes a player from a game. This will be called when a player leaves, or when the host kicks
   * a player from the game.
   *
   * @param user The user to be removed.
   * @return
   */
  public ApiSuccessModel removePlayerFromGame(UserDocument user) {
    UUID gameId = userIdToGameIdMap.get(user.getId());
    if (gameId == null) {
      throw appConstants.getLeaveGameException();
    }

    GameDocument game = activeGames.get(gameId);
    if (game == null) {
      throw appConstants.getLeaveGameException();
    }

    // Find the playerModel, throw if not found, otherwise, remove player from game.
    final Optional<PlayerModel> player = game
        .getPlayers()
        .stream()
        .filter(playerModel -> playerModel.getId().equals(user.getId()))
        .findFirst();
    if (!player.isPresent()) {
      throw appConstants.getLeaveGameException();
    }
    game.getPlayers().removeIf(playerModel -> playerModel.getId().equals(user.getId()));

    if (game.getPlayers().size() > 0) {
      // Select another host.
      game.setHost(game.getPlayers().get(0).getId());
      log.debug("Changing host from: {}, to: {}.", user.getId(), game.getHost()); // TODO: Make constant
    } else {
      // The game is empty, so remove it.
      activeGames.remove(game.getId());
      log.debug("No players left in game: {}, removing game.", game.getId()); // TODO: Make constant
    }

    // Remove user from mapping of user ID to game ID.
    userIdToGameIdMap.remove(user.getId());
    log.debug("Player with ID: {}, has left game with ID: {}.", user.getId(), game.getId()); // TODO: Make constant


    // Destroy the emitter sending this user updates.
    try {
      gameEmitters.remove(user.getId()).complete();
    } catch (Exception e) {
      log.error("Issue removing the game emitter when player with ID: {}, left game (player may"
          + " not have had an emitter for some reason).", user.getId());
    }

    // Add GameActionModel with the appropriate action.
    game.getGameActions().add(new GameActionModel(
        player.get(),
        GameAction.Leave,
        String.format(
            "%s %s has left the game.",
            player.get().getFirstName(),
            player.get().getLastName())));

    // Update the players in the game, and players who are on the join game page.
    sendGameDocumentToAllPlayers(game);
    sendUpdatedGetGameLists();

    return new ApiSuccessModel(appConstants.getPlayerHasLeftMessage());
  }

  /**
   * Creates and returns an SSE emitter which will send the client updated lists of games for the
   * join game page.
   *
   * @param userId The ID of the user the emitter is being sent to.
   * @return SseEmitter used to send client an updated list of games.
   */
  public SseEmitter getJoinGameEmitter(UUID userId) {
    // TODO: Maybe add a check to ensure there isn't already an emitter for this user.
    SseEmitter sseEmitter = new SseEmitter(appConstants.getJoinGameEmitterDuration());
    sseEmitter.onCompletion(() -> {
      log.debug("Join game emitter for {} is complete.", userId);
      joinGameEmitters.remove(userId);
    });
    sseEmitter.onTimeout(() -> {
      log.debug("Join game emitter for {} timed out.", userId);
      joinGameEmitters.remove(userId);
      sseEmitter.complete();
    });
    sseEmitter.onError((ex) -> {
      log.debug("Join game emitter for {} encountered an error. Should be removed.", userId);
      joinGameEmitters.remove(userId);
      sseEmitter.complete();
    });

    joinGameEmitters.put(userId, sseEmitter);
    log.debug("Giving join game emitter to user with ID: {}.", userId);
    return sseEmitter;
  }

  public ApiSuccessModel completeGameEmitter(UUID userId) {
    if (gameEmitters.get(userId) == null) {
      throw appConstants.getNoEmitterForIdException();
    }
    gameEmitters.get(userId).complete();
    return new ApiSuccessModel(appConstants.getEmitterCompleteSuccess());
  }

  /**
   * When a player leaves the join game page, we don't want to maintain the emitter. This method
   * will destroy it by calling the complete method.
   *
   * @param userId User ID of the player whose emitter will be destroyed.
   * @return
   */
  public ApiSuccessModel completeJoinGameEmitter(UUID userId) {
    if (joinGameEmitters.get(userId) == null) {
      throw appConstants.getNoEmitterForIdException();
    }
    // .complete() should remove the emitter from the map.
    joinGameEmitters.get(userId).complete();
    log.debug("Removing join game emitter for user with ID: {}.", userId);    // TODO: ADD CONSTANTS

    /*
          TODO: It seems like the .complete() is running on a different thread, and thus, the
           emitter is not being removed from the hash map before this log which outputs all the
           emitters in the hash map, is outputted. The emitter is being removed, it's just
           occurring AFTER the log. Should investigate some way of ensuring the emitter is removed
           before logging. Probably can use a semaphore or some kind of loop with a thread.sleep.
     */
    log.debug("Current join game emitters: {}.", joinGameEmitters);
    return new ApiSuccessModel(appConstants.getEmitterCompleteSuccess());
  }

  /**
   * Helper method that sends updated game lists to clients viewing the join game page, whenever a a
   * player joins, leaves, or is kicked from, a game.
   */
  private void sendUpdatedGetGameLists() {
    for (UUID id : joinGameEmitters.keySet()) {
      try {
        SseEmitter emitter = joinGameEmitters.get(id);
        emitter.send(getGameList());
      } catch (IOException e) {
        log.error(appConstants.getFailedToSendGameListLog());
        log.error("Removing join game emitter for player with ID: {}.", id);
        joinGameEmitters.remove(id);
      }
    }
  }
}
