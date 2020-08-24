import {
  AppState,
  AppStateContainer,
  ChatContainer,
  ChatStateContainer,
  GameDataStateContainer,
  GameListStateContainer,
  GameStateContainer,
  LobbyStateContainer,
  MiscEventsState,
  MiscEventsStateContainer,
  PlayerDataStateContainer,
  PokerTableStateContainer,
  PrivatePlayerDataStateContainer
} from '../shared/models/app-state.model';
import {createFeatureSelector, createSelector} from '@ngrx/store';
import {DrawGameDataContainer, Game, GamePlayer, Lobby, PokerTable} from '../api/models';
import {GameListContainerModel} from '../shared/models/game-list-container.model';

export const appFeature =
  createFeatureSelector<AppStateContainer, AppState>('appState');

export const selectAuthenticated = createSelector(
  appFeature, (state: AppState) => state.authenticated);
export const selectLoggedInUser = createSelector(
  appFeature, (state: AppState) => state.loggedInUser);
/** Current game selector. */
export const selectCurrentGame = createSelector(
  appFeature, (state: AppState) => state.currentGame);
export const selectLobbyInfo = (state: AppStateContainer) => state.appState.lobbyInfo;
export const selectLastLobbyInfo = (state: AppStateContainer) => state.appState.lastLobbyInfo;
/** Ready status selector. */
export const selectReadyStatus = createSelector(appFeature, (state: AppState) => state.ready);
/** JWT selector. */
export const selectJwt = createSelector(appFeature, (state: AppState) => state.jwt);
/** SignInFail selector. */
export const selectSignInFail = createSelector(appFeature,
  (state: AppState) => state.showSignInFail);

export const gameListFeature =
  createFeatureSelector<GameListStateContainer, GameListContainerModel>('gameList');
/** Game list selector. */
export const selectGameList = createSelector(
  gameListFeature,
  (state: GameListContainerModel) => state.gameList
);

export const lobbyFeature =
  createFeatureSelector<LobbyStateContainer, Lobby>('lobbyModel');
/** Lobby document selector. */
export const selectLobbyModel = createSelector(lobbyFeature, (state: Lobby) => state);

export const gameFeature =
  createFeatureSelector<GameStateContainer, Game>('gameModel');
/** Game document selector. */
export const selectGameModel = createSelector(gameFeature, (state: Game) => state);
/** Game state selector. */
export const selectGamePhase = createSelector(gameFeature, (state: Game) => state.phase);

export const gameDataFeature =
  createFeatureSelector<GameDataStateContainer, DrawGameDataContainer>('gameData');
/** Game data selector. */
export const selectGameData = createSelector(
  gameDataFeature, (state: DrawGameDataContainer) => state.gameData);

export const playerDataFeature =
  createFeatureSelector<PlayerDataStateContainer, GamePlayer>('playerData');
/** Game data selector. */
export const selectPlayerData = createSelector(
  playerDataFeature, (state: GamePlayer) => state);
/** Away status selector. */
export const selectAwayStatus = createSelector(
  playerDataFeature, (state: GamePlayer) => state.away);

export const chatFeature =
  createFeatureSelector<ChatStateContainer, ChatContainer>('chats');
/** General chat selector. */
export const selectGeneralChat = createSelector(
  chatFeature, (state: ChatContainer) => state.generalChat);
/** Game chat selector. */
export const selectGameChat = createSelector(
  chatFeature, (state: ChatContainer) => state.gameChat);

export const pokerTableFeature =
  createFeatureSelector<PokerTableStateContainer, PokerTable>('tableState');
export const selectPokerTable = createSelector(
  pokerTableFeature, (state: PokerTable) => state);
export const selectPlayerThatActed = createSelector(
  pokerTableFeature, (state: PokerTable) => state.playerThatActed);
export const selectPlayers = createSelector(
  pokerTableFeature, (state: PokerTable) => state.players);
export const selectDealer = createSelector(
  pokerTableFeature, (state: PokerTable) => state.dealer);
export const selectDisplayHandSummary = createSelector(
  pokerTableFeature, (state: PokerTable) => state.displayHandSummary);
export const selectActingPlayer = createSelector(
  pokerTableFeature, (state: PokerTable) => state.actingPlayer);
export const selectHandSummary = createSelector(
  pokerTableFeature, (state: PokerTable) => state.summary);
export const selectHandWinners = createSelector(
  pokerTableFeature, (state: PokerTable) => state.winners);
export const selectCommunityCards = createSelector(
  pokerTableFeature, (state: PokerTable) => state.sharedCards);

export const miscEventsFeature =
  createFeatureSelector<MiscEventsStateContainer, MiscEventsState>('miscEvents');
export const selectTimer = createSelector(miscEventsFeature,
  (state: MiscEventsState) => state.timer);
export const selectDeal = createSelector(miscEventsFeature, (state: MiscEventsState) => state.deal);
export const selectHideCards = createSelector(miscEventsFeature,
  (state: MiscEventsState) => state.hide);
export const selectHiddenCards = createSelector(miscEventsFeature,
  (state: MiscEventsState) => state.hiddenCards);

export const privatePlayerDataFeature =
  createFeatureSelector<PrivatePlayerDataStateContainer, GamePlayer>('privatePlayerData');
export const selectPrivateCards = createSelector(
  privatePlayerDataFeature, (state: GamePlayer) => state.cards);
