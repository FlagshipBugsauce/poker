package com.poker.poker.services.game;

import static com.poker.poker.models.enums.GameAction.AllInCheck;
import static com.poker.poker.models.enums.HandPhase.Over;
import static com.poker.poker.models.enums.MessageType.Deal;
import static com.poker.poker.models.enums.MessageType.HideCards;
import static com.poker.poker.models.enums.MessageType.PlayerAwayToggled;
import static com.poker.poker.models.enums.MessageType.PlayerData;
import static com.poker.poker.models.enums.MessageType.PlayerJoinedLobby;
import static com.poker.poker.models.enums.MessageType.PlayerLeftLobby;
import static com.poker.poker.models.enums.MessageType.ReadyToggled;
import static com.poker.poker.models.enums.MessageType.Timer;
import static com.poker.poker.utilities.PokerTableUtilities.adjustWager;
import static com.poker.poker.utilities.PokerTableUtilities.defaultAction;
import static com.poker.poker.utilities.PokerTableUtilities.getSystemChatActionMessage;
import static com.poker.poker.utilities.PokerTableUtilities.handleEndOfHand;
import static com.poker.poker.utilities.PokerTableUtilities.handlePlayerAction;
import static com.poker.poker.utilities.PokerTableUtilities.numInHand;
import static com.poker.poker.utilities.PokerTableUtilities.numNonZeroChips;
import static com.poker.poker.utilities.PokerTableUtilities.setupNewHand;
import static com.poker.poker.utilities.PokerTableUtilities.setupNextPhase;
import static com.poker.poker.utilities.PokerTableUtilities.transitionHandPhase;
import static java.math.RoundingMode.HALF_UP;

