import {TopBarLobbyModel} from './top-bar-lobby.model';
import {
  CardModel,
  ChatMessageModel,
  ClientUserModel,
  CurrentGameModel,
  DealModel,
  DrawGameDataContainerModel,
  GameModel,
  GamePlayerModel,
  HideCardsModel,
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

export interface MiscEventsStateContainer {
  miscEvents: MiscEventsState;
}

export interface MiscEventsState {
  timer?: TimerModel;
  deal?: DealModel;
  hide?: HideCardsModel;
  hiddenCards: boolean[];
}

export interface PrivatePlayerDataStateContainer {
  privatePlayerData: GamePlayerModel;
}
