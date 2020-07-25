package com.poker.poker.services.game;

import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.models.game.DeckModel;
import com.poker.poker.models.game.GameModel;
import com.poker.poker.models.game.HandModel;
import com.poker.poker.models.game.LobbyModel;
import com.poker.poker.models.game.PokerTableModel;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
@AllArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GameDataService {

  private final GameConstants gameConstants;
  private final Map<UUID, GameModel> games;
  private final Map<UUID, LobbyModel> lobbys;
  private final Map<UUID, HandModel> hands;
  private final Map<UUID, DeckModel> decks;
  private final Map<UUID, PokerTableModel> tables;

  /**
   * Mapping from user ID to game ID. Can be used to retrieve the game a user is in.
   */
  private final Map<UUID, UUID> userIdToGameIdMap;
  private final Map<UUID, UUID> gameIdToHandIdMap;

  public void newGame(final GameModel game) {
    assert games.get(game.getId()) == null;
    assert decks.get(game.getId()) == null;
    games.put(game.getId(), game);
    decks.put(game.getId(), new DeckModel());
  }

  public GameModel getGame(final UUID id) {
    assert games.get(id) != null;
    return games.get(id);
  }

  public GameModel getUsersGame(final UUID id) {
    assert userIdToGameIdMap.get(id) != null;
    assert games.get(userIdToGameIdMap.get(id)) != null;
    return games.get(userIdToGameIdMap.get(id));
  }

  public boolean isUserInGame(final UUID id) {
    return userIdToGameIdMap.get(id) != null && games.get(userIdToGameIdMap.get(id)) != null;
  }

  public void endGame(final UUID id) {
    assert games.get(id) != null;
    assert decks.get(id) != null;
    assert tables.get(id) != null;
    games.get(id).getPlayers().forEach(p -> userIdToGameIdMap.remove(p.getId()));
    games.remove(id);
    decks.remove(id);
    tables.remove(id);
  }

  public void userJoinedGame(final UUID userId, final UUID gameId) {
    userIdToGameIdMap.put(userId, gameId);
  }

  public void userLeftGame(final UUID userId) {
    userIdToGameIdMap.remove(userId);
  }

  public void newLobby(final LobbyModel lobby) {
    assert lobbys.get(lobby.getId()) == null;
    lobbys.put(lobby.getId(), lobby);
  }

  public LobbyModel getLobby(final UUID id) {
    assert lobbys.get(id) != null;
    return lobbys.get(id);
  }

  public LobbyModel getUsersLobby(final UUID id) {
    assert userIdToGameIdMap.get(id) != null;
    assert lobbys.get(userIdToGameIdMap.get(id)) != null;
    return lobbys.get(userIdToGameIdMap.get(id));
  }

  public void endLobby(final UUID id) {
    assert lobbys.get(id) != null;
    lobbys.remove(id);
  }

  public void newPokerTable(final UUID id, final PokerTableModel table) {
    assert tables.get(id) == null;
    tables.put(id, table);
  }

  public PokerTableModel getPokerTable(final UUID id) {
    assert tables.get(id) != null;
    return tables.get(id);
  }

  public void newHand(final UUID id, final HandModel hand) {
    assert hands.get(hand.getId()) == null;
    assert gameIdToHandIdMap.get(id) == null;
    hands.put(hand.getId(), hand);
    gameIdToHandIdMap.put(id, hand.getId());
  }

  public void removeHand(final UUID id) {
    assert hands.get(id) != null;
    hands.remove(id);
  }

  public void endHand(final UUID gameId) {
    assert gameIdToHandIdMap.get(gameId) != null;
    assert hands.get(gameIdToHandIdMap.get(gameId)) != null;
    hands.remove(gameIdToHandIdMap.get(gameId));
    gameIdToHandIdMap.remove(gameId);
  }

  public void endHand(final HandModel hand) {
    assert hands.get(hand.getId()) != null;
    assert gameIdToHandIdMap.get(hand.getGameId()) != null;
    hands.remove(hand.getId());
    gameIdToHandIdMap.remove(hand.getGameId());
  }
}
