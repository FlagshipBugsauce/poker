import {
  AppState,
  AppStateContainer,
  ChatContainer,
  ChatStateContainer,
  DrawnCardsContainer,
  DrawnCardsStateContainer,
  GameDataStateContainer,
  GameListStateContainer,
  GameStateContainer,
  HandStateContainer,
  LobbyStateContainer,
  PlayerDataStateContainer,
  PokerTableStateContainer
} from '../shared/models/app-state.model';
import {createFeatureSelector, createSelector} from '@ngrx/store';
import {
  DrawGameDataContainerModel,
  GameModel,
  GamePlayerModel,
  HandModel,
  LobbyModel,
  PokerTableModel
} from '../api/models';
import {GameListContainerModel} from '../shared/models/game-list-container.model';

export const authenticatedFeature =
  createFeatureSelector<AppStateContainer, AppState>('appState');
export const selectAuthenticated = createSelector(
  authenticatedFeature, (state: AppState) => state.authenticated);

export const loggedInUserFeature =
  createFeatureSelector<AppStateContainer, AppState>('appState');
export const selectLoggedInUser = createSelector(
  loggedInUserFeature,
  (state: AppState) => state.loggedInUser
);

/** Current game selector. */
export const currentGameFeature =
  createFeatureSelector<AppStateContainer, AppState>('appState');
export const selectCurrentGame = createSelector(
  currentGameFeature,
  (state: AppState) => state.currentGame
);

export const selectLobbyInfo = (state: AppStateContainer) => state.appState.lobbyInfo;

export const selectLastLobbyInfo = (state: AppStateContainer) => state.appState.lastLobbyInfo;

export const readyStatusFeature =
  createFeatureSelector<AppStateContainer, AppState>('appState');
/** Ready status selector. */
export const selectReadyStatus = createSelector(
  readyStatusFeature,
  (state: AppState) => state.ready
);

export const jwtFeature = createFeatureSelector<AppStateContainer, AppState>('appState');
/** JWT selector. */
export const selectJwt = createSelector(jwtFeature, (state: AppState) => state.jwt);

export const signInFailFeature = createFeatureSelector<AppStateContainer, AppState>('appState');
/** SignInFail selector. */
export const selectSignInFail = createSelector(
  signInFailFeature,
  (state: AppState) => state.showSignInFail
);

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
export const selectLobbyModel = createSelector(
  lobbyFeature,
  (state: LobbyModel) => state
);

export const gameFeature =
  createFeatureSelector<GameStateContainer, GameModel>('gameModel');
/** Game document selector. */
export const selectGameModel = createSelector(
  gameFeature,
  (state: GameModel) => state
);
export const gamePhaseFeature =
  createFeatureSelector<GameStateContainer, GameModel>('gameModel');
/** Game state selector. */
export const selectGamePhase = createSelector(
  gamePhaseFeature,
  (state: GameModel) => state.phase
);

export const gamePlayersFeature =
  createFeatureSelector<GameStateContainer, GameModel>('gameModel');
export const selectGamePlayers = createSelector(
  gamePlayersFeature, (state: GameModel) => state.players);

export const handFeature =
  createFeatureSelector<HandStateContainer, HandModel>('handModel');
/** Hand document selector. */
export const selectHandModel = createSelector(
  handFeature,
  (state: HandModel) => state
);

export const handActionFeature =
  createFeatureSelector<HandStateContainer, HandModel>('handModel');
export const selectHandActions = createSelector(
  handActionFeature, (state: HandModel) => state.actions);

export const drawnCardsFeature =
  createFeatureSelector<DrawnCardsStateContainer, DrawnCardsContainer>('drawnCards');
/** Drawn cards selector. */
export const selectDrawnCards = createSelector(
  drawnCardsFeature,
  (state: DrawnCardsContainer) => state.drawnCards
);

export const gameDataFeature =
  createFeatureSelector<GameDataStateContainer, DrawGameDataContainerModel>('gameData');
/** Game data selector. */
export const selectGameData = createSelector(
  gameDataFeature,
  (state: DrawGameDataContainerModel) => state.gameData
);

export const playerDataFeature =
  createFeatureSelector<PlayerDataStateContainer, GamePlayerModel>('playerData');
/** Game data selector. */
export const selectPlayerData = createSelector(
  playerDataFeature,
  (state: GamePlayerModel) => state
);

export const awayStatusFeature = createFeatureSelector<PlayerDataStateContainer, GamePlayerModel>('playerData');
/** Away status selector. */
export const selectAwayStatus = createSelector(
  awayStatusFeature,
  (state: GamePlayerModel) => state.away
);

export const actingStatusFeature = createFeatureSelector<PlayerDataStateContainer, GamePlayerModel>('playerData');
/** Acting status selector. */
export const selectActingStatus = createSelector(
  actingStatusFeature,
  (state: GamePlayerModel) => state.acting
);

export const generalChatFeature =
  createFeatureSelector<ChatStateContainer, ChatContainer>('chats');
/** General chat selector. */
export const selectGeneralChat = createSelector(
  generalChatFeature, (state: ChatContainer) => state.generalChat);

export const gameChatFeature =
  createFeatureSelector<ChatStateContainer, ChatContainer>('chats');
export const selectGameChat = createSelector(
  gameChatFeature, (state: ChatContainer) => state.gameChat);

export const pokerTableFeature =
  createFeatureSelector<PokerTableStateContainer, PokerTableModel>('tableState');
export const selectPokerTable = createSelector(
  pokerTableFeature, (state: PokerTableModel) => state);
export const selectCardPosition = createSelector(
  pokerTableFeature, (state: PokerTableModel) => state.playerThatActed);
export const selectStartTurnTimer = createSelector(
  pokerTableFeature, (state: PokerTableModel) => state.startTurnTimer);
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
