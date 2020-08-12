package com.poker.poker.services.game;

import static com.poker.poker.utilities.PokerTableUtilities.adjustWager;
import static com.poker.poker.utilities.PokerTableUtilities.defaultAction;
import static com.poker.poker.utilities.PokerTableUtilities.getSystemChatActionMessage;
import static com.poker.poker.utilities.PokerTableUtilities.handleEndOfHand;
import static com.poker.poker.utilities.PokerTableUtilities.handlePlayerAction;
import static com.poker.poker.utilities.PokerTableUtilities.newHandSetup;

import com.poker.poker.config.AppConfig;
import com.poker.poker.events.AwayStatusEvent;
import com.poker.poker.events.CreateGameEvent;
import com.poker.poker.events.CurrentGameEvent;
import com.poker.poker.events.DealCardsEvent;
import com.poker.poker.events.GameActionEvent;
import com.poker.poker.events.GameMessageEvent;
import com.poker.poker.events.GameOverEvent;
import com.poker.poker.events.HideCardsEvent;
import com.poker.poker.events.JoinGameEvent;
import com.poker.poker.events.LeaveGameEvent;
import com.poker.poker.events.LeaveLobbyEvent;
import com.poker.poker.events.PrivateMessageEvent;
import com.poker.poker.events.PublishCardsEvent;
import com.poker.poker.events.PublishCurrentGameEvent;
import com.poker.poker.events.PublishMessageEvent;
import com.poker.poker.events.ReadyEvent;
import com.poker.poker.events.RejoinGameEvent;
import com.poker.poker.events.StartGameEvent;
import com.poker.poker.events.StartHandEvent;
import com.poker.poker.events.SystemChatMessageEvent;
import com.poker.poker.events.ToastMessageEvent;
import com.poker.poker.events.WaitForPlayerEvent;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.enums.GameAction;
import com.poker.poker.models.enums.GamePhase;
import com.poker.poker.models.enums.MessageType;
import com.poker.poker.models.game.CurrentGameModel;
import com.poker.poker.models.game.DealModel;
import com.poker.poker.models.game.GameModel;
import com.poker.poker.models.game.GameParameterModel;
import com.poker.poker.models.game.GamePlayerModel;
import com.poker.poker.models.game.HideCardsModel;
import com.poker.poker.models.game.LobbyModel;
import com.poker.poker.models.game.LobbyPlayerModel;
import com.poker.poker.models.game.PokerTableModel;
import com.poker.poker.models.game.TimerModel;
import com.poker.poker.models.user.UserModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GameService {

  private final AppConfig appConfig;
  private final GameDataService data;
  private final CardService cardService;
  private final ApplicationEventPublisher publisher;

  /**
   * Publishes a message to system chat for specified game ID.
   *
   * @param gameId  The lobby the message should be published to.
   * @param message The message to publish.
   */
  public void publishSystemChatMessageEvent(final UUID gameId, final String message) {
    publisher.publishEvent(new SystemChatMessageEvent(this, gameId, message));
  }

  /**
   * Publishes current game info so client has information needed to rejoin an active game.
   *
   * @param event Event containing data needed by the client.
   */
  @EventListener
  public void publishCurrentGameInfo(final PublishCurrentGameEvent event) {
    final UUID id =
        data.isUserInGame(event.getId()) ? data.getUsersGame(event.getId()).getId() : null;
    publisher.publishEvent(
        new CurrentGameEvent(
            this, event.getId(), new CurrentGameModel(data.isUserInGame(event.getId()), id)));
  }

  /**
   * Creates a game.
   *
   * @param event Event containing data needed to create a game.
   */
  @EventListener
  public void createGame(final CreateGameEvent event) {
    final GameParameterModel params = event.getGameParameterModel();
    final UserModel user = event.getHost();

    if (data.isUserInGame(user.getId())) {
      return; // User can only be in one game at a time.
    }

    final UUID gameId = data.newGame(params, user);

    // TODO: Refactor this to use generic message (requires client refactor also).
    publisher.publishEvent(
        new PublishMessageEvent<>(
            this, "/topic/game/create/" + user.getId(), new ApiSuccessModel(gameId.toString())));
  }

  /**
   * Handles a player joining a game lobby.
   *
   * @param event Event containing data needed to handle a join event.
   */
  @EventListener
  public void joinLobby(final JoinGameEvent event) {
    final LobbyModel lobby = data.getLobby(event.getGameId());
    if (data.isUserInGame(event.getUser().getId())) {
      return;
    }
    final UserModel user = event.getUser();
    final LobbyPlayerModel player = new LobbyPlayerModel(user, false, false);
    lobby.getPlayers().add(player);
    data.userJoinedGame(user.getId(), lobby.getId());

    final String toastMessage =
        String.format("%s %s has joined the game.", player.getFirstName(), player.getLastName());
    publisher.publishEvent(new ToastMessageEvent(this, lobby.getId(), toastMessage, "lg"));
    publisher.publishEvent(
        new GameMessageEvent<>(this, MessageType.PlayerJoinedLobby, lobby.getId(), player));
    data.cachedGameListIsOutdated();
  }

  /**
   * Handles a player leaving a game lobby.
   *
   * @param event Event containing data needed to handle a leave lobby event.
   */
  @EventListener
  public void leaveLobby(final LeaveLobbyEvent event) {
    if (!data.isUserInGame(event.getUser().getId())) {
      return;
    }
    final UserModel user = event.getUser();
    final LobbyModel lobby = data.getUsersLobby(user.getId());
    final LobbyPlayerModel player = data.getLobbyPlayer(user.getId());

    lobby.getPlayers().removeIf(p -> p.getId().equals(user.getId()));
    data.removeUserIdToGameIdMapping(user.getId());

    if (!lobby.getPlayers().isEmpty()) {
      lobby.setHost(lobby.getPlayers().get(0));
      lobby.getPlayers().get(0).setHost(true);
    } else {
      log.debug("There are no players left in lobby {}, ending game.", lobby.getId());
      data.endLobby(lobby.getId());
      return;
    }

    final String toastMessage =
        String.format("%s %s has left the lobby.", player.getFirstName(), player.getLastName());
    publisher.publishEvent(new ToastMessageEvent(this, lobby.getId(), toastMessage, "lg"));
    publisher.publishEvent(
        new GameMessageEvent<>(this, MessageType.PlayerLeftLobby, lobby.getId(), player));

    data.cachedGameListIsOutdated();
  }

  /**
   * Handles the event triggered when a player's ready status is toggled.
   *
   * @param event Event containing data necessary to toggle a player's ready status.
   */
  @EventListener
  public void ready(final ReadyEvent event) {
    final UUID userId = event.getId();
    if (!data.isUserInGame(userId)) {
      return;
    }
    final LobbyModel lobby = data.getUsersLobby(userId);
    synchronized (lobby.getPlayers()) {
      final LobbyPlayerModel player = data.getLobbyPlayer(userId);
      player.setReady(!player.isReady());
      publishSystemChatMessageEvent(
          lobby.getId(),
          String.format(
              "%s %s %s ready.",
              player.getFirstName(), player.getLastName(), player.isReady() ? "is" : "is not"));
      publisher.publishEvent(
          new GameMessageEvent<>(this, MessageType.ReadyToggled, lobby.getId(), player));
    }
  }

  /**
   * Handles a start game event.
   *
   * @param event Event containing data necessary to start a game.
   */
  @EventListener
  public void start(final StartGameEvent event) {
    final GameModel game = data.getUsersGame(event.getId());
    final LobbyModel lobby = data.getUsersLobby(event.getId());

    // Check that all players are ready.
    if (lobby.getPlayers().stream().anyMatch(p -> !p.isReady())) {
      return;
    }

    data.startGame(game.getId());
    publishSystemChatMessageEvent(game.getId(), "The game has started.");
    updateCurrentGameTopic(game);
    publisher.publishEvent(new StartHandEvent(this, game.getId()));
  }

  /**
   * Updates the current game topic to ensure the client knows when a player is in a game.
   *
   * @param game The game the player is in.
   */
  public void updateCurrentGameTopic(final GameModel game) {
    game.getPlayers()
        .forEach(
            player -> publisher.publishEvent(new PublishCurrentGameEvent(this, player.getId())));
  }

  /**
   * Handles leave game events.
   *
   * @param event Event containing data necessary for a player to leave a game.
   */
  @Async
  @EventListener
  public void leaveGame(final LeaveGameEvent event) {
    publisher.publishEvent(new AwayStatusEvent(this, event.getUser().getId(), true));
  }

  /**
   * Handles rejoin game events.
   *
   * @param event Event containing data necessary for a player to rejoin a game.
   */
  @Async
  @EventListener
  public void rejoinGame(final RejoinGameEvent event) {
    publisher.publishEvent(new AwayStatusEvent(this, event.getUser().getId(), false));
  }

  /**
   * Handles event when a player's away status is toggled.
   *
   * @param event Event containing data necessary to toggle a player's away status.
   */
  @Async
  @EventListener
  public void afk(final AwayStatusEvent event) {
    final GameModel game = data.getUsersGame(event.getId());
    final PokerTableModel table = data.getPokerTable(game.getId());
    final GamePlayerModel player = data.getPlayer(event.getId());
    final GamePlayerModel acting = table.getPlayers().get(table.getActingPlayer());
    player.setAway(event.isAway());

    // If the players away status is true, and it's their turn, draw their card for them.
    if (player.getId().equals(acting.getId()) && player.isAway()) {
      publisher.publishEvent(new GameActionEvent(
          this, player.getId(), defaultAction(player), null));
    }

    publisher.publishEvent(
        new GameMessageEvent<>(this, MessageType.PlayerData, player.getId(), player));
    publisher.publishEvent(
        new GameMessageEvent<>(this, MessageType.PlayerAwayToggled, game.getId(), player));
    data.broadcastObfuscatedPokerTable(game.getId());
    // TODO: Refine what we're sending out here
  }

  /**
   * Handle a start hand event.
   *
   * @param event Event containing data necessary to start a hand.
   */
  @Async
  @EventListener
  public void startHand(final StartHandEvent event) throws InterruptedException {
    final GameModel game = data.getGame(event.getId());
    final PokerTableModel table = data.getPokerTable(game.getId());

    // Table setup:
    newHandSetup(table, data.getDeck(game.getId()));

    data.broadcastObfuscatedPokerTable(game.getId());

    publisher.publishEvent(new HideCardsEvent(this, game.getId()));
    publisher.publishEvent(new DealCardsEvent(this, game.getId()));
    publisher.publishEvent(new PublishCardsEvent(this, game.getId()));

    publishTimerMessage(game.getId(), appConfig.getDealDurationInMs());
    Thread.sleep(appConfig.getDealDurationInMs());

    // Broadcast updates.
    data.broadcastGame(game.getId());
    data.broadcastObfuscatedPokerTable(game.getId());

    // Wait for player action.
    publisher.publishEvent(new WaitForPlayerEvent(
        this,
        table.getPlayers().get(table.getActingPlayer()).getId()));
  }

  @Async
  @EventListener
  public void broadcastPlayerCards(final PublishCardsEvent event) {
    final GameModel game = data.getGame(event.getId());
    game.getPlayers().forEach(p -> publisher.publishEvent(
        new PrivateMessageEvent<>(this, MessageType.PlayerData, p.getId(), p)));
  }

  @Async
  @EventListener
  public void dealCards(final DealCardsEvent event) {
    publisher.publishEvent(new GameMessageEvent<>(
        this, MessageType.Deal, event.getId(), new DealModel()));
  }

  @Async
  @EventListener
  public void hideCards(final HideCardsEvent event) {
    publisher.publishEvent(new GameMessageEvent<>(
        this, MessageType.HideCards, event.getId(), new HideCardsModel()));
  }

  @Async
  @EventListener
  public void handleGameAction(final GameActionEvent event) throws InterruptedException {
    final GameModel game = data.getUsersGame(event.getPlayerId());
    final PokerTableModel table = data.getPokerTable(game.getId());

    // Update table + player models.
    handlePlayerAction(table, event.getType(), event.getPlayerId(), adjustWager(table, event));

    publishSystemChatMessageEvent(
        game.getId(), getSystemChatActionMessage(table, event));
    data.broadcastObfuscatedPokerTable(game.getId());

    // Check if hand should continue, if yes, publish wait even and return;
    final long numPlayersRemaining =
        table.getPlayers().stream().filter(p -> !p.isOut() && !p.isFolded()).count();
    if (numPlayersRemaining >= 2 && table.getPlayerThatActed() != table.getLastToAct()) {
      // Wait for the next action.
      log.debug("Waiting for next player action.");
      publisher.publishEvent(
          new WaitForPlayerEvent(this, table.getPlayers().get(table.getActingPlayer()).getId()));
      return;
    }

    // If we get here, then the hand (and possibly the game) is over.
    handleEndOfHand(table, cardService);
    // Let's display the summary and cards (if appropriate).
    if (numPlayersRemaining > 1) {
      data.broadcastPokerTable(game.getId());
    } else {
      data.broadcastObfuscatedPokerTable(game.getId());
    }
    publishPlayerWonHandChatMessage(game.getId());
    publishTimerMessage(game.getId(), appConfig.getHandSummaryDurationInMs());
    Thread.sleep(appConfig.getHandSummaryDurationInMs());

    // Now we need to determine whether the game is over (i.e. all players are bust, except one).
    log.debug("Determining if game should continue.");
    final int numRemaining = (int) table.getPlayers()
        .stream()
        .filter(p -> !p.getControls().getBankRoll()
            .equals(BigDecimal.ZERO))  // TODO: Should check isOut
        .count();

    if (numRemaining > 1) {
      // Start a new hand
      publisher.publishEvent(new StartHandEvent(this, game.getId()));
    } else {
      // Display game summary.
      publisher.publishEvent(new GameOverEvent(this, game.getId()));
    }
  }

  private void publishPlayerWonHandChatMessage(final UUID id) {
    final PokerTableModel table = data.getPokerTable(id);
    assert table.getSummary() != null;
    assert table.isDisplayHandSummary();
    final GamePlayerModel winner = table.getPlayers().get(table.getSummary().getWinner());
    publishSystemChatMessageEvent(
        id, String.format("%s %s won the hand.", winner.getFirstName(), winner.getLastName()));
  }

  private void publishTimerMessage(final UUID id, final int durationInMs) {
    final GameModel game = data.getGame(id);
    final BigDecimal duration =
        new BigDecimal(durationInMs).divide(new BigDecimal(1000), 10, RoundingMode.HALF_UP);
    publisher.publishEvent(
        new GameMessageEvent<>(
            this, MessageType.Timer, game.getId(), new TimerModel(UUID.randomUUID(), duration)));
  }

  /**
   * Waits for a player to perform an action (i.e. drawing a card).
   *
   * @param event Event containing data required to determine whether player acts/does not act.
   * @throws InterruptedException Throws if the Thread.sleep is interrupted for some reason.
   */
  @Async
  @EventListener
  public void waitForAction(final WaitForPlayerEvent event) throws InterruptedException {
    final GameModel game = data.getUsersGame(event.getId());
    final PokerTableModel table = data.getPokerTable(game.getId());
    final GamePlayerModel player = data.getPlayer(event.getId());
    final int tracker = table.getEventTracker();
    data.broadcastObfuscatedPokerTable(game.getId());

    assert table
        .getPlayers()
        .get(table.getActingPlayer())
        .getId()
        .equals(player.getId());

    if (player.isAway()) {
      // Perform default action for afk players, could be AllInCheck, Check or Fold.
      publisher.publishEvent(new GameActionEvent(
          this, player.getId(), defaultAction(player), null));
      return;
    } else if (player.isAllIn()) {
      // Player is All-In, they can't do anything nor can they be removed from the round.
      publisher.publishEvent(new GameActionEvent(
          this, player.getId(), GameAction.AllInCheck, null));
      return;
    }

    publishTimerMessage(game.getId(), player.isAway() ? 0 : appConfig.getTimeToActInMs());
    Thread.sleep(appConfig.getTimeToActInMs());

    // Check if player acted (event tracker value will not be the same if an action was performed).
    if (table.getEventTracker() != tracker) {
      log.debug("Player acted.");
      return;
    }

    log.debug("Player did not act - setting Away status to true and perform default action.");
    publisher.publishEvent(new AwayStatusEvent(this, player.getId(), true));
  }

  /**
   * Handles game over events.
   *
   * @param event Event containing data necessary to end a game.
   */
  @Async
  @EventListener
  public void gameOver(final GameOverEvent event) {
    final GameModel game = data.getGame(event.getId());
    game.setPhase(GamePhase.Over);

    // TODO: Save game in DB
    data.endGame(game.getId());
    updateCurrentGameTopic(game);
  }
}
