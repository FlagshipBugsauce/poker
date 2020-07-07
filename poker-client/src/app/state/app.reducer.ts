import {createReducer, on} from '@ngrx/store';
import {
  gameDataUpdated,
  gameDocumentUpdated,
  gameListUpdated,
  gameToastReceived,
  handDocumentUpdated,
  hideFailedSignInWarning,
  joinLobby,
  leaveLobby,
  lobbyDocumentUpdated,
  notReady,
  playerDataUpdated,
  readyUp,
  signInFail,
  signInSuccess,
  signOut,
  updateCurrentGame
} from './app.actions';
import {AppState} from '../shared/models/app-state.model';
import {TopBarLobbyModel} from '../shared/models/top-bar-lobby.model';
import {
  AuthResponseModel,
  CurrentGameModel,
  DrawGameDataContainerModel,
  GameDocument,
  GamePlayerModel,
  HandDocument,
  LobbyModel,
  ToastModel
} from '../api/models';
import {GameListContainerModel} from '../shared/models/game-list-container.model';

/**
 * Reducer for general application state.
 */
export const initialState: AppState = {
  showSignInFail: false,
  jwt: '',
  authenticated: false,
  ready: false,
};
const appReducerLocal = createReducer<AppState>(
  initialState,
  on(signOut, (state: AppState) => ({
    ...state, authenticated: false,
    loggedInUser: null,
    jwt: ''
  })),
  on(
    joinLobby,
    (state: AppState, lobbyInfo: TopBarLobbyModel) =>
      ({...state, lastLobbyInfo: null, lobbyInfo})
  ),
  on(
    leaveLobby,
    (state: AppState) => ({...state, lastLobbyInfo: state.lobbyInfo, lobbyInfo: null})
  ),
  on(readyUp, (state: AppState) => ({...state, ready: !state.ready})),
  on(notReady, (state: AppState) => ({...state, ready: false})),
  on(
    signInSuccess,
    (state: AppState, response: AuthResponseModel) =>
      ({
        ...state,
        jwt: response.jwt,
        authenticated: true,
        loggedInUser: response.userDetails
      })
  ),
  on(signInFail, (state: AppState) => ({...state, showSignInFail: true})),
  on(hideFailedSignInWarning, (state: AppState) => ({...state, showSignInFail: false})),
  on(updateCurrentGame,
    (state: AppState, currentGame: CurrentGameModel) => ({...state, currentGame}))
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
const gameDataReducerInternal = createReducer<DrawGameDataContainerModel>(
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
const gameDocumentReducerInternal = createReducer<GameDocument>(
  gameDocumentInitialState,
  on(gameDocumentUpdated, (state: GameDocument, newState: GameDocument) => newState));

export function gameDocumentReducer(state: GameDocument, action) {
  return gameDocumentReducerInternal(state, action);
}

/**
 * LobbyDocument reducer and initial state.
 */
export const lobbyDocumentInitialState: LobbyModel = {} as LobbyModel;
const lobbyDocumentReducerInternal = createReducer<LobbyModel>(
  lobbyDocumentInitialState,
  on(lobbyDocumentUpdated, (state: LobbyModel, newState: LobbyModel) => newState));

export function lobbyDocumentReducer(state: LobbyModel, action) {
  return lobbyDocumentReducerInternal(state, action);
}

/**
 * HandDocument reducer and initial state.
 */
export const handDocumentInitialState: HandDocument = {} as HandDocument;
const handDocumentReducerInternal = createReducer<HandDocument>(
  handDocumentInitialState,
  on(handDocumentUpdated, (state: HandDocument, newState: HandDocument) => newState));

export function handDocumentReducer(state: HandDocument, action) {
  return handDocumentReducerInternal(state, action);
}

/**
 * GameList reducer and initial state.
 */
export const gameListInitialState: GameListContainerModel = {gameList: []};
const gameListReducerInternal = createReducer<GameListContainerModel>(
  gameListInitialState,
  on(gameListUpdated,
    (state: GameListContainerModel, newState: GameListContainerModel) => newState));

export function gameListReducer(state: GameListContainerModel, action) {
  return gameListReducerInternal(state, action);
}

/**
 * GameList reducer and initial state.
 */
export const playerDataInitialState: GamePlayerModel = {} as GamePlayerModel;
const playerDataReducerInternal = createReducer<GamePlayerModel>(
  playerDataInitialState,
  on(playerDataUpdated,
    (state: GamePlayerModel, newState: GamePlayerModel) => newState));

export function playerDataReducer(state: GamePlayerModel, action) {
  return playerDataReducerInternal(state, action);
}

// TODO: Don't think I actually need this...
export const toastDataInitialState: ToastModel = {} as ToastModel;
const toastDataReducerInternal = createReducer<ToastModel>(
  toastDataInitialState,
  on(gameToastReceived, (state: ToastModel, newState: ToastModel) => newState));

export function toastDataReducer(state, action) {
  return toastDataReducerInternal(state, action);
}
