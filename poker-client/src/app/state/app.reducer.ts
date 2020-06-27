import {createReducer, on} from '@ngrx/store';
import {
  navigate,
  signIn,
  signOut,
  joinLobby,
  leaveLobby,
  createGame,
  rejoinGame,
  leaveGame,
  readyUp,
  gameDocumentUpdated,
  gameListUpdated,
  lobbyDocumentUpdated,
  handDocumentUpdated, gameDataUpdated, notReady
} from './app.actions';
import {AppState} from '../shared/models/app-state.model';
import {TopBarLobbyModel} from '../shared/models/top-bar-lobby.model';
import {
  DrawGameDataContainerModel, DrawGameDataModel, DrawGameDrawModel,
  GameDocument,
  HandDocument,
  LobbyDocument,
  UserModel
} from '../api/models';
import {GameListContainerModel} from '../shared/models/game-list-container.model';

/**
 * Reducer for general application state.
 */
export const initialState: AppState = {
  currentPage: '',
  authenticated: false,
  inGame: false,
  ready: false
};
const appReducerLocal = createReducer(
  initialState,
  on(signIn,
    (state: AppState, loggedInUser: UserModel) =>
      ({...state, authenticated: true, loggedInUser})),
  on(signOut, (state: AppState) => ({...state, authenticated: false, loggedInUser: null})),
  on(joinLobby,
    (state: AppState, lobbyInfo: TopBarLobbyModel) =>
      ({...state, lastLobbyInfo: null, lobbyInfo})),
  on(leaveLobby,
    (state: AppState) => ({...state, lastLobbyInfo: state.lobbyInfo, lobbyInfo: null})),
  on(readyUp, (state: AppState) => ({...state, ready: !state.ready})),
  on(notReady, (state: AppState) => ({...state, ready: false}))
);
export function appReducer(state: AppState, action) {
  return appReducerLocal(state, action);
}

/**
 * GameData reducer and initial state.
 */
export const gameDataInitialState: DrawGameDataContainerModel = {
  currentHand: 0,
  gameData: []
};
const gameDataReducerInternal = createReducer(
  gameDataInitialState,
  on(gameDataUpdated,
    (state: DrawGameDataContainerModel, newState: DrawGameDataContainerModel) => newState));
export function gameDataReducer(state: DrawGameDataContainerModel, action) {
  return gameDataReducerInternal(state, action);
}

/**
 * GameDocument reducer and initial state.
 */
export const gameDocumentInitialState: GameDocument = {} as GameDocument;
const gameDocumentReducerInternal = createReducer(
  gameDocumentInitialState,
  on(gameDocumentUpdated, (state: GameDocument, newState: GameDocument) => newState));
export function gameDocumentReducer(state: GameDocument, action) {
  return gameDocumentReducerInternal(state, action);
}

/**
 * LobbyDocument reducer and initial state.
 */
export const lobbyDocumentInitialState: LobbyDocument = {} as LobbyDocument;
const lobbyDocumentReducerInternal = createReducer(
  lobbyDocumentInitialState,
  on(lobbyDocumentUpdated, (state: LobbyDocument, newState: LobbyDocument) => newState));
export function lobbyDocumentReducer(state: LobbyDocument, action) {
  return lobbyDocumentReducerInternal(state, action);
}

/**
 * HandDocument reducer and initial state.
 */
export const handDocumentInitialState: HandDocument = {} as HandDocument;
const handDocumentReducerInternal = createReducer(
  handDocumentInitialState,
  on(handDocumentUpdated, (state: HandDocument, newState: HandDocument) => newState));
export function handDocumentReducer(state: HandDocument, action) {
  return handDocumentReducerInternal(state, action);
}

/**
 * GameList reducer and initial state.
 */
export const gameListInitialState: GameListContainerModel = {gameList: []};
const gameListReducerInternal = createReducer(
  gameListInitialState,
  on(gameListUpdated,
    (state: GameListContainerModel, newState: GameListContainerModel) => newState));
export function gameListReducer(state: GameListContainerModel, action) {
  return gameListReducerInternal(state, action);
}
