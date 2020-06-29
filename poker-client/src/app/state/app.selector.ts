import {
  AppState,
  AppStateContainer,
  GameDataStateContainer,
  GameListStateContainer,
  GameStateContainer,
  HandStateContainer,
  LobbyStateContainer, PlayerDataStateContainer
} from '../shared/models/app-state.model';
import {createFeatureSelector, createSelector} from '@ngrx/store';
import {HandDocument} from '../api/models/hand-document';
import {
  DrawGameDataContainerModel,
  GameDocument,
  GamePlayerModel,
  LobbyDocument
} from '../api/models';
import {GameListContainerModel} from '../shared/models/game-list-container.model';

export const selectAuthenticated = (state: AppStateContainer) => state.appState.authenticated;

export const loggedInUserFeature =
  createFeatureSelector<AppStateContainer, AppState>('appState');
export const selectLoggedInUser = createSelector(
  loggedInUserFeature,
  (state: AppState) => state.loggedInUser
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
  createFeatureSelector<LobbyStateContainer, LobbyDocument>('lobbyDocument');
/** Lobby document selector. */
export const selectLobbyDocument = createSelector(
  lobbyFeature,
  (state: LobbyDocument) => state
);

export const gameFeature =
  createFeatureSelector<GameStateContainer, GameDocument>('gameDocument');
/** Game document selector. */
export const selectGameDocument = createSelector(
  gameFeature,
  (state: GameDocument) => state
);

export const handFeature =
  createFeatureSelector<HandStateContainer, HandDocument>('handDocument');
/** Hand document selector. */
export const selectHandDocument = createSelector(
  handFeature,
  (state: HandDocument) => state
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
