import {createAction, props} from '@ngrx/store';
import {TopBarLobbyModel} from '../shared/models/top-bar-lobby.model';
import {UserModel} from '../api/models';

export const navigate = createAction('[Router Service] Navigate');
export const signIn = createAction(
  '[Auth Service] SignIn',
  props<UserModel>());
export const signOut = createAction('[Auth Service] SignOut');
export const joinLobby = createAction(
  '[Join Component] JoinLobby',
  props<TopBarLobbyModel>());
export const leaveLobby = createAction('[Lobby Component] LeaveLobby');
export const createGame = createAction('[Lobby Component] CreateGame');
export const rejoinGame = createAction('[Lobby Component] RejoinGame');
export const leaveGame = createAction('[Game Component] LeaveGame');
