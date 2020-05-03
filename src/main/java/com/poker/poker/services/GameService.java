package com.poker.poker.services;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.documents.GameDocument;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.enums.GameState;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.models.game.GetGameModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
   * A set of players who are currently in a game. This is used to ensure that players can't join
   * multiple games.
   */
  private Set<UUID> playersInGames;

  private AppConstants appConstants;
  private UuidService uuidService;

  /**
   * Creates a new game document based on attributes given in createGameModel.
   *
   * @param createGameModel A model containing: name, maximum players, and buy in.
   * @param userId the UUID of the client.
   * @return a UUID, the unique id for the game document created in this method.
   */
  public ApiSuccessModel createGame(CreateGameModel createGameModel, UUID userId) {
    // TODO: Make sure a user can't create a game if they are already in a game.
    GameDocument gameDocument =
        new GameDocument(
            UUID.randomUUID(),
            userId,
            createGameModel.getName(),
            createGameModel.getMaxPlayers(),
            createGameModel.getBuyIn(),
            new ArrayList<>(Arrays.asList(userId)),
            new ArrayList<>(),
            GameState.PreGame);
    log.info(appConstants.getGameCreation(), userId);
    activeGames.put(gameDocument.getId(), gameDocument);
    playersInGames.add(userId);
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
                gd.getPlayerIds().size(),
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
   * @param gameId The UUID of the game the client wishes to join.
   * @param userId The UUID of the client.
   * @return An ApiSuccessModel containing a message which indicates the attempt to join was
   *     successful.
   */
  public ApiSuccessModel joinGame(String gameId, UUID userId) {
    // Make sure the game ID is a valid UUID.
    uuidService.checkIfValidAndThrowBadRequest(gameId);

    // Make sure player is not already in a game.
    if (playersInGames.contains(userId)) {
      throw appConstants.getJoinGamePlayerAlreadyJoinedException();
    }

    // Find the active game you wish to join
    GameDocument gameDocument = activeGames.get(UUID.fromString(gameId));

    // Return a bad request status if there is no game with ID provided.
    if (gameDocument == null) {
      throw appConstants.getInvalidUuidException();
    }

    // Update all players copy of gameDocument who are in the game via SSE
    for (UUID id : gameDocument.getPlayerIds()) {
      SseEmitter emitter = gameEmitters.get(id);
      try {
        log.info(appConstants.getJoinGameSendingUpdate(), id);
        emitter.send(gameDocument);
      } catch (IOException | NullPointerException e) {
        log.error(appConstants.getJoinGameSendingUpdateFailed(), id);
      }
    }

    // Add new player to list of players in currently in the game.
    gameDocument.getPlayerIds().add(userId);
    playersInGames.add(userId);
    return new ApiSuccessModel(appConstants.getJoinGameJoinSuccessful());
  }
}