import com.poker.poker.config.AppConfig;
import com.poker.poker.events.AwayStatusEvent;
import com.poker.poker.events.CreateGameEvent;
import com.poker.poker.events.CurrentGameEvent;
import com.poker.poker.events.DealCardsEvent;
import com.poker.poker.events.GameActionEvent;
import com.poker.poker.events.GameMessageEvent;
import com.poker.poker.events.GameOverEvent;
import com.poker.poker.events.HandPhaseTransitionEvent;
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
import com.poker.poker.models.ApiSuccess;
import com.poker.poker.models.enums.GamePhase;
import com.poker.poker.models.game.CurrentGame;
import com.poker.poker.models.game.Deal;
import com.poker.poker.models.game.Game;
import com.poker.poker.models.game.GameParameter;
import com.poker.poker.models.game.GamePlayer;
import com.poker.poker.models.game.HideCards;
import com.poker.poker.models.game.Lobby;
import com.poker.poker.models.game.LobbyPlayer;
import com.poker.poker.models.game.PokerTable;
import com.poker.poker.models.game.Timer;
import com.poker.poker.models.user.User;
import java.math.BigDecimal;
import java.util.List;
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
  private final ApplicationEventPublisher publisher;

  /**
   * Publishes a message to system chat for specified game ID.
   *
   * @param gameId The lobby the message should be published to.
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
            this, event.getId(), new CurrentGame(data.isUserInGame(event.getId()), id)));
  }

  /**
   * Creates a game.
   *
   * @param event Event containing data needed to create a game.
   */
  @EventListener
  public void createGame(final CreateGameEvent event) {
    final GameParameter params = event.getGameParameter();
    final User user = event.getHost();

    if (data.isUserInGame(user.getId())) {
      return; // User can only be in one game at a time.
    }

    final UUID gameId = data.newGame(params, user);

    // TODO: Refactor this to use generic message (requires client refactor also).
    publisher.publishEvent(
        new PublishMessageEvent<>(
            this, "/topic/game/create/" + user.getId(), new ApiSuccess(gameId.toString())));
  }

  /**
   * Handles a player joining a game lobby.
   *
   * @param event Event containing data needed to handle a join event.
   */
  @EventListener
  public void joinLobby(final JoinGameEvent event) {
    final Lobby lobby = data.getLobby(event.getGameId());
    if (data.isUserInGame(event.getUser().getId())) {
      return;
    }
    final User user = event.getUser();
    final LobbyPlayer player = new LobbyPlayer(user, false, false);
    lobby.getPlayers().add(player);
    data.userJoinedGame(user.getId(), lobby.getId());

    final String toastMessage =
        String.format("%s %s has joined the game.", player.getFirstName(), player.getLastName());
    publisher.publishEvent(new ToastMessageEvent(this, lobby.getId(), toastMessage, "lg"));
    publisher.publishEvent(new GameMessageEvent<>(this, PlayerJoinedLobby, lobby.getId(), player));
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
    final User user = event.getUser();
    final Lobby lobby = data.getUsersLobby(user.getId());
    final LobbyPlayer player = data.getLobbyPlayer(user.getId());

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
    publisher.publishEvent(new GameMessageEvent<>(this, PlayerLeftLobby, lobby.getId(), player));

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
    final Lobby lobby = data.getUsersLobby(userId);
    synchronized (lobby.getPlayers()) {
      final LobbyPlayer player = data.getLobbyPlayer(userId);
      player.setReady(!player.isReady());
      publishSystemChatMessageEvent(
          lobby.getId(),
          String.format(
              "%s %s %s ready.",
              player.getFirstName(), player.getLastName(), player.isReady() ? "is" : "is not"));
      publisher.publishEvent(new GameMessageEvent<>(this, ReadyToggled, lobby.getId(), player));
    }
  }

  /**
   * Handles a start game event.
   *
   * @param event Event containing data necessary to start a game.
   */
  @EventListener
  public void start(final StartGameEvent event) {
    final Game game = data.getUsersGame(event.getId());
    final Lobby lobby = data.getUsersLobby(event.getId());

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
  public void updateCurrentGameTopic(final Game game) {
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
    final Game game = data.getUsersGame(event.getId());
    final PokerTable table = data.getPokerTable(game.getId());
    final GamePlayer player = data.getPlayer(event.getId());
    final GamePlayer acting = table.getPlayers().get(table.getActingPlayer());
    player.setAway(event.isAway());

    // If the players away status is true, and it's their turn, draw their card for them.
    if (player.getId().equals(acting.getId()) && player.isAway()) {
      publisher.publishEvent(
          new GameActionEvent(this, player.getId(), defaultAction(player), null));
    }

    publisher.publishEvent(new GameMessageEvent<>(this, PlayerData, player.getId(), player));
    publisher.publishEvent(new GameMessageEvent<>(this, PlayerAwayToggled, game.getId(), player));
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
    final Game game = data.getGame(event.getId());
    final PokerTable table = data.getPokerTable(game.getId());

    // Table setup:
    setupNewHand(table, data.getDeck(game.getId()));

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
    publisher.publishEvent(
        new WaitForPlayerEvent(this, table.getPlayers().get(table.getActingPlayer()).getId()));
  }

  @Async
  @EventListener
  public void broadcastPlayerCards(final PublishCardsEvent event) {
    final Game game = data.getGame(event.getId());
    game.getPlayers()
        .forEach(
            p -> publisher.publishEvent(new PrivateMessageEvent<>(this, PlayerData, p.getId(), p)));
  }

  @Async
  @EventListener
  public void dealCards(final DealCardsEvent event) {
    publisher.publishEvent(new GameMessageEvent<>(this, Deal, event.getId(), new Deal()));
  }

  @Async
  @EventListener
  public void hideCards(final HideCardsEvent event) {
    publisher.publishEvent(new GameMessageEvent<>(this, HideCards, event.getId(), new HideCards()));
  }

  @Async
  @EventListener
  public void handleGameAction(final GameActionEvent event) {
    final Game game = data.getUsersGame(event.getPlayerId());
    final PokerTable table = data.getPokerTable(game.getId());

    // Update table + player models.
    handlePlayerAction(table, event.getType(), event.getPlayerId(), adjustWager(table, event));
    publishSystemChatMessageEvent(game.getId(), getSystemChatActionMessage(table, event));
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
    publisher.publishEvent(new HandPhaseTransitionEvent(this, game.getId()));
  }

  /**
   * Helper that returns a lambda which will broadcast the hand in the appropriate fashion, i.e. it
   * will either hide all cards, or it will display cards of players who haven't folded. TODO: Need
   * to investigate edge cases where hands are displayed in some order, and in some cases, the
   * losing player is not obligated to show their cards.
   *
   * @param id      Game ID.
   * @param hideAll Flag that determine whether all cards are hidden or not.
   * @return Runnable which will broadcast the poker table.
   */
  public Runnable getHandEndBroadcaster(final UUID id, final boolean hideAll) {
    return () -> {
      if (hideAll) {
        data.broadcastObfuscatedPokerTable(id);
      } else {
        data.broadcastPokerTableWithFoldedCardsHidden(id);
      }
      publishTimerMessage(id, appConfig.getHandSummaryDurationInMs());
      try {
        Thread.sleep(appConfig.getHandSummaryDurationInMs());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    };
  }

  @Async
  @EventListener
  public void transitionToNextPhase(final HandPhaseTransitionEvent event)
      throws InterruptedException {
    final Game game = data.getGame(event.getId());
    final PokerTable table = data.getPokerTable(event.getId());
    final List<GamePlayer> players = table.getPlayers();

    // Handle case where all players but one have folded.
    if (numInHand(table) <= 1) {
      log.debug("All players in game {} have folded. Ending hand.", game.getId());
      // Then hand is over
      handleEndOfHand(table, getHandEndBroadcaster(game.getId(), true));
      publisher.publishEvent(
          numNonZeroChips(table) > 1
              ? new StartHandEvent(this, game.getId())
              : new GameOverEvent(this, game.getId()));
      return;
    }

    // Helper to transition the phase.
    transitionHandPhase(table);

    if (table.getPhase() == Over) {
      handleEndOfHand(table, getHandEndBroadcaster(game.getId(), false));
      publisher.publishEvent(
          numNonZeroChips(table) > 1
              ? new StartHandEvent(this, game.getId())
              : new GameOverEvent(this, game.getId()));
      return;
    }

    // If game is not over, then setup the next phase.
    setupNextPhase(table, data.getDeck(game.getId()));
    data.broadcastObfuscatedPokerTable(game.getId());
    log.debug("Hand phase: {}, has started.", table.getPhase());
    publisher.publishEvent(
        new WaitForPlayerEvent(this, table.getPlayers().get(table.getActingPlayer()).getId()));
  }

  private void publishPlayerWonHandChatMessage(final UUID id) {
    final PokerTable table = data.getPokerTable(id);
    assert table.getSummary() != null;
    assert table.isDisplayHandSummary();
    final GamePlayer winner = table.getPlayers().get(table.getSummary().getWinner());
    publishSystemChatMessageEvent(
        id, String.format("%s %s won the hand.", winner.getFirstName(), winner.getLastName()));
  }

  // TODO: Should probably refactor this to another service.
  public void publishTimerMessage(final UUID id, final int durationInMs) {
    final Game game = data.getGame(id);
    final BigDecimal duration =
        new BigDecimal(durationInMs).divide(new BigDecimal(1000), 10, HALF_UP);
    publisher.publishEvent(
        new GameMessageEvent<>(this, Timer, game.getId(), new Timer(UUID.randomUUID(), duration)));
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
    final Game game = data.getUsersGame(event.getId());
    final PokerTable table = data.getPokerTable(game.getId());
    final GamePlayer player = data.getPlayer(event.getId());
    final int tracker = table.getEventTracker();
    data.broadcastObfuscatedPokerTable(game.getId());

    assert table.getPlayers().get(table.getActingPlayer()).getId().equals(player.getId());

    //    Thread.sleep(10); // TODO: May not be necessary - having some issues with AFK actions.
    if (player.isAway()) {
      // Perform default action for afk players, could be AllInCheck, Check or Fold.
      publisher.publishEvent(
          new GameActionEvent(this, player.getId(), defaultAction(player), null));
      return;
    } else if (player.isAllIn()) {
      // Player is All-In, they can't do anything nor can they be removed from the round.
      publisher.publishEvent(new GameActionEvent(this, player.getId(), AllInCheck, null));
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
    final Game game = data.getGame(event.getId());
    game.setPhase(GamePhase.Over);

    // TODO: Save game in DB
    data.endGame(game.getId());
    updateCurrentGameTopic(game);
  }
}
