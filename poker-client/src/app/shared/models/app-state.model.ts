export interface AppState {
  currentPage: string;
  authenticated: boolean;
  inLobby: boolean;
  inGame: boolean;
}

export interface AppStateContainer {
  appState: AppState;
}
