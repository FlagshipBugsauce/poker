package com.poker.poker.services.game;

import com.poker.poker.config.AppConfig;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.events.AwayStatusEvent;
import com.poker.poker.events.CreateGameEvent;
import com.poker.poker.events.CurrentGameEvent;
import com.poker.poker.events.DrawCardEvent;
import com.poker.poker.events.GameMessageEvent;
import com.poker.poker.events.GameOverEvent;
import com.poker.poker.events.HandEvent;
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
import com.poker.poker.models.game.DeckModel;
import com.poker.poker.models.game.GameModel;
import com.poker.poker.models.game.GameParameterModel;
import com.poker.poker.models.game.GamePlayerModel;
import com.poker.poker.models.game.HandSummaryModel;
import com.poker.poker.models.game.LobbyModel;
import com.poker.poker.models.game.LobbyPlayerModel;
import com.poker.poker.models.game.PokerTableModel;
import com.poker.poker.models.websocket.CurrentGameModel;
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
    publisher.publishEvent(new CurrentGameEvent(
        this, event.getId(), new CurrentGameModel(
        data.isUserInGame(event.getId()),
        data.isUserInGame(event.getId()) ? data.getUsersGame(event.getId()).getId() : null)));
  }

  /**
   * Creates a game.
   *
   * @param event Event containing data needed to create a game.
   */
  @EventListener
  public void createGame(final CreateGameEvent event) {
    final GameParameterModel params = event.getGameParameterModel();
    final UserDocument user = event.getHost();

    if (data.isUserInGame(user.getId())) {
      return; // User can only be in one game at a time.
    }

    final UUID gameId = data.newGame(params, user);

    // TODO: Refactor this to use generic message (requires client refactor also).
    publisher.publishEvent(new PublishMessageEvent<>(
        this,
        "/topic/game/create/" + user.getId(),
        new ApiSuccessModel(gameId.toString())));
  }

  /**
   * Handles a player joining a game lobby.
   *
   * @param event Event containing data needed to handle a join event.
   */
  @EventListener
  public void joinLobby(final JoinGameEvent event) {
    final LobbyModel lobby = data.getLobby(event.getGameId());
    final UserDocument user = event.getUser();
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
    final UserDocument user = event.getUser();
    final LobbyModel lobby = data.getUsersLobby(user.getId());
    final LobbyPlayerModel player = lobby
        .getPlayers()
        .stream()
        .filter(p -> p.getId().equals(user.getId()))
        .findFirst()
        .orElse(null);

    if (player == null) {
      return;
    }

    lobby.getPlayers().removeIf(p -> p.getId().equals(user.getId()));
    data.removeUserIdToGameIdMapping(user.getId());

    if (lobby.getPlayers().size() > 0) {
      lobby.setHost(lobby.getPlayers().get(0));
      lobby.getPlayers().get(0).setHost(true);
    } else {
      data.endLobby(lobby.getId());
      return;
    }

    final String toastMessage = String.format(
        "%s %s has left the lobby.", player.getFirstName(), player.getLastName());
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
      final LobbyPlayerModel player = data.getUsersLobby(userId).getPlayers()
          .stream()
          .filter(p -> p.getId().equals(userId))
          .findFirst()
          .orElse(null);
      if (player == null) {
        return;
      }
      player.setReady(!player.isReady());
      publishSystemChatMessageEvent(lobby.getId(), String.format(
          "%s %s %s ready.",
          player.getFirstName(),
          player.getLastName(),
          player.isReady() ? "is" : "is not"));
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
    updateCurrentGameTopic(game, false);

    publisher.publishEvent(new StartHandEvent(this, game.getId()));
  }

  /**
   * Updates the current game topic to ensure the client knows when a player is in a game.
   *
   * @param game The game the player is in.
   * @param over Indicates whether the game is over.
   */
  public void updateCurrentGameTopic(final GameModel game, final boolean over) {
    game.getPlayers().forEach(player -> publisher.publishEvent(new CurrentGameEvent(
        this, player.getId(), new CurrentGameModel(!over, over ? null : game.getId()))));
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
    final GamePlayerModel player = game.getPlayers()
        .stream()
        .filter(p -> p.getId().equals(event.getId()))
        .findFirst()
        .orElse(null);
    final GamePlayerModel acting = table.getPlayers().get(table.getActingPlayer());

    if (player == null) {
      return;
    }

    player.setAway(event.isAway());

    // If the players away status is true, and it's their turn, draw their card for them.
    if (player.getId().equals(acting.getId()) && player.isAway()) {
      publisher.publishEvent(new DrawCardEvent(this, player.getId()));
    }

    publisher
        .publishEvent(new GameMessageEvent<>(this, MessageType.PlayerData, player.getId(), player));
    publisher.publishEvent(new GameMessageEvent<>(
        this, MessageType.PlayerAwayToggled, game.getId(), player));
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
    publisher.publishEvent(new WaitForPlayerEvent(this, table.getPlayers().get(0)));
    // Note: No need to broadcast table update until next player draws.
    data.broadcastGame(game.getId());
  }

  /**
   * Handles a draw card event.
   *
   * @param event Event containing data necessary to draw a card.
   */
  @Async
  @EventListener
  public void drawCard(final DrawCardEvent event) {
    final GameModel game = data.getUsersGame(event.getId());
    final PokerTableModel table = data.getPokerTable(game.getId());
    final GamePlayerModel player = table.getPlayers().get(table.getActingPlayer());
    if (!player.getId().equals(event.getId())) {
      log.error("Player {} attempted to draw a card when it was not this player's turn.",
          event.getId());
      return;
    }
    final DeckModel deck = data.getDeck(game.getId());
    final CardModel card = deck.draw();
    player.getCards().add(card);

    // Need to do a few table updates.
    table.playerActed();

    // Broadcast table -- TODO: Refine what is being sent out
    data.broadcastPokerTable(game.getId());
    publishSystemChatMessageEvent(game.getId(), String.format("%s %s drew the %s of %s",
        player.getFirstName(), player.getLastName(), card.getValue(), card.getSuit()));

    publisher.publishEvent(new HandEvent(this, player.getId()));
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
    final int actingPlayer = table.getActingPlayer();

    Thread.sleep(event.getPlayer().isAway() ? 100 : appConfig.getTimeToActInMillis());
    if (table.getActingPlayer() != actingPlayer) {
      return;
    }

    if (event.getPlayer().isAway()) {
      publisher.publishEvent(new DrawCardEvent(this, event.getPlayer().getId()));
    } else {
      publisher.publishEvent(new AwayStatusEvent(this, event.getPlayer().getId(), true));
    }
  }

  /**
   * Handles a player action.
   *
   * @param event Event containing information needed to handle a player action.
   * @throws InterruptedException Throws if the Thread.sleep is interrupted for some reason.
   */
  @Async
  @EventListener
  public void handleHandEvent(final HandEvent event) throws InterruptedException {
    final GameModel game = data.getUsersGame(event.getId());
    final PokerTableModel table = data.getPokerTable(game.getId());
    final int currentHand = game.getCurrentHand();
    final int totalHands = game.getTotalHands();

    if (table.getActingPlayer() != 0 && currentHand <= totalHands) {
      // Hand is not over, wait for another action.
      publisher.publishEvent(
          new WaitForPlayerEvent(this, table.getPlayers().get(table.getActingPlayer())));
    } else if (table.getActingPlayer() == 0 && currentHand <= totalHands) {
      // Hand is over, pause to display a summary, then start a new hand.
      table.setDisplayHandSummary(true);
      table.setSummary(getHandSummary(game.getId()));
      data.broadcastPokerTable(game.getId());

      // Pause for 3 seconds while summary is displayed to UI.
      Thread.sleep(3000);

      // Remove summary.
      table.setDisplayHandSummary(false);
      final GamePlayerModel winner = table.getPlayers().get(table.getSummary().getWinner());
      publishSystemChatMessageEvent(game.getId(),
          String.format("%s %s won the hand.", winner.getFirstName(), winner.getLastName()));
      table.setSummary(null);

      publisher.publishEvent(new StartHandEvent(this, game.getId()));
    } else {
      // Game is over, run the end game sequence.
      publisher.publishEvent(new GameOverEvent(this, game.getId()));
    }
  }

  /**
   * Helper that returns a HandSummaryModel to help the UI display a hand summary.
   *
   * @param id ID of the game the summary is for.
   * @return
   */
  private HandSummaryModel getHandSummary(final UUID id) {
    final PokerTableModel table = data.getPokerTable(id);
    final List<GamePlayerModel> players = table.getPlayers().stream().map(GamePlayerModel::new)
        .sorted((a, b) -> -cardService.compare(a.getCards().get(0), b.getCards().get(0)))
        .collect(Collectors.toList());
    final int winningIndex = table.getPlayers().indexOf(players.get(0));
    // Update winners score
    final GamePlayerModel winningPlayer = table.getPlayers().get(winningIndex);
    winningPlayer.setScore(winningPlayer.getScore() + 1);
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
  }
}
