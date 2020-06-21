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

export const initialState: AppState = {
  currentPage: '',
  authenticated: false,
  inLobby: false,
  inGame: false
};

const appReducerLocal = createReducer(
  initialState,
  on(signIn, (state: AppState) => ({...state, authenticated: true})),
  on(signOut, (state: AppState) => ({...state, authenticated: false})));

export function appReducer(state: AppState, action) {
  return appReducerLocal(state, action);
}
