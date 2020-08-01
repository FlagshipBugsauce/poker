import {TopBarLobbyModel} from './top-bar-lobby.model';
import {
  CardModel,
  ChatMessageModel,
  ClientUserModel,
  CurrentGameModel,
  DrawGameDataContainerModel,
  GameModel,
  GamePlayerModel,
  LobbyModel,
  PokerTableModel,
  TimerModel,
  ToastModel
} from '../../api/models';
import {GameListContainerModel} from './game-list-container.model';

export interface AppState {
  showSignInFail: boolean;
  authenticated: boolean;
  jwt?: string;
  lobbyInfo?: TopBarLobbyModel;
  lastLobbyInfo?: TopBarLobbyModel;
  loggedInUser?: ClientUserModel;
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

export interface ChatStateContainer {
  chats: ChatContainer;
}

export interface ChatContainer {
  generalChat?: ChatMessageModel;
  gameChat?: ChatMessageModel;
}

export interface PokerTableStateContainer {
  tableState: PokerTableModel;
}

export interface TimerStateContainer {
  timerState: TimerState;
}

export interface TimerState {
  timer?: TimerModel;
}
