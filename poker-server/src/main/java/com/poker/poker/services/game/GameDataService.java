package com.poker.poker.services.game;

import static com.poker.poker.models.enums.GamePhase.Play;
import static com.poker.poker.models.enums.MessageType.Game;
import static com.poker.poker.models.enums.MessageType.GameData;
import static com.poker.poker.models.enums.MessageType.GameList;
import static com.poker.poker.models.enums.MessageType.GamePhaseChanged;
import static com.poker.poker.models.enums.MessageType.PokerTable;
import static com.poker.poker.utilities.PokerTableUtilities.hideCards;
import static com.poker.poker.utilities.PokerTableUtilities.hideFoldedCards;
import static ir.cafebabe.math.utils.BigDecimalUtils.is;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.CEILING;
import static java.util.stream.Collectors.toList;

import com.poker.poker.config.AppConfig;
import com.poker.poker.events.GameMessageEvent;
import com.poker.poker.models.enums.GamePhase;
import com.poker.poker.models.game.Deck;
import com.poker.poker.models.game.DrawGameData;
import com.poker.poker.models.game.DrawGameDataContainer;
import com.poker.poker.models.game.Game;
import com.poker.poker.models.game.GameList;
import com.poker.poker.models.game.GameParameter;
import com.poker.poker.models.game.GamePlayer;
import com.poker.poker.models.game.Lobby;
import com.poker.poker.models.game.LobbyPlayer;
import com.poker.poker.models.game.PokerTable;
import com.poker.poker.models.user.User;
import com.poker.poker.models.websocket.GenericServerMessage;
import com.poker.poker.services.WebSocketService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Data handler for poker games. All methods have a pre-condition that the ID provided is valid.
 * Currently using assert statements as a type of pre-condition to all methods.
 */
