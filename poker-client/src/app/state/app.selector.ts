import {
  AppStateContainer,
  GameDataStateContainer,
  GameListStateContainer,
  GameStateContainer,
  HandStateContainer,
  LobbyStateContainer
} from '../shared/models/app-state.model';

export const selectAuthenticated = (state: AppStateContainer) => state.appState.authenticated;
export const selectLoggedInUser = (state: AppStateContainer) => state.appState.loggedInUser;
export const selectLobbyInfo = (state: AppStateContainer) => state.appState.lobbyInfo;
export const selectLastLobbyInfo = (state: AppStateContainer) => state.appState.lastLobbyInfo;
export const selectReadyStatus = (state: AppStateContainer) => state.appState.ready;

export const selectGameList = (state: GameListStateContainer) => state.gameList.gameList;
export const selectLobbyDocument = (state: LobbyStateContainer) => state.lobbyDocument;
export const selectGameDocument = (state: GameStateContainer) => state.gameDocument;
export const selectHandDocument = (state: HandStateContainer) => state.handDocument;
export const selectGameData = (state: GameDataStateContainer) => state.gameData.gameData;
