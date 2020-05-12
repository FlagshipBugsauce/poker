package com.poker.poker.services.game;

import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.documents.GameDocument;
import com.poker.poker.documents.LobbyDocument;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.enums.EmitterType;
import com.poker.poker.models.enums.GameState;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.models.game.HandModel;
import com.poker.poker.models.game.PlayerModel;
import com.poker.poker.repositories.LobbyRepository;
import com.poker.poker.services.SseService;
import com.poker.poker.services.UuidService;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GameService {

  private Map<UUID, UUID> userIdToGameIdMap;

  private Map<UUID, GameDocument> games;

  private GameConstants gameConstants;

  private SseService sseService;

  private LobbyService lobbyService;

  private UuidService uuidService;

  private LobbyRepository lobbyRepository;

  // TODO: Add javadoc
  public GameDocument getUsersGameDocument(UUID userId) {
    // TODO: May want to add validation here.
    return games.get(userIdToGameIdMap.get(userId));
  }

  /**
   * Creates a new game.
   *
   * @param createGameModel Model representing the game parameters.
   * @param user The user who created the game.
   * @return An ApiSuccessModel containing the ID of the game, to indicate creation was successful.
   */
  public ApiSuccessModel createGame(CreateGameModel createGameModel, UserDocument user) {
    if (userIdToGameIdMap.get(user.getId()) != null) {
      // TODO: Might be irrelevant exception
      throw gameConstants.getJoinGamePlayerAlreadyJoinedException();
    }

    // Create game document with state set to "Lobby"
    GameDocument gameDocument =
        new GameDocument(
            UUID.randomUUID(),
            GameState.Lobby,
            new ArrayList<>(), // List of player models is only updated after game begins.
            new ArrayList<>()); // List of hands is empty until the game starts.

    userIdToGameIdMap.put(user.getId(), gameDocument.getId());
    games.put(gameDocument.getId(), gameDocument);

    // Create the lobby.
    lobbyService.createLobby(createGameModel, user, gameDocument.getId());
    return new ApiSuccessModel(gameDocument.getId().toString());
  }

  public ApiSuccessModel joinGame(String gameIdString, UserDocument user) {
    uuidService.checkIfValidAndThrowBadRequest(gameIdString);

    // Check that game exists
    if (games.get(UUID.fromString(gameIdString)) == null) {
      throw gameConstants.getGameNotFoundException();
    }

    ApiSuccessModel response;
    GameDocument gameDocument = games.get(UUID.fromString(gameIdString));

    if (gameDocument.getState() == GameState.Lobby) {
      // If game is in lobby, then user can only join if they're not in ANY other games

      // First let's check if they're trying to join a lobby they're already in, don't want to throw
      // OR proceed further, if this is the case.
      if (lobbyService.isUserInLobby(user.getId())
          && userIdToGameIdMap.get(user.getId()).equals(gameDocument.getId())) {
        return new ApiSuccessModel("Player is already in the game.");
      }
      // Now let's check if they're in a game. If they are, then throw.
      if (userIdToGameIdMap.get(user.getId()) != null) {
        // TODO: Might be irrelevant exception
        throw gameConstants.getJoinGamePlayerAlreadyJoinedException();
      }

      // Now, let's call lobbyService and say the user wants to join.
      response = lobbyService.joinLobby(gameDocument.getId(), user);
      userIdToGameIdMap.put(user.getId(), gameDocument.getId());
    } else if (gameDocument.getState() == GameState.Play) {
      // Joining will be different if the game has already started.
      // TODO: Handle logic for rejoining here.
      // TODO: Remove this exception.
      throw gameConstants.getGameNotFoundException();
    } else {
      // TODO: Change this to relevant exception.
      throw gameConstants.getGameNotFoundException();
    }

    return response;
  }

  public ApiSuccessModel removePlayerFromLobby(UserDocument user) {
    userIdToGameIdMap.remove(user.getId());

    // Destroy the game emitter sending the player updates.
    try {
      sseService.completeEmitter(EmitterType.Game, user.getId());
    } catch (Exception e) {
      log.error("Issue removing the game emitter when player with ID: {}.", user.getId());
    }
    return lobbyService.removePlayerFromLobby(user);
  }

  /**
   * Starts the game associated with the provided ID, provided all pre-conditions are satisfied.
   *
   * @param user The ID of the game to start.
   */
  public ApiSuccessModel startGame(UserDocument user) {
    log.debug("User {} has attempted to start a game.", user.getId());
    // Transitions the games state from Lobby -> Play
    // TODO: Add some validation to ensure game exists, etc...
    // Some validation to ensure gameId is valid
    UUID gameId = userIdToGameIdMap.get(user.getId());
    LobbyDocument lobbyDocument = lobbyService.startGame(gameId);
    lobbyRepository.save(lobbyDocument);

    // Send updated game lists
    sseService.sendToAll(EmitterType.GameList, lobbyService.getLobbyList());
    GameDocument gameDocument = games.get(gameId);

    // Set the players
    gameDocument.setPlayers(lobbyDocument.getPlayers());
    gameDocument.setState(GameState.Play);

    // Complete the Lobby emitter.
    // Send update so client knows the game state has changed.
    gameDocument
        .getPlayers()
        .forEach(
            player -> {
              try {
                sseService.completeEmitter(EmitterType.Lobby, player.getId());
                sseService.sendUpdate(EmitterType.Game, player.getId(), gameDocument);
              } catch (Exception ignore) {
              }
            });

    log.debug(gameDocument.getPlayers().toString());

    // Start the first hand.
    startNewHand(gameId);

    return new ApiSuccessModel("The game has been started successfully.");
  }

  public void startNewHand(UUID gameId) {
    log.debug("Hand #X of game {} has started.", gameId);
    // Games occur in "hands" (rounds).
    // Each "hand" will provide a new SSE for the player, will be tracked on
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    GameDocument gameDocument = games.get(gameId);

    // Temporary
    final PlayerModel winner = gameDocument
        .getPlayers()
        .get((int) (Math.random() * gameDocument.getPlayers().size()));
    final String winnerMessage = String.format(
        "%s %s won the game.",
        winner.getFirstName(),
        winner.getLastName());

    gameDocument.getPlayers().forEach(player -> {
      try {
        HandModel hand = new HandModel(UUID.randomUUID(), gameDocument.getId(), winnerMessage);
        sseService.sendUpdate(EmitterType.Hand, player.getId(), hand);
      } catch (Exception ignore) {}
    });
  }
}
