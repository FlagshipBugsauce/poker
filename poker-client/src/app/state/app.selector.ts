import {
  AppState,
  AppStateContainer,
  GameDataStateContainer,
  GameListStateContainer,
  GameStateContainer,
  HandStateContainer,
  LobbyStateContainer
} from '../shared/models/app-state.model';
import {createFeatureSelector, createSelector} from '@ngrx/store';
import {HandDocument} from '../api/models/hand-document';
import {DrawGameDataContainerModel, GameDocument, LobbyDocument} from '../api/models';
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

// export const selectReadyStatus = (state: AppStateContainer) => state.appState.ready;

export const readyStatusFeature =
  createFeatureSelector<AppStateContainer, AppState>('appState');
export const selectReadyStatus = createSelector(
  readyStatusFeature,
  (state: AppState) => state.ready
);


export const gameListFeature =
  createFeatureSelector<GameListStateContainer, GameListContainerModel>('gameList');
/**
 * Game list selector.
 */
export const selectGameList = createSelector(
  gameListFeature,
  (state: GameListContainerModel) => state.gameList
);

export const lobbyFeature =
  createFeatureSelector<LobbyStateContainer, LobbyDocument>('lobbyDocument');
/**
 * Lobby document selector.
 */
export const selectLobbyDocument = createSelector(
  lobbyFeature,
  (state: LobbyDocument) => state
);

export const gameFeature =
  createFeatureSelector<GameStateContainer, GameDocument>('gameDocument');
/**
 * Game document selector.
 */
export const selectGameDocument = createSelector(
  gameFeature,
  (state: GameDocument) => state
);

export const handFeature =
  createFeatureSelector<HandStateContainer, HandDocument>('handDocument');
/**
 * Hand document selector.
 */
export const selectHandDocument = createSelector(
  handFeature,
  (state: HandDocument) => state
);

export const gameDataFeature =
  createFeatureSelector<GameDataStateContainer, DrawGameDataContainerModel>('gameData');
/**
 * Game data selector.
 */
export const selectGameData = createSelector(
  gameDataFeature,
  (state: DrawGameDataContainerModel) => state.gameData
);
