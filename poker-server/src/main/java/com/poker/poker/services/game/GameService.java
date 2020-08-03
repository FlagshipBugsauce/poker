package com.poker.poker.services.game;

import com.poker.poker.config.AppConfig;
import com.poker.poker.events.AwayStatusEvent;
import com.poker.poker.events.CreateGameEvent;
import com.poker.poker.events.CurrentGameEvent;
import com.poker.poker.events.DealCardsEvent;
import com.poker.poker.events.DrawCardEvent;
import com.poker.poker.events.GameMessageEvent;
import com.poker.poker.events.GameOverEvent;
import com.poker.poker.events.JoinGameEvent;
import com.poker.poker.events.LeaveGameEvent;
import com.poker.poker.events.LeaveLobbyEvent;
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
import com.poker.poker.models.enums.GamePhase;
import com.poker.poker.models.enums.MessageType;
import com.poker.poker.models.game.CardModel;
import com.poker.poker.models.game.CurrentGameModel;
import com.poker.poker.models.game.DealModel;
import com.poker.poker.models.game.DeckModel;
import com.poker.poker.models.game.DrawGameDataContainerModel;
import com.poker.poker.models.game.DrawGameDataModel;
import com.poker.poker.models.game.DrawGameDrawModel;
import com.poker.poker.models.game.GameModel;
import com.poker.poker.models.game.GameParameterModel;
import com.poker.poker.models.game.GamePlayerModel;
import com.poker.poker.models.game.HandSummaryModel;
import com.poker.poker.models.game.LobbyModel;
import com.poker.poker.models.game.LobbyPlayerModel;
import com.poker.poker.models.game.PokerTableModel;
import com.poker.poker.models.game.TimerModel;
import com.poker.poker.models.user.UserModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
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
  private final GameDataService data;
  private final CardService cardService;
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

    if (lobby.getPlayers().size() > 0) {
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
      publisher.publishEvent(new DrawCardEvent(this, player.getId()));
    }

    publisher.publishEvent(
        new GameMessageEvent<>(this, MessageType.PlayerData, player.getId(), player));
    publisher.publishEvent(
        new GameMessageEvent<>(this, MessageType.PlayerAwayToggled, game.getId(), player));
    data.broadcastPokerTable(game.getId());
    // TODO: Refine what we're sending out here
  }

  /**
   * Handle a start hand event.
   *
   * @param event Event containing data necessary to start a hand.
   */
  @Async
  @EventListener
  public void startHand(final StartHandEvent event) {
    final GameModel game = data.getGame(event.getId());
    final PokerTableModel table = data.getPokerTable(game.getId());
    game.incrementCurrentHand();
    table.getPlayers().forEach(p -> p.setCards(new ArrayList<>())); // Clear drawn cards.
    data.getDeck(game.getId()).restoreAndShuffle(); // Restore and shuffle the deck.
    table.setActingPlayer(0);
    table.setDisplayHandSummary(false);
    // Note: No need to broadcast table update until next player draws.
    data.broadcastGame(game.getId());
    data.broadcastPokerTable(game.getId());
    publisher.publishEvent(new WaitForPlayerEvent(this, table.getPlayers().get(0)));
  }

  @Async
  @EventListener
  public void dealCards(final DealCardsEvent event) {
    publisher.publishEvent(
        new GameMessageEvent<>(this, MessageType.Deal, event.getId(), new DealModel()));
  }

  /**
   * Handles a draw card event.
   *
   * @param event Event containing data necessary to draw a card.
   */
  @Async
  @EventListener
  public void drawCard(final DrawCardEvent event) throws InterruptedException {
    final GameModel game = data.getUsersGame(event.getId());
    final PokerTableModel table = data.getPokerTable(game.getId());
    final GamePlayerModel player = table.getPlayers().get(table.getActingPlayer());

    if (!player.getId().equals(event.getId())) {
      log.error("Player {} acted when it was not this player's turn.", event.getId());
      return;
    }

    final DeckModel deck = data.getDeck(game.getId());
    final CardModel card = deck.draw();
    player.getCards().add(card);
    table.playerActed();

    // Broadcast table -- TODO: Refine what is being sent out
    data.broadcastPokerTable(game.getId());
    publishCardDrawnChatMessage(game.getId(), card, player);

    final int currentHand = game.getCurrentHand();
    final int totalHands = game.getTotalHands();

    if (table.getActingPlayer() != 0) { // Hand is not over, wait for another action.
      data.broadcastPokerTable(game.getId());
      publisher.publishEvent(
          new WaitForPlayerEvent(this, table.getPlayers().get(table.getActingPlayer())));
    } else { // Hand is over, pause to display a summary, then start a new hand.
      table.setDisplayHandSummary(true);
      table.setSummary(getHandSummary(game.getId()));
      data.broadcastPokerTable(game.getId());

      // Pause for 3 seconds while summary is displayed to UI.
      publishTimerMessage(game.getId(), appConfig.getHandSummaryDurationInMs());
      Thread.sleep(appConfig.getHandSummaryDurationInMs());
      publishPlayerWonHandChatMessage(game.getId());
      table.setDisplayHandSummary(false);
      table.setSummary(null);
      data.broadcastPokerTable(game.getId());

      if (currentHand < totalHands) {
        publisher.publishEvent(new StartHandEvent(this, game.getId()));
      } else {
        publisher.publishEvent(new GameOverEvent(this, game.getId()));
      }
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

  private void publishCardDrawnChatMessage(
      final UUID id, final CardModel card, final GamePlayerModel player) {
    final GameModel game = data.getGame(id);
    publishSystemChatMessageEvent(
        game.getId(),
        String.format(
            "%s %s drew the %s of %s",
            player.getFirstName(), player.getLastName(), card.getValue(), card.getSuit()));
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
    final GameModel game = data.getUsersGame(event.getPlayer().getId());
    final PokerTableModel table = data.getPokerTable(game.getId());
    final int action = table.getEventTracker();
    publishTimerMessage(
        game.getId(), event.getPlayer().isAway() ? 100 : appConfig.getTimeToActInMs());

    assert table
        .getPlayers()
        .get(table.getActingPlayer())
        .getId()
        .equals(event.getPlayer().getId());

    Thread.sleep(event.getPlayer().isAway() ? 100 : appConfig.getTimeToActInMs());
    if (table.getEventTracker() != action) {
      log.debug("Player acted.");
      return;
    }

    log.debug("Player did not act - performing default action.");
    if (event.getPlayer().isAway()) {
      publisher.publishEvent(new DrawCardEvent(this, event.getPlayer().getId()));
    } else {
      publisher.publishEvent(new AwayStatusEvent(this, event.getPlayer().getId(), true));
    }
  }

  /**
   * Helper that returns a HandSummaryModel to help the UI display a hand summary.
   *
   * @param id ID of the game the summary is for.
   * @return Hand summary.
   */
  private HandSummaryModel getHandSummary(final UUID id) {
    final PokerTableModel table = data.getPokerTable(id);

    // TODO: Assertion for debugging purposes.
    table
        .getPlayers()
        .forEach(
            p -> {
              assert p.getCards().size() == 1;
              assert p.getCards().get(0) != null;
            });

    final List<GamePlayerModel> players =
        table.getPlayers().stream()
            .map(GamePlayerModel::new)
            .sorted((a, b) -> -cardService.compare(a.getCards().get(0), b.getCards().get(0)))
            .collect(Collectors.toList());
    final int winningIndex = table.getPlayers().indexOf(players.get(0));
    // Update winners score.
    final GamePlayerModel winningPlayer = table.getPlayers().get(winningIndex);
//    winningPlayer.setScore(winningPlayer.getScore() + 1);

    /// Update game data.
    final DrawGameDataContainerModel gameData = data.getGameSummary(id);
    for (final DrawGameDataModel d : gameData.getGameData()) {
      final boolean winner =
          table.getPlayers().get(winningIndex).getId().equals(d.getPlayer().getId());
      d.getDraws().add(new DrawGameDrawModel(d.getPlayer().getCards().get(0), winner));
    }

    return new HandSummaryModel(players.get(0).getCards().get(0), winningIndex);
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
