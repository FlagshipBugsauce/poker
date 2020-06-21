import {createAction} from '@ngrx/store';

export const navigate = createAction('[Router Service] Navigate');
export const signIn = createAction('[Auth Service] SignIn');
export const signOut = createAction('[Auth Service] SignOut');
export const joinLobby = createAction('[Join Component] JoinLobby');
export const leaveLobby = createAction('[Lobby Component] LeaveLobby');
export const createGame = createAction('[Lobby Component] CreateGame');
export const rejoinGame = createAction('[Lobby Component] RejoinGame');
export const leaveGame = createAction('[Game Component] LeaveGame');
