import {createReducer, on} from '@ngrx/store';
import {
  navigate,
  signIn,
  signOut,
  joinLobby,
  leaveLobby,
  createGame,
  rejoinGame,
  leaveGame
} from './app.actions';
import {AppState} from '../shared/models/app-state.model';
import {TopBarLobbyModel} from '../shared/models/top-bar-lobby.model';
import {UserModel} from '../api/models';

export const initialState: AppState = {
  currentPage: '',
  authenticated: false,
  inGame: false
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
    (state: AppState) => ({...state, lastLobbyInfo: state.lobbyInfo, lobbyInfo: null})));

export function appReducer(state: AppState, action) {
  return appReducerLocal(state, action);
}