@Slf4j
@Getter
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GameDataService {

  private final AppConfig appConfig;
  private final ApplicationEventPublisher publisher;
  private final WebSocketService webSocketService;
  private final Map<UUID, com.poker.poker.models.game.Game> games;
  private final Map<UUID, Lobby> lobbys;
  private final Map<UUID, Deck> decks;
  private final Map<UUID, com.poker.poker.models.game.PokerTable> tables;
  private final Map<UUID, DrawGameDataContainer> summaries;
  /** Mapping from user ID to game ID. Can be used to retrieve the game a user is in. */
  private final Map<UUID, UUID> userIdToGameIdMap;

  private List<com.poker.poker.models.game.GameList> gameList;
  private boolean useCachedGameList = false;

  public GameDataService(
      final AppConfig appConfig,
      final ApplicationEventPublisher publisher,
      final WebSocketService webSocketService) {
    this.appConfig = appConfig;
    this.publisher = publisher;
    this.webSocketService = webSocketService;
    games = Collections.synchronizedMap(new HashMap<>());
    lobbys = Collections.synchronizedMap(new HashMap<>());
    decks = Collections.synchronizedMap(new HashMap<>());
    tables = Collections.synchronizedMap(new HashMap<>());
    summaries = Collections.synchronizedMap(new HashMap<>());
    userIdToGameIdMap = Collections.synchronizedMap(new HashMap<>());
  }

  /**
   * If there is any change to any of the lobbys, this method should be called so that an updated
   * game list is generated.
   */
  public void cachedGameListIsOutdated() {
    useCachedGameList = false;
  }

  /** Every 3 seconds, broadcasts the list of joinable games to the game list topic. */
  @Scheduled(cron = "0/3 * * * * ?")
  public void broadcastGameList() {
    webSocketService.sendPublicMessage(
        appConfig.getGameListTopic(),
        new GenericServerMessage<>(GameList, useCachedGameList ? gameList : getLobbyList()));
  }

  /**
   * Broadcasts the poker table associated with the specified game ID to the game topic associated
   * with this ID.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>tables.get(id) != null</code>
   * </ol>
   *
   * @param id ID of the game associated with the poker table being broadcast.
   */
  public void broadcastPokerTable(final UUID id) {
    assert tables.get(id) != null;
    publisher.publishEvent(new GameMessageEvent<>(this, PokerTable, id, tables.get(id)));
  }

  /**
   * Broadcasts the poker table associated with the specified game ID to the game topic associated
   * with this ID. Before broadcasting, player cards are hidden, so the data being sent out lets
   * clients that are listening know that players have cards, but does not give them any information
   * about what those cards actually are.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>tables.get(id) != null</code>
   * </ol>
   *
   * @param id ID of the game associated with the poker table being broadcast.
   */
  public void broadcastObfuscatedPokerTable(final UUID id) {
    assert tables.get(id) != null;
    publisher.publishEvent(new GameMessageEvent<>(this, PokerTable, id, hideCards(tables.get(id))));
  }

  public void broadcastPokerTableWithFoldedCardsHidden(final UUID id) {
    assert (tables.get(id)) != null;
    publisher.publishEvent(
        new GameMessageEvent<>(this, PokerTable, id, hideFoldedCards(tables.get(id))));
  }

  /**
   * Broadcasts certain game data, such as the game phase.
   *
   * <p>TODO: May deprecate this and replace with PokerTable.
   *
   * @param id Game ID of the game to broadcast.
   */
  public void broadcastGame(final UUID id) {
    assert games.get(id) != null;
    publisher.publishEvent(new GameMessageEvent<>(this, Game, id, games.get(id)));
  }

  public void broadcastGameSummary(final UUID id) {
    assert summaries.get(id) != null;
    publisher.publishEvent(new GameMessageEvent<>(this, GameData, id, summaries.get(id)));
  }

  /**
   * Gets the list of active game models that are currently in the PreGame state.
   *
   * @return An ActiveGameModel which is a subset of a game document.
   */
  public List<com.poker.poker.models.game.GameList> getLobbyList() {
    gameList = new ArrayList<>();
    for (final Lobby lobby : lobbys.values()) {
      gameList.add(
          new GameList(
              lobby.getId(), lobby.getParameters(), lobby.getHost(), lobby.getPlayers().size()));
    }
    useCachedGameList = true;
    return gameList;
  }

  /**
   * Sets up all the required mappings when a game is created and returns the ID of the new game.
   *
   * @param params Parameters of the new game.
   * @param user User who created the game.
   * @return ID of the new game.
   */
  public UUID newGame(final GameParameter params, final User user) {
    final UUID gameId = UUID.randomUUID();
    assert games.get(gameId) == null;
    assert lobbys.get(gameId) == null;
    assert decks.get(gameId) == null;
    assert tables.get(gameId) == null;
    assert summaries.get(gameId) == null;
    assert userIdToGameIdMap.get(user.getId()) == null;

    final int turnDuration = params.getTurnDuration() == 0 ?
        appConfig.getTimeToActInMs() / 1000 : params.getTurnDuration();
    final BigDecimal blinds =
        params.getStartingBlinds() == null || is(params.getStartingBlinds()).eq(ZERO) ?
            params.getBuyIn().divide(new BigDecimal(appConfig.getNumSmallBlinds()), CEILING) :
            params.getStartingBlinds();

    // TODO: Don't need turnDuration here.
    final Game game =
        new Game(gameId, GamePhase.Lobby, new ArrayList<>(), turnDuration);

    final LobbyPlayer host = new LobbyPlayer(user, false, true);
    final List<LobbyPlayer> players = Collections.synchronizedList(new ArrayList<>());
    players.add(host);

    games.put(gameId, game);
    lobbys.put(gameId, new Lobby(gameId, host, params, players));
    decks.put(gameId, new Deck());
    tables.put(gameId, new PokerTable());
    tables.get(gameId).setTurnDuration(turnDuration); // Set turn duration
    tables.get(gameId).setBlind(blinds);              // Set blinds
    summaries.put(gameId, new DrawGameDataContainer(new ArrayList<>()));
    userIdToGameIdMap.put(host.getId(), gameId);

    cachedGameListIsOutdated();

    return gameId;
  }

  public com.poker.poker.models.game.Game getGame(final UUID id) {
    assert games.get(id) != null;
    return games.get(id);
  }

  public com.poker.poker.models.game.Game getUsersGame(final UUID id) {
    assert userIdToGameIdMap.get(id) != null;
    assert games.get(userIdToGameIdMap.get(id)) != null;
    return games.get(userIdToGameIdMap.get(id));
  }

  public boolean isUserInGame(final UUID id) {
    return userIdToGameIdMap.get(id) != null && games.get(userIdToGameIdMap.get(id)) != null;
  }

  public void removeUserIdToGameIdMapping(final UUID id) {
    assert userIdToGameIdMap.get(id) != null;
    userIdToGameIdMap.remove(id);
  }

  public void endGame(final UUID id) {
    assert games.get(id) != null;
    assert decks.get(id) != null;
    assert tables.get(id) != null;
    assert summaries.get(id) != null;
    broadcastGame(id);
    publisher.publishEvent(new GameMessageEvent<>(this, GamePhaseChanged, id, GamePhase.Over));
    broadcastObfuscatedPokerTable(id);
    broadcastGameSummary(id);
    games.get(id).getPlayers().forEach(p -> userIdToGameIdMap.remove(p.getId()));
    games.remove(id);
    decks.remove(id);
    tables.remove(id);
    summaries.remove(id);
  }

  public void userJoinedGame(final UUID userId, final UUID gameId) {
    userIdToGameIdMap.put(userId, gameId);
    useCachedGameList = false;
  }

  public void userLeftGame(final UUID userId) {
    userIdToGameIdMap.remove(userId);
  }

  public Lobby getLobby(final UUID id) {
    assert lobbys.get(id) != null;
    return lobbys.get(id);
  }

  public Lobby getUsersLobby(final UUID id) {
    assert userIdToGameIdMap.get(id) != null;
    assert lobbys.get(userIdToGameIdMap.get(id)) != null;
    return lobbys.get(userIdToGameIdMap.get(id));
  }

  public void endLobby(final UUID id) {
    assert lobbys.get(id) != null;
    lobbys.get(id).getPlayers().forEach(p -> userIdToGameIdMap.remove(p.getId()));
    lobbys.remove(id);
    endGame(id);
  }

  public void startGame(final UUID id) {
    assert lobbys.get(id) != null;
    assert games.get(id) != null;
    assert tables.get(id) != null;
    assert summaries.get(id) != null;
    final com.poker.poker.models.game.Game game = games.get(id);
    game.setPlayers(lobbys.get(id).getPlayers().stream().map(GamePlayer::new).collect(toList()));
    Collections.shuffle(game.getPlayers());

    game.getPlayers()
        .forEach(p -> p.getControls().setBankRoll(lobbys.get(id).getParameters().getBuyIn()));

    summaries
        .get(id)
        .setGameData(
            game.getPlayers().stream()
                .map(p -> new DrawGameData(p, false, new ArrayList<>()))
                .collect(toList()));

    final Lobby lobby = lobbys.remove(id);
    tables.get(id).setPlayers(game.getPlayers());

    cachedGameListIsOutdated();
    broadcastObfuscatedPokerTable(id);
    broadcastGame(id);
    game.setPhase(Play);
    publisher.publishEvent(new GameMessageEvent<>(this, GamePhaseChanged, id, Play));
  }

  public com.poker.poker.models.game.PokerTable getPokerTable(final UUID id) {
    assert tables.get(id) != null;
    return tables.get(id);
  }

  public Deck getDeck(final UUID id) {
    assert decks.get(id) != null;
    return decks.get(id);
  }

  public DrawGameDataContainer getGameSummary(final UUID id) {
    assert summaries.get(id) != null;
    return summaries.get(id);
  }

  public LobbyPlayer getLobbyPlayer(final UUID playerId) {
    assert isUserInGame(playerId);
    assert lobbys.get(userIdToGameIdMap.get(playerId)) != null;
    return getLobbyPlayer(userIdToGameIdMap.get(playerId), playerId);
  }

  public LobbyPlayer getLobbyPlayer(final UUID lobbyId, final UUID playerId) {
    assert lobbys.get(lobbyId) != null;
    assert lobbys.get(lobbyId).getPlayers() != null;
    assert isUserInGame(playerId);
    assert userIdToGameIdMap.get(playerId).equals(lobbyId);

    final LobbyPlayer player =
        lobbys.get(lobbyId).getPlayers().stream()
            .filter(p -> p.getId().equals(playerId))
            .findFirst()
            .orElse(null);

    assert player != null;
    return player;
  }

  public GamePlayer getPlayer(final UUID playerId) {
    assert isUserInGame(playerId);
    assert games.get(userIdToGameIdMap.get(playerId)) != null;
    return getPlayer(userIdToGameIdMap.get(playerId), playerId);
  }

  public GamePlayer getPlayer(final UUID gameId, final UUID playerId) {
    assert games.get(gameId) != null;
    assert games.get(gameId).getPlayers() != null;
    assert isUserInGame(playerId);
    assert userIdToGameIdMap.get(playerId).equals(gameId);

    final GamePlayer player =
        games.get(gameId).getPlayers().stream()
            .filter(p -> p.getId().equals(playerId))
            .findFirst()
            .orElse(null);

    assert player != null;
    return player;
  }
}
