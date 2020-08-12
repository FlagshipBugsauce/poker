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
import {
  DrawGameDataContainerModel,
  GameModel,
  GamePlayerModel,
  LobbyModel,
  PokerTableModel
} from '../api/models';
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
  createFeatureSelector<LobbyStateContainer, LobbyModel>('lobbyModel');
/** Lobby document selector. */
export const selectLobbyModel = createSelector(lobbyFeature, (state: LobbyModel) => state);

export const gameFeature =
  createFeatureSelector<GameStateContainer, GameModel>('gameModel');
/** Game document selector. */
export const selectGameModel = createSelector(gameFeature, (state: GameModel) => state);
/** Game state selector. */
export const selectGamePhase = createSelector(gameFeature, (state: GameModel) => state.phase);

export const gameDataFeature =
  createFeatureSelector<GameDataStateContainer, DrawGameDataContainerModel>('gameData');
/** Game data selector. */
export const selectGameData = createSelector(
  gameDataFeature, (state: DrawGameDataContainerModel) => state.gameData);

export const playerDataFeature =
  createFeatureSelector<PlayerDataStateContainer, GamePlayerModel>('playerData');
/** Game data selector. */
export const selectPlayerData = createSelector(
  playerDataFeature, (state: GamePlayerModel) => state);
/** Away status selector. */
export const selectAwayStatus = createSelector(
  playerDataFeature, (state: GamePlayerModel) => state.away);

export const chatFeature =
  createFeatureSelector<ChatStateContainer, ChatContainer>('chats');
/** General chat selector. */
export const selectGeneralChat = createSelector(
  chatFeature, (state: ChatContainer) => state.generalChat);
/** Game chat selector. */
export const selectGameChat = createSelector(
  chatFeature, (state: ChatContainer) => state.gameChat);

export const pokerTableFeature =
  createFeatureSelector<PokerTableStateContainer, PokerTableModel>('tableState');
export const selectPokerTable = createSelector(
  pokerTableFeature, (state: PokerTableModel) => state);
export const selectPlayerThatActed = createSelector(
  pokerTableFeature, (state: PokerTableModel) => state.playerThatActed);
export const selectPlayers = createSelector(
  pokerTableFeature, (state: PokerTableModel) => state.players);
export const selectDealer = createSelector(
  pokerTableFeature, (state: PokerTableModel) => state.dealer);
export const selectDisplayHandSummary = createSelector(
  pokerTableFeature, (state: PokerTableModel) => state.displayHandSummary);
export const selectActingPlayer = createSelector(
  pokerTableFeature, (state: PokerTableModel) => state.actingPlayer);
export const selectHandSummary = createSelector(
  pokerTableFeature, (state: PokerTableModel) => state.summary);
export const selectHandWinners = createSelector(
  pokerTableFeature, (state: PokerTableModel) => state.winners);

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
  createFeatureSelector<PrivatePlayerDataStateContainer, GamePlayerModel>('privatePlayerData');
export const selectPrivateCards = createSelector(
  privatePlayerDataFeature, (state: GamePlayerModel) => state.cards);
