package com.poker.poker.services.game;

import com.poker.poker.config.AppConfig;
import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.models.game.GameModel;
import com.poker.poker.models.game.HandModel;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.events.CreateGameEvent;
import com.poker.poker.events.CurrentGameEvent;
import com.poker.poker.events.HandActionEvent;
import com.poker.poker.events.JoinGameEvent;
import com.poker.poker.events.LeaveGameEvent;
import com.poker.poker.events.PlayerAfkEvent;
import com.poker.poker.events.PublishMessageEvent;
import com.poker.poker.events.RejoinGameEvent;
import com.poker.poker.events.WaitForPlayerEvent;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.websocket.SocketContainerModel;
import com.poker.poker.models.enums.GamePhase;
import com.poker.poker.models.enums.MessageType;
import com.poker.poker.models.game.CardModel;
import com.poker.poker.models.game.GameParameterModel;
import com.poker.poker.models.game.DeckModel;
import com.poker.poker.models.game.DrawGameDataContainerModel;
import com.poker.poker.models.game.DrawGameDataModel;
import com.poker.poker.models.game.DrawGameDrawModel;
import com.poker.poker.models.game.GamePlayerModel;
import com.poker.poker.models.game.PlayerModel;
import com.poker.poker.models.websocket.CurrentGameModel;
import com.poker.poker.repositories.GameRepository;
import com.poker.poker.repositories.UserRepository;
import com.poker.poker.services.UuidService;
import com.poker.poker.services.WebSocketService;
import com.poker.poker.validation.exceptions.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GameService {

  private final AppConfig appConfig;

  private final UserRepository userRepository;

  /** Mapping of user Id to game Id. */
  private final Map<UUID, UUID> userIdToGameIdMap;

  /** Mapping of game Id to game document. */
  private final Map<UUID, GameModel> games;

  /** Mapping of game Id to game data. */
  private final Map<UUID, DrawGameDataContainerModel> gameIdToGameDataMap;

  private final GameConstants gameConstants;

  private final LobbyService lobbyService;

  private final UuidService uuidService;

  private final HandService handService;

  private final GameRepository gameRepository;

  private final ApplicationEventPublisher applicationEventPublisher;

  private final WebSocketService webSocketService;

  public CurrentGameModel getCurrentGameModel(final UUID userId) {
    final CurrentGameModel currentGameModel = new CurrentGameModel();
    if (userIdToGameIdMap.get(userId) != null
        && games.get(userIdToGameIdMap.get(userId)) != null
        && games.get(userIdToGameIdMap.get(userId)).getPhase() == GamePhase.Play) {
      currentGameModel.setId(games.get(userIdToGameIdMap.get(userId)).getId());
      currentGameModel.setInGame(true);
    } else {
      currentGameModel.setInGame(false);
      currentGameModel.setId(null);
    }
    return currentGameModel;
  }

  /**
   * Retrieves the game data needed for the client to display a summary of what has occurred in the
   * specified game.
   *
   * @param gameModel The specified game.
   * @return Data representing what has occurred in the game.
   */
  public DrawGameDataContainerModel getGameData(final GameModel gameModel) {
    return getGameData(gameModel.getId());
  }

  /**
   * Retrieves the game data needed for the client to display a summary of what has occurred in the
   * specified game.
   *
   * @param gameId The specified game.
   * @return Data representing what has occurred in the game.
   */
  public DrawGameDataContainerModel getGameData(final UUID gameId) {
    if (gameIdToGameDataMap.get(gameId) == null) {
      throw gameConstants.getGameDataNotFoundException();
    }
    return gameIdToGameDataMap.get(gameId);
  }

  /**
   * Returns information about a player.
   *
   * @param userId The ID of the player.
   * @return Model representing the player.
   */
  public GamePlayerModel getPlayerData(final UUID userId) {
    return getUsersGameModel(userId).getPlayers().stream()
        .filter(player -> player.getId().equals(userId))
        .findFirst()
        .orElseThrow(gameConstants::getPlayerDataNotFoundException);
  }

  /**
   * Initializes game data for the specified game, so the client can parse it easily.
   *
   * @param gameModel The specified game.
   */
  private void initializeGameData(final GameModel gameModel) {
    if (games.get(gameModel.getId()) == null) {
      throw gameConstants.getGameNotFoundException();
    }
    final List<DrawGameDataModel> gameData = new ArrayList<>(gameModel.getPlayers().size());
    for (final GamePlayerModel player : gameModel.getPlayers()) {
      final DrawGameDataModel drawGameDataModel =
          new DrawGameDataModel(player, false, new ArrayList<>(appConfig.getNumRoundsInRollGame()));
      for (int i = 0; i < appConfig.getNumRoundsInRollGame(); i++) {
        drawGameDataModel.getDraws().add(new DrawGameDrawModel(null, false, false));
      }
      gameData.add(drawGameDataModel);
    }
    // Set the first players status to active.
    gameData.get(0).setActing(true);
    // Set the first draw to acting so it is highlighted.
    gameData.get(0).getDraws().get(0).setActing(true);
    gameIdToGameDataMap.put(gameModel.getId(), new DrawGameDataContainerModel(gameData, 1));
  }

  /**
   * Helper that updates game data for the specified game. Specifically, sets the winner so the
   * player can be highlighted in some way.
   *
   * @param gameId The ID of the specified game.
   * @param player The player who won.
   * @param hand The hand the player won in.
   */
  private void setWinnerInGameData(final UUID gameId, final PlayerModel player, final int hand) {
    getGameData(gameId).incrementHand(appConfig.getNumRoundsInRollGame());
    getGameData(gameId).getGameData().stream()
        .filter(data -> data.getPlayer().getId().equals(player.getId()))
        .findFirst()
        .ifPresent(data -> data.getDraws().get(hand - 1).setWinner(true));
  }

  /**
   * Helper that updates game data for a specified game, after a player performs an action.
   *
   * @param gameId The ID of the specified game.
   * @param player The player that acted.
   * @param card The card the player drew.
   * @param hand The current hand of the game.
   */
  private void updateGameData(
      final UUID gameId, final GamePlayerModel player, final CardModel card, final int hand) {
    final List<DrawGameDataModel> gameData = getGameData(gameId).getGameData();
    int playerIndex = -1;
    for (int i = 0; i < gameData.size(); i++) {
      if (gameData.get(i).getPlayer().getId().equals(player.getId())) {
        playerIndex = i;
        break;
      }
    }

    // Make sure playerIndex was set.
    assert (playerIndex != -1);

    // The player has acted, so acting should be false and the card they drew should be filled in.
    gameData.get(playerIndex).setActing(false);
    gameData.get(playerIndex).getDraws().get(hand - 1).setActing(false);
    gameData.get(playerIndex).getDraws().get(hand - 1).setCard(card);

    // Also need to update the acting fields for the row and column.
    if (playerIndex == gameData.size() - 1 && hand < appConfig.getNumRoundsInRollGame()) {
      gameData.get(0).getDraws().get(hand).setActing(true);
      gameData.get(0).setActing(true);
    } else if (hand < appConfig.getNumRoundsInRollGame()) {
      gameData.get(playerIndex + 1).getDraws().get(hand - 1).setActing(true);
      gameData.get(playerIndex + 1).setActing(true);
    }
  }

  /**
   * Retrieves the game document for the game the specified user is in.
   *
   * @param userId ID of the user whose game document is being sought.
   * @return The game document for the game the specified user.
   * @throws BadRequestException If there is no game document associated with the specified user.
   */
  public GameModel getUsersGameModel(final UUID userId) throws BadRequestException {
    if (userIdToGameIdMap.get(userId) == null) {
      throw gameConstants.getNoUserIdToGameIdMappingFound();
    }
    if (games.get(userIdToGameIdMap.get(userId)) == null) {
      throw gameConstants.getGameNotFoundException();
    }
    return games.get(userIdToGameIdMap.get(userId));
  }

  /**
   * Retrieves the game document associated with the specified ID. Throws if game can't be found.
   *
   * @param gameId The specified ID.
   * @return GameDocument associated with the specified ID.
   * @throws BadRequestException If there is no game with the specified ID.
   */
  public GameModel getGameModel(final UUID gameId) throws BadRequestException {
    if (games.get(gameId) == null) {
      throw gameConstants.getGameNotFoundException();
    }
    return games.get(gameId);
  }

  /**
   * Listens for create game events, then creates the game.
   *
   * @param createGameEvent Event object with information required to create the game.
   * @throws BadRequestException If the request fails.
   */
  @EventListener
  public void createGame(final CreateGameEvent createGameEvent) throws BadRequestException {
    final GameParameterModel gameParameterModel = createGameEvent.getGameParameterModel();
    final UserDocument user = createGameEvent.getHost();
    if (userIdToGameIdMap.get(user.getId()) != null) {
      throw gameConstants.getCreateGamePlayerAlreadyInGameException();
    }

    // Create game document with state set to "Lobby"
    GameModel gameModel =
        new GameModel(
            UUID.randomUUID(),
            GamePhase.Lobby,
            new ArrayList<>(), // List of player models is only updated after game begins.
            new ArrayList<>(),
            appConfig.getNumRoundsInRollGame(),
            appConfig.getTimeToActInMillis() / 1000);

    userIdToGameIdMap.put(user.getId(), gameModel.getId());
    games.put(gameModel.getId(), gameModel);

    // Create the lobby.
    lobbyService.createLobby(gameParameterModel, user, gameModel.getId());

    applicationEventPublisher.publishEvent(
        new PublishMessageEvent<>(
            this,
            "/topic/game/create/" + user.getId(),
            new ApiSuccessModel(gameModel.getId().toString())));
  }

  /**
   * Event listener to handle an event triggered in HandService. Need an event listener here to
   * avoid circular dependency.
   *
   * @param playerAfkEvent Event being handled (contains reference to AFK player).
   */
  @Async
  @EventListener
  public void handlePlayerAfkEvent(final PlayerAfkEvent playerAfkEvent) {
    setPlayerActiveStatusInternal(playerAfkEvent.getPlayer().getId(), true);
  }

  /**
   * Transitions the specified player's "active" status to the value provided. When a player is
   * inactive, their moves are made for them as soon as it is their turn to act, rather than waiting
   * until the timer has elapsed.
   *
   * @param playerId The ID of the player whose status is being transitioned.
   * @param status The away status being set.
   */
  public ApiSuccessModel setPlayerActiveStatus(final UUID playerId, final boolean status) {
    setPlayerActiveStatusInternal(playerId, status);
    return new ApiSuccessModel("PLayers status changed successfully.");
  }

  /**
   * Internal implementation with no return value.
   *
   * @param playerId The ID of the player whose status is being transitioned.
   */
  private void setPlayerActiveStatusInternal(final UUID playerId, final boolean status) {
    final GameModel gameModel = getUsersGameModel(playerId);
    gameModel.getPlayers().stream()
        .filter(player -> player.getId().equals(playerId))
        .findFirst()
        .orElseThrow(gameConstants::getPlayerNotInGameException)
        .setAway(status);
    final UserDocument user = userRepository.findUserDocumentById(playerId);
    if (status && handService.getHand(user).getActing().getId().equals(playerId)) {
      handService.draw(user);
    }

    broadcastGameUpdate(gameModel);
    webSocketService.sendPublicMessage(
        "/topic/game/" + playerId,
        new SocketContainerModel(MessageType.PlayerData, getPlayerData(playerId)));

    log.debug("Updating away status to {} for player {}.", status, playerId);
  }

  /**
   * Once a game has started, a player can't be removed like they are when they leave a lobby.
   * Instead, their status is set to AFK mode. If the player rejoins, their status will be
   * transitioned from AFK mode to normal.
   *
   * @param leaveGameEvent Event that is dispatched when a user leaves a game.
   */
  @EventListener
  public void leaveGame(final LeaveGameEvent leaveGameEvent) {
    setPlayerActiveStatus(leaveGameEvent.getUser().getId(), true);
  }

  /**
   * If a player leaves a game that has started, they may be able to rejoin in. This method
   * determines whether the player can still rejoin and will perform the appropriate steps to add
   * them back into the game if they can.
   *
   * @param rejoinGameEvent Event that is dispatched when a user rejoins a game.
   */
  @EventListener
  public void rejoinGame(final RejoinGameEvent rejoinGameEvent) {
    setPlayerActiveStatus(rejoinGameEvent.getUser().getId(), false);
  }

  public void checkIfGameExists(final UUID gameId) {
    if (games.get(gameId) == null) {
      throw gameConstants.getGameNotFoundException();
    }
  }

  public void checkIfGameIsInLobbyState(final UUID gameId) {
    if (getGameModel(gameId).getPhase() != GamePhase.Lobby) {
      throw gameConstants.getCanOnlyJoinLobbyException();
    }
  }

  public boolean isUserInSpecifiedGame(final UUID gameId, final UUID userId) {
    return lobbyService.isUserInLobby(userId) && userIdToGameIdMap.get(userId).equals(gameId);
  }

  public void checkIfUserIsInGame(final UUID userId) {
    // Check if they're in a game. If they are, then throw.
    if (userIdToGameIdMap.get(userId) != null) {
      throw gameConstants.getJoinGamePlayerAlreadyJoinedException();
    }
  }

  /**
   * Listener that adds a player to a specified game.
   *
   * @param joinGameEvent Event that triggers the method.
   */
  @EventListener
  public void joinLobby(final JoinGameEvent joinGameEvent) {
    userIdToGameIdMap.put(
        joinGameEvent.getUser().getId(), getGameModel(joinGameEvent.getGameId()).getId());
  }

  /**
   * Removes specified user from a game lobby by reversing what was done when the player joined.
   *
   * @param user The user being removed.
   * @return An ApiSuccessModel indicating the request was successful.
   * @throws BadRequestException If the request is invalid.
   */
  public ApiSuccessModel removePlayerFromLobby(UserDocument user) throws BadRequestException {
    userIdToGameIdMap.remove(user.getId()); // Remove mapping.
    return lobbyService.removePlayerFromLobby(user); // Let LobbyService do the rest.
  }

  /**
   * Starts the game associated with the provided ID, provided all pre-conditions are satisfied.
   *
   * @param user The ID of the game to start.
   * @return An ApiSuccessModel indicating the request was successful.
   * @throws BadRequestException If some pre-condition was not satisfied, or something went wrong.
   */
  public ApiSuccessModel startGame(UserDocument user) throws BadRequestException {
    log.debug("User {} has attempted to start a game.", user.getId());
    final UUID gameId = userIdToGameIdMap.get(user.getId());
    if (games.get(gameId) == null) {
      throw gameConstants.getGameNotFoundException();
    }
    final GameModel gameModel = games.get(gameId);
    lobbyService.startGame(gameModel); // Lobby related housekeeping.
    gameModel.setPhase(GamePhase.Play); // Transition game state.
    handService.setDeck(gameId, new DeckModel()); // Give hand service the deck.
    handService.newHand(gameModel); // Create the hand.
    initializeGameData(gameModel); // Initialize game data for client.
    broadcastGameUpdate(gameModel); // Broadcast the game document.

    /*    TODO: I had an SSE broadcast here sending out player data to players[0] for some reason.
    Leaving this here in case this was actually needed. Will remove soon. */

    // Update Current game topic
    updateCurrentGameTopic(gameModel, false);

    return new ApiSuccessModel("The game has been started successfully.");
  }

  // TODO: Add docs
  public void updateCurrentGameTopic(final GameModel game, final boolean over) {
    game.getPlayers()
        .forEach(
            player ->
                applicationEventPublisher.publishEvent(
                    new CurrentGameEvent(
                        this,
                        player.getId(),
                        new CurrentGameModel(!over, over ? null : game.getId()))));
  }

  /**
   * Broadcasts the game data for the specified game to relevant players.
   *
   * @param gameModel The specified game.
   */
  public void broadcastGameDataUpdate(final GameModel gameModel) {
    // Broadcast to game topic
    webSocketService.sendPublicMessage(
        appConfig.getGameTopic() + gameModel.getId(),
        new SocketContainerModel(MessageType.GameData, getGameData(gameModel)));
  }

  /**
   * Broadcasts the specified game document to relevant players.
   *
   * @param gameModel The data being broadcast to players.
   */
  public void broadcastGameUpdate(final GameModel gameModel) {
    // Broadcast to game topic
    webSocketService.sendPublicMessage(
        appConfig.getGameTopic() + gameModel.getId(),
        new SocketContainerModel(MessageType.Game, gameModel));
  }

  /**
   * Asynchronous event listener that handles HandActionEvents.
   *
   * @param handActionEvent The event object representing a hand action.
   */
  @Async
  @EventListener
  public void handleHandActionEvent(final HandActionEvent handActionEvent) {
    log.debug("{} event detected by game service.", handActionEvent.getType());
    final GameModel gameModel = games.get(handActionEvent.getGameId());
    final HandModel hand = handService.getHand(handActionEvent.getHandId());
    final int playerThatActed = gameModel.getPlayers().indexOf(hand.getActing());
    final GamePlayerModel nextPlayerToAct =
        gameModel.getPlayers().get((playerThatActed + 1) % gameModel.getPlayers().size());

    // Update acting field and update players
    gameModel.getPlayers().get(playerThatActed).setActing(false);
    // Broadcast player data to game topic
    webSocketService.sendPublicMessage(
        appConfig.getGameTopic() + gameModel.getPlayers().get(playerThatActed).getId(),
        new SocketContainerModel(
            MessageType.PlayerData,
            getPlayerData(gameModel.getPlayers().get(playerThatActed).getId())));

    // Set acting to true for player who needs to act.
    nextPlayerToAct.setActing(true);
    // Broadcast player data to game topic
    webSocketService.sendPublicMessage(
        appConfig.getGameTopic() + nextPlayerToAct.getId(),
        new SocketContainerModel(MessageType.PlayerData, getPlayerData(nextPlayerToAct.getId())));

    // Set player to act:
    log.debug("Next player to act is {}.", nextPlayerToAct.getId());
    hand.setActing(nextPlayerToAct);

    // Broadcast updated hand model
    handService.broadcastHandUpdate(gameModel);

    /*
         Temporary logic here to help design game flow for later. Most of this will be gone. Just
         trying to work out the kinks with waiting for players to act, etc...
    */
    final boolean handOver = hand.getActing().equals(gameModel.getPlayers().get(0));
    final int currentRound =
        handOver ? 1 + gameModel.getHands().size() : gameModel.getHands().size();

    // Update game data needed for UI.
    updateGameData(
        gameModel.getId(),
        gameModel.getPlayers().get(playerThatActed),
        hand.getActions().get(hand.getActions().size() - 1).getDrawnCard(),
        gameModel.getHands().size());

    if (!handOver) {
      applicationEventPublisher.publishEvent(new WaitForPlayerEvent(this, nextPlayerToAct));
      broadcastGameDataUpdate(gameModel);
    } else if (currentRound <= gameModel.getTotalHands()) {
      log.debug("Hand ended. Beginning new hand.");
      // Update game data
      setWinnerInGameData(
          gameModel.getId(), handService.determineWinner(hand), gameModel.getHands().size());

      // Update scores
      handService.endHand(gameModel);
      handService.newHand(gameModel);
      broadcastGameUpdate(gameModel);
      broadcastGameDataUpdate(gameModel);
    } else {
      // If the round is over and the current round > total rounds, then the game must be over.
      log.debug("The game has ended.");
      setWinnerInGameData(
          gameModel.getId(), handService.determineWinner(hand), gameModel.getHands().size());
      endGame(gameModel);
    }
  }

  /**
   * Procedure that transitions the game to its final state, informs players that the game is over
   * and who won, provides some basic statistics about the game and performs a few housekeeping
   * tasks like removing relevant mappings, etc...
   *
   * @param gameModel The game document associated with the game that is ending.
   */
  public void endGame(GameModel gameModel) {
    handService.endHand(gameModel); // End hand.
    handService.removeDeck(gameModel);
    gameModel.setPhase(GamePhase.Over); // Transition game state.
    gameRepository.save(gameModel); // Save game document.
    games.remove(gameModel.getId()); // Remove mapping.
    gameModel.getPlayers().forEach(p -> userIdToGameIdMap.remove(p.getId())); // Remove mappings.
    broadcastGameDataUpdate(gameModel); // Broadcast final update.
    broadcastGameUpdate(gameModel); // Broadcast final update.
    gameIdToGameDataMap.remove(gameModel.getId()); // Remove mapping.
    updateCurrentGameTopic(gameModel, true);
  }
}
