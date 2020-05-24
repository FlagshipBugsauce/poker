package com.poker.poker.services.game;

import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.documents.GameDocument;
import com.poker.poker.documents.HandDocument;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.events.HandActionEvent;
import com.poker.poker.events.WaitForPlayerEvent;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.GameSummaryModel;
import com.poker.poker.models.enums.EmitterType;
import com.poker.poker.models.enums.GameState;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.models.game.GamePlayerModel;
import com.poker.poker.repositories.GameRepository;
import com.poker.poker.services.SseService;
import com.poker.poker.services.UuidService;
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

  private Map<UUID, UUID> userIdToGameIdMap;

  private Map<UUID, GameDocument> games;

  private GameConstants gameConstants;

  private SseService sseService;

  private LobbyService lobbyService;

  private UuidService uuidService;

  private HandService handService;

  private GameRepository gameRepository;

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
            new ArrayList<>(), // List of player models is only updated after game begins.
            new ArrayList<>(),
            null, // List of hands is empty until the game starts.
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
    userIdToGameIdMap.remove(user.getId()); // Remove mapping.
    sseService.completeEmitter(EmitterType.Game, user.getId()); // Destroy emitter.
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
    handService.newHand(gameDocument); // Create the hand.
    broadcastGameUpdate(gameDocument); // Broadcast the game document.
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
    final boolean handOver = hand.getPlayerToAct().equals(gameDocument.getPlayers().get(0));
    final int currentRound =
        handOver ? 1 + gameDocument.getHands().size() : gameDocument.getHands().size();

    if (!handOver) {
      applicationEventPublisher.publishEvent(new WaitForPlayerEvent(this, nextPlayerToAct));
    } else if (currentRound <= gameDocument.getTotalHands()) {
      log.debug("Hand ended. Beginning new hand.");
      // Update scores
      handService.endHand(gameDocument);
      handService.newHand(gameDocument);
      broadcastGameUpdate(gameDocument);
    } else {
      // If the round is over and the current round > total rounds, then the game must be over.
      log.debug("The game has ended.");
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
    gameDocument.setState(GameState.Over); // Transition game state.
    gameDocument.setSummary(new GameSummaryModel(getSummaryMessageForRollGame(gameDocument)));
    gameRepository.save(gameDocument); // Save game document.
    games.remove(gameDocument.getId()); // Remove mapping.
    gameDocument.getPlayers().forEach(p -> userIdToGameIdMap.remove(p.getId())); // Remove mappings.
    broadcastGameUpdate(gameDocument); // Broadcast final update.
  }
}
