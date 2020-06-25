import {TopBarLobbyModel} from './top-bar-lobby.model';
import {UserModel} from '../../api/models';

export interface AppState {
  currentPage: string;
  authenticated: boolean;
  lobbyInfo?: TopBarLobbyModel;
  lastLobbyInfo?: TopBarLobbyModel;
  loggedInUser?: UserModel;
  inGame: boolean;
}

export interface AppStateContainer {
  appState: AppState;
}
