import {AppStateContainer} from '../shared/models/app-state.model';

export const selectAuthenticated = (state: AppStateContainer) => state.appState.authenticated;
export const selectLoggedInUser = (state: AppStateContainer) => state.appState.loggedInUser;
export const selectLobbyInfo = (state: AppStateContainer) => state.appState.lobbyInfo;
export const selectLastLobbyInfo = (state: AppStateContainer) => state.appState.lastLobbyInfo;
