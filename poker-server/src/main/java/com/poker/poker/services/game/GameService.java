package com.poker.poker.services.game;

import com.poker.poker.config.AppConfig;
import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.documents.GameDocument;
import com.poker.poker.documents.HandDocument;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.events.CreateGameEvent;
import com.poker.poker.events.CurrentGameEvent;
import com.poker.poker.events.HandActionEvent;
import com.poker.poker.events.LeaveGameEvent;
import com.poker.poker.events.PlayerAfkEvent;
import com.poker.poker.events.PublishMessageEvent;
import com.poker.poker.events.RejoinGameEvent;
import com.poker.poker.events.WaitForPlayerEvent;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.GameSummaryModel;
import com.poker.poker.models.SocketContainerModel;
import com.poker.poker.models.enums.GameState;
import com.poker.poker.models.enums.MessageType;
import com.poker.poker.models.game.CardModel;
import com.poker.poker.models.game.CreateGameModel;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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
  private final Map<UUID, GameDocument> games;

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
        && games.get(userIdToGameIdMap.get(userId)).getState() == GameState.Play) {
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
   * @param gameDocument The specified game.
   * @return Data representing what has occurred in the game.
   */
  public DrawGameDataContainerModel getGameData(final GameDocument gameDocument) {
    return getGameData(gameDocument.getId());
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
    return getUsersGameDocument(userId).getPlayers().stream()
        .filter(player -> player.getId().equals(userId))
        .findFirst()
        .orElseThrow(gameConstants::getPlayerDataNotFoundException);
  }

  /**
   * Initializes game data for the specified game, so the client can parse it easily.
   *
   * @param gameDocument The specified game.
   */
  private void initializeGameData(final GameDocument gameDocument) {
    if (games.get(gameDocument.getId()) == null) {
      throw gameConstants.getGameNotFoundException();
    }
    final List<DrawGameDataModel> gameData = new ArrayList<>(gameDocument.getPlayers().size());
    for (final GamePlayerModel player : gameDocument.getPlayers()) {
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
    gameIdToGameDataMap.put(gameDocument.getId(), new DrawGameDataContainerModel(gameData, 1));
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
  public GameDocument getUsersGameDocument(final UUID userId) throws BadRequestException {
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
  public GameDocument getGameDocument(final UUID gameId) throws BadRequestException {
    if (games.get(gameId) == null) {
      throw gameConstants.getGameNotFoundException();
    }
    return games.get(gameId);
  }

  /**
   * Listens for create game events, then creates the game.
   * @param createGameEvent Event object with information required to create the game.
   * @throws BadRequestException If the request fails.
   */
  @EventListener
  public void createGame(final CreateGameEvent createGameEvent)
      throws BadRequestException {
    final CreateGameModel createGameModel = createGameEvent.getCreateGameModel();
    final UserDocument user = createGameEvent.getHost();
    if (userIdToGameIdMap.get(user.getId()) != null) {
      throw gameConstants.getCreateGamePlayerAlreadyInGameException();
    }

    // Create game document with state set to "Lobby"
    GameDocument gameDocument =
        new GameDocument(
            UUID.randomUUID(),
            GameState.Lobby,
            new ArrayList<>(), // List of player models is only updated after game begins.
            new ArrayList<>(),
            null, // List of hands is empty until the game starts.
            appConfig.getNumRoundsInRollGame(),
            appConfig.getTimeToActInMillis() / 1000);

    userIdToGameIdMap.put(user.getId(), gameDocument.getId());
    games.put(gameDocument.getId(), gameDocument);

    // Create the lobby.
    lobbyService.createLobby(createGameModel, user, gameDocument.getId());

    applicationEventPublisher.publishEvent(new PublishMessageEvent<>(
        this,
        "/topic/game/create/" + user.getId(),
        new ApiSuccessModel(gameDocument.getId().toString())
    ));
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
    final GameDocument gameDocument = getUsersGameDocument(playerId);
    gameDocument.getPlayers().stream()
        .filter(player -> player.getId().equals(playerId))
        .findFirst()
        .orElseThrow(gameConstants::getPlayerNotInGameException)
        .setAway(status);
    final UserDocument user = userRepository.findUserDocumentById(playerId);
    if (status && handService.getHand(user).getPlayerToAct().getId().equals(playerId)) {
      handService.draw(user);
    }

    broadcastGameUpdate(gameDocument);
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

  /**
   * Attempts to add the specified user to the specified game lobby.
   *
   * @param gameIdString UUID associated with the game the specified user is attempting to join.
   * @param userDocument Model representing the user attempting to join the game.
   * @return An ApiSuccessModel indicating the request was successful.
   * @throws BadRequestException If the game ID provided is invalid or if the specified user is
   *     already in a game (other than the game they are attempting to join - if they attempt to
   *     join this game, nothing will happen).
   */
  public ApiSuccessModel joinLobby(String gameIdString, UserDocument userDocument)
      throws BadRequestException {
    uuidService.checkIfValidAndThrowBadRequest(gameIdString);

    // Check that game exists
    if (games.get(UUID.fromString(gameIdString)) == null) {
      throw gameConstants.getGameNotFoundException();
    }

    final GameDocument gameDocument = games.get(UUID.fromString(gameIdString));
    if (gameDocument.getState() != GameState.Lobby) {
      throw gameConstants.getCanOnlyJoinLobbyException();
    }

    // Check if user is trying to join a lobby they're already in.
    if (lobbyService.isUserInLobby(userDocument.getId())
        && userIdToGameIdMap.get(userDocument.getId()).equals(gameDocument.getId())) {
      // Return informative message with OK status.
      return new ApiSuccessModel("Player is already in the game.");
    }

    // Check if they're in a game. If they are, then throw.
    if (userIdToGameIdMap.get(userDocument.getId()) != null) {
      throw gameConstants.getJoinGamePlayerAlreadyJoinedException();
    }

    // Call lobbyService and say the user wants to join.
    final ApiSuccessModel response = lobbyService.joinLobby(gameDocument.getId(), userDocument);

    // Add mapping userId -> gameId after adding to lobby succeeds.
    userIdToGameIdMap.put(userDocument.getId(), gameDocument.getId());

    return response;
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
    final GameDocument gameDocument = games.get(gameId);
    lobbyService.startGame(gameDocument); // Lobby related housekeeping.
    gameDocument.setState(GameState.Play); // Transition game state.
    handService.setDeck(gameId, new DeckModel()); // Give hand service the deck.
    handService.newHand(gameDocument); // Create the hand.
    initializeGameData(gameDocument); // Initialize game data for client.
    broadcastGameUpdate(gameDocument); // Broadcast the game document.

    /*    TODO: I had an SSE broadcast here sending out player data to players[0] for some reason.
    Leaving this here in case this was actually needed. Will remove soon. */

    // Update Current game topic
    updateCurrentGameTopic(gameDocument, false);

    return new ApiSuccessModel("The game has been started successfully.");
  }

  // TODO: Add docs
  public void updateCurrentGameTopic(final GameDocument game, final boolean over) {
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
   * @param gameDocument The specified game.
   */
  public void broadcastGameDataUpdate(final GameDocument gameDocument) {
    // Broadcast to game topic
    webSocketService.sendPublicMessage(
        appConfig.getGameTopic() + gameDocument.getId(),
        new SocketContainerModel(MessageType.GameData, getGameData(gameDocument)));
  }

  /**
   * Broadcasts the specified game document to relevant players.
   *
   * @param gameDocument The data being broadcast to players.
   */
  public void broadcastGameUpdate(final GameDocument gameDocument) {
    // Broadcast to game topic
    webSocketService.sendPublicMessage(
        appConfig.getGameTopic() + gameDocument.getId(),
        new SocketContainerModel(MessageType.Game, gameDocument));
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
    final GameDocument gameDocument = games.get(handActionEvent.getGameId());
    final HandDocument hand = handService.getHand(handActionEvent.getHandId());
    final int playerThatActed = gameDocument.getPlayers().indexOf(hand.getPlayerToAct());
    final GamePlayerModel nextPlayerToAct =
        gameDocument.getPlayers().get((playerThatActed + 1) % gameDocument.getPlayers().size());

    // Update acting field and update players
    gameDocument.getPlayers().get(playerThatActed).setActing(false);
    // Broadcast player data to game topic
    webSocketService.sendPublicMessage(
        appConfig.getGameTopic() + gameDocument.getPlayers().get(playerThatActed).getId(),
        new SocketContainerModel(
            MessageType.PlayerData,
            getPlayerData(gameDocument.getPlayers().get(playerThatActed).getId())));

    // Set acting to true for player who needs to act.
    nextPlayerToAct.setActing(true);
    // Broadcast player data to game topic
    webSocketService.sendPublicMessage(
        appConfig.getGameTopic() + nextPlayerToAct.getId(),
        new SocketContainerModel(MessageType.PlayerData, getPlayerData(nextPlayerToAct.getId())));

    // Set player to act:
    log.debug("Next player to act is {}.", nextPlayerToAct.getId());
    hand.setPlayerToAct(nextPlayerToAct);

    // Broadcast updated hand model
    handService.broadcastHandUpdate(gameDocument);

    /*
         Temporary logic here to help design game flow for later. Most of this will be gone. Just
         trying to work out the kinks with waiting for players to act, etc...
    */
    final boolean handOver = hand.getPlayerToAct().equals(gameDocument.getPlayers().get(0));
    final int currentRound =
        handOver ? 1 + gameDocument.getHands().size() : gameDocument.getHands().size();

    // Update game data needed for UI.
    updateGameData(
        gameDocument.getId(),
        gameDocument.getPlayers().get(playerThatActed),
        hand.getActions().get(hand.getActions().size() - 1).getDrawnCard(),
        gameDocument.getHands().size());

    if (!handOver) {
      applicationEventPublisher.publishEvent(new WaitForPlayerEvent(this, nextPlayerToAct));
      broadcastGameDataUpdate(gameDocument);
    } else if (currentRound <= gameDocument.getTotalHands()) {
      log.debug("Hand ended. Beginning new hand.");
      // Update game data
      setWinnerInGameData(
          gameDocument.getId(), handService.determineWinner(hand), gameDocument.getHands().size());

      // Update scores
      handService.endHand(gameDocument);
      handService.newHand(gameDocument);
      broadcastGameUpdate(gameDocument);
      broadcastGameDataUpdate(gameDocument);
    } else {
      // If the round is over and the current round > total rounds, then the game must be over.
      log.debug("The game has ended.");
      setWinnerInGameData(
          gameDocument.getId(), handService.determineWinner(hand), gameDocument.getHands().size());
      endGame(gameDocument);
    }
  }

  /**
   * Creates a string summarizing who won the roll game.
   *
   * @param gameDocument The model of the game.
   * @return A string summarizing who won the roll game.
   */
  private String getSummaryMessageForRollGame(GameDocument gameDocument) {
    final int winningScore =
        Collections.max(
            gameDocument.getPlayers().stream()
                .map(GamePlayerModel::getScore)
                .collect(Collectors.toList()));
    final List<GamePlayerModel> winners =
        gameDocument.getPlayers().stream()
            .filter(p -> p.getScore() == winningScore)
            .collect(Collectors.toList());
    StringBuilder message = new StringBuilder();
    if (winners.size() > 1) {
      message
          .append("There are ")
          .append(winners.size())
          .append(" players tied for first place. The winners are ");
      for (int i = 0; i < winners.size() - 1; i++) {
        message
            .append(winners.get(i).getFirstName())
            .append(' ')
            .append(winners.get(i).getLastName())
            .append(i == winners.size() - 2 ? "" : ", ");
      }
      message
          .append(" and ")
          .append(winners.get(winners.size() - 1).getFirstName())
          .append(' ')
          .append(winners.get(winners.size() - 1).getLastName());
    } else {
      message
          .append("The winner is ")
          .append(winners.get(0).getFirstName())
          .append(' ')
          .append(winners.get(0).getLastName());
    }
    message.append(". Other scores can be viewed below.");
    return message.toString();
  }

  /**
   * Procedure that transitions the game to its final state, informs players that the game is over
   * and who won, provides some basic statistics about the game and performs a few housekeeping
   * tasks like removing relevant mappings, etc...
   *
   * @param gameDocument The game document associated with the game that is ending.
   */
  public void endGame(GameDocument gameDocument) {
    handService.endHand(gameDocument); // End hand.
    handService.removeDeck(gameDocument);
    gameDocument.setState(GameState.Over); // Transition game state.
    gameDocument.setSummary(new GameSummaryModel(getSummaryMessageForRollGame(gameDocument)));
    gameRepository.save(gameDocument); // Save game document.
    games.remove(gameDocument.getId()); // Remove mapping.
    gameDocument.getPlayers().forEach(p -> userIdToGameIdMap.remove(p.getId())); // Remove mappings.
    broadcastGameDataUpdate(gameDocument); // Broadcast final update.
    broadcastGameUpdate(gameDocument); // Broadcast final update.
    gameIdToGameDataMap.remove(gameDocument.getId()); // Remove mapping.
    updateCurrentGameTopic(gameDocument, true);
  }
}
