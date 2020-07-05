import {TopBarLobbyModel} from './top-bar-lobby.model';
import {
  CurrentGameModel,
  DrawGameDataContainerModel,
  GameDocument,
  GamePlayerModel,
  HandDocument,
  LobbyDocument,
  ToastModel,
  UserModel
} from '../../api/models';
import {GameListContainerModel} from './game-list-container.model';

export interface AppState {
  showSignInFail: boolean;
  authenticated: boolean;
  jwt?: string;
  lobbyInfo?: TopBarLobbyModel;
  lastLobbyInfo?: TopBarLobbyModel;
  loggedInUser?: UserModel;
  ready: boolean;
  currentGame?: CurrentGameModel;
}

export interface AppStateContainer {
  appState: AppState;
}

export interface GameDataStateContainer {
  gameData: DrawGameDataContainerModel;
}

export interface LobbyStateContainer {
  lobbyDocument: LobbyDocument;
}

export interface GameStateContainer {
  gameDocument: GameDocument;
}

export interface GameListStateContainer {
  gameList: GameListContainerModel;
}

export interface HandStateContainer {
  handDocument: HandDocument;
}

export interface PlayerDataStateContainer {
  playerData: GamePlayerModel;
}

export interface ToastStateContainer {
  lastToast: ToastModel;
}
