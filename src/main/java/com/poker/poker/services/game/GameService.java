package com.poker.poker.services.game;

import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.documents.GameDocument;
import com.poker.poker.documents.LobbyDocument;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.events.HandActionEvent;
import com.poker.poker.events.WaitForPlayerEvent;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.GameSummaryModel;
import com.poker.poker.models.enums.EmitterType;
import com.poker.poker.models.enums.GameState;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.models.game.GamePlayerModel;
import com.poker.poker.models.game.hand.HandActionModel;
import com.poker.poker.documents.HandDocument;
import com.poker.poker.models.game.hand.RollActionModel;
import com.poker.poker.repositories.GameRepository;
import com.poker.poker.repositories.HandRepository;
import com.poker.poker.services.SseService;
import com.poker.poker.services.UuidService;
import com.poker.poker.validation.exceptions.BadRequestException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

  private Map<UUID, UUID> userIdToGameIdMap;

  private Map<UUID, GameDocument> games;

  private GameConstants gameConstants;

  private SseService sseService;

  private LobbyService lobbyService;

  private UuidService uuidService;

  private HandService handService;

  private GameRepository gameRepository;

  private HandRepository handRepository;

  private ApplicationEventPublisher applicationEventPublisher;

  /**
   * Retrieves the game document for the game the specified user is in.
   *
   * @param userId ID of the user whose game document is being sought.
   * @return The game document for the game the specified user.
   * @throws BadRequestException If there is no game document associated with the specified user.
   */
  public GameDocument getUsersGameDocument(UUID userId) throws BadRequestException {
    if (userIdToGameIdMap.get(userId) == null) {
      throw gameConstants.getNoUserIdToGameIdMappingFound();
    }
    if (games.get(userIdToGameIdMap.get(userId)) == null) {
      throw gameConstants.getGameNotFoundException();
    }
    return games.get(userIdToGameIdMap.get(userId));
  }

  /**
   * Creates a new game.
   *
   * @param createGameModel Model representing the game parameters.
   * @param user The user who created the game.
   * @return An ApiSuccessModel containing the ID of the game, to indicate creation was successful.
   * @throws BadRequestException If the user is already in a game.
   */
  public ApiSuccessModel createGame(CreateGameModel createGameModel, UserDocument user)
      throws BadRequestException {
    if (userIdToGameIdMap.get(user.getId()) != null) {
      throw gameConstants.getCreateGamePlayerAlreadyInGameException();
    }

    // Create game document with state set to "Lobby"
    GameDocument gameDocument =
        new GameDocument(
            UUID.randomUUID(),
            GameState.Lobby,
            new ArrayList<>(),  // List of player models is only updated after game begins.
            new ArrayList<>(),
            null,     // List of hands is empty until the game starts.
            gameConstants.getNumRoundsInRollGame(),
            (int) (gameConstants.getTimeToActInMillis() / 1000));

    userIdToGameIdMap.put(user.getId(), gameDocument.getId());
    games.put(gameDocument.getId(), gameDocument);

    // Create the lobby.
    lobbyService.createLobby(createGameModel, user, gameDocument.getId());
    return new ApiSuccessModel(gameDocument.getId().toString());
  }

  /**
   * If a player leaves a game that has started, they may be able to rejoin in. This method
   * determines whether the player can still rejoin and will perform the appropriate steps to add
   * them back into the game if they can.
   *
   * @param gameIdString ID of the game the player is attempting to rejoin.
   * @param userDocument Model representing the user attempting to rejoin.
   */
  public void rejoinGame(String gameIdString, UserDocument userDocument) {
    // TODO: Implement this.
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
    // Remove userId -> gameId mapping.
    userIdToGameIdMap.remove(user.getId());

    // Destroy the game emitter sending the player updates.
    sseService.completeEmitter(EmitterType.Game, user.getId());

    // Allow lobbyService to handle the rest.
    return lobbyService.removePlayerFromLobby(user);
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

    // Inform lobbyService the game is starting, which sets the game document's list of players.
    lobbyService.startGame(gameDocument);

    // Send updated game lists (game can no longer be joined via game list).
    sseService.sendToAll(EmitterType.GameList, lobbyService.getLobbyList());

    // Transition the game state.
    gameDocument.setState(GameState.Play);

    // Send update so client knows the game state has changed.
    broadcastGameUpdate(gameDocument);

    // Create new hand.
    handService.newHand(gameDocument);

    // Get the hand.
    final HandDocument hand = handService.getHand(gameDocument);

    // Set the
    hand.setPlayerToAct(gameDocument.getPlayers().get(0));

    // Broadcast hand update.
    handService.broadcastHandUpdate(gameDocument);

    // Wait for player to act.
    applicationEventPublisher
        .publishEvent(new WaitForPlayerEvent(this, hand.getPlayerToAct()));

    // Send another game document update so client knows that a hand has begun.
    broadcastGameUpdate(gameDocument);

    return new ApiSuccessModel("The game has been started successfully.");
  }

  /**
   * Broadcasts the specified game document to relevant players.
   *
   * @param gameDocument The data being broadcast to players.
   */
  public void broadcastGameUpdate(GameDocument gameDocument) {
    gameDocument
        .getPlayers()
        .forEach(player -> sseService.sendUpdate(EmitterType.Game, player.getId(), gameDocument));
  }

  /**
   * Asynchronous event listener that handles HandActionEvents.
   *
   * @param handActionEvent The event object representing a hand action.
   */
  @Async
  @EventListener
  public void handleHandActionEvent(HandActionEvent handActionEvent) {
    log.debug("{} event detected by game service.", handActionEvent.getType());
    final GameDocument gameDocument = games.get(handActionEvent.getGameId());
    final HandDocument hand = handService.getHand(handActionEvent.getHandId());
    final int playerThatActed = gameDocument.getPlayers().indexOf(hand.getPlayerToAct());
    final GamePlayerModel nextPlayerToAct =
        gameDocument.getPlayers().get((playerThatActed + 1) % gameDocument.getPlayers().size());

    // Set player to act:
    log.debug("Next player to act is {}.", nextPlayerToAct.getId());
    hand.setPlayerToAct(nextPlayerToAct);

    // Broadcast updated hand model
    log.debug("Sending updated hand models to players in game {}.", gameDocument.getId());
    handService.broadcastHandUpdate(gameDocument);

    /*
         Temporary logic here to help design game flow for later. Most of this will be gone. Just
         trying to work out the kinks with waiting for players to act, etc...
    */

    final int numRounds = gameDocument.getTotalHands();
    final boolean roundOver = hand.getPlayerToAct().equals(gameDocument.getPlayers().get(0));
    final int currentRound =
        roundOver ? 1 + gameDocument.getHands().size() : gameDocument.getHands().size();

    if (!roundOver) {
      applicationEventPublisher.publishEvent(new WaitForPlayerEvent(this, nextPlayerToAct));
    } else if (currentRound <= numRounds) {
      log.debug("Hand ended. Beginning new hand.");
      // Update scores
      handService.determineWinner(gameDocument);

      handService.endHand(gameDocument);
      handService.newHand(gameDocument);
      final HandDocument newHand = handService.getHand(gameDocument);
      newHand.setPlayerToAct(nextPlayerToAct);

      broadcastGameUpdate(gameDocument);
      handService.broadcastHandUpdate(gameDocument);

      // Publish a wait for player event.
      applicationEventPublisher.publishEvent(new WaitForPlayerEvent(this, nextPlayerToAct));
    } else {
      // If the round is over and the current round > total rounds, then the game must be over.
      log.debug("The game has ended.");
      endGame(gameDocument);
    }
  }

  /**
   * Procedure that transitions the game to its final state, informs players that the game is over
   * and who won, provides some basic statistics about the game and performs a few housekeeping
   * tasks like removing relevant mappings, etc...
   *
   * @param gameDocument The game document associated with the game that is ending.
   */
  public void endGame(GameDocument gameDocument) {
    // Conclude the hand.
    handService.determineWinner(gameDocument);
    handService.endHand(gameDocument);

    // Transition the game state.
    gameDocument.setState(GameState.Over);

    // Determine the winner and generate a brief summary.
    final List<HandDocument> hands = new ArrayList<>();
    for (UUID handId : gameDocument.getHands()) {
      hands.add(handRepository.findHandDocumentById(handId));
    }

    final Map<GamePlayerModel, Integer> points = new HashMap<>();
    for (GamePlayerModel lobbyPlayerModel : gameDocument.getPlayers()) {
      points.put(lobbyPlayerModel, 0);
    }

    for (HandDocument hand : hands) {
      GamePlayerModel winner = null;
      int winningRoll = -1;
      for (HandActionModel a : hand.getActions()) {
        RollActionModel r = (RollActionModel) a;
        if (r.getValue() > winningRoll) {
          winner = r.getPlayer();
          winningRoll = r.getValue();
        }
      }
      assert winner != null;
      points.put(winner, points.get(winner) + 1);
    }

    // Create some output to show a summary for the scores and who won.
    GamePlayerModel winner = null;
    int highestScore = -1;
    final StringBuilder sb = new StringBuilder();
    sb.append("The scores are: ");
    for (GamePlayerModel lobbyPlayerModel : points.keySet()) {
      sb.append(lobbyPlayerModel.getFirstName()).append(" ").append(lobbyPlayerModel.getLastName());
      sb.append(": ").append(points.get(lobbyPlayerModel)).append(", ");
      if (points.get(lobbyPlayerModel) > highestScore) {
        winner = lobbyPlayerModel;
        highestScore = points.get(lobbyPlayerModel);
      }
    }
    final String currentOutput = sb.toString().substring(0, sb.toString().length() - 2);

    assert winner != null;
    gameDocument.setSummary(
        new GameSummaryModel(
            String.format(
                "%s. The winrar is %s %s, with a score of %d.",
                currentOutput, winner.getFirstName(), winner.getLastName(), highestScore)));

    // General housekeeping:

    // Send final update to players.
    broadcastGameUpdate(gameDocument);

    // Save the game document.
    gameRepository.save(gameDocument);

    // Remove mapping from game ID to game document.
    games.remove(gameDocument.getId());

    // Remove mapping from user ID to game ID.
    gameDocument.getPlayers().forEach(p -> userIdToGameIdMap.remove(p.getId()));
  }
}
