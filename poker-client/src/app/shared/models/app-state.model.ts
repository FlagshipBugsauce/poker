import {TopBarLobbyModel} from './top-bar-lobby.model';
import {
  CardModel,
  CurrentGameModel,
  DrawGameDataContainerModel,
  GameModel,
  GamePlayerModel,
  HandModel,
  LobbyModel,
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
  lobbyModel: LobbyModel;
}

export interface GameStateContainer {
  gameModel: GameModel;
}

export interface GameListStateContainer {
  gameList: GameListContainerModel;
}

export interface HandStateContainer {
  handModel: HandModel;
}

export interface PlayerDataStateContainer {
  playerData: GamePlayerModel;
}

export interface DrawnCardsStateContainer {
  drawnCards: DrawnCardsContainer;
}

export interface DrawnCardsContainer {
  drawnCards: CardModel[];
}

export interface ToastStateContainer {
  lastToast: ToastModel;
}
