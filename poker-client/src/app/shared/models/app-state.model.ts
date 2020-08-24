import {TopBarLobbyModel} from './top-bar-lobby.model';
import {
  Card,
  ChatMessage,
  ClientUser,
  CurrentGame,
  Deal,
  DrawGameDataContainer,
  Game,
  GamePlayer,
  HideCards,
  Lobby,
  PokerTable,
  Timer,
  Toast
} from '../../api/models';
import {GameListContainerModel} from './game-list-container.model';

export interface AppState {
  showSignInFail: boolean;
  authenticated: boolean;
  jwt?: string;
  lobbyInfo?: TopBarLobbyModel;
  lastLobbyInfo?: TopBarLobbyModel;
  loggedInUser?: ClientUser;
  ready: boolean;
  currentGame?: CurrentGame;
}

export interface AppStateContainer {
  appState: AppState;
}

export interface GameDataStateContainer {
  gameData: DrawGameDataContainer;
}

export interface LobbyStateContainer {
  lobbyModel: Lobby;
}

export interface GameStateContainer {
  gameModel: Game;
}

export interface GameListStateContainer {
  gameList: GameListContainerModel;
}

export interface PlayerDataStateContainer {
  playerData: GamePlayer;
}

export interface DrawnCardsStateContainer {
  drawnCards: DrawnCardsContainer;
}

export interface DrawnCardsContainer {
  drawnCards: Card[];
}

export interface ToastStateContainer {
  lastToast: Toast;
}

export interface ChatStateContainer {
  chats: ChatContainer;
}

export interface ChatContainer {
  generalChat?: ChatMessage;
  gameChat?: ChatMessage;
}

export interface PokerTableStateContainer {
  tableState: PokerTable;
}

export interface MiscEventsStateContainer {
  miscEvents: MiscEventsState;
}

export interface MiscEventsState {
  timer?: Timer;
  deal?: Deal;
  hide?: HideCards;
  hiddenCards: boolean[][];
}

export interface PrivatePlayerDataStateContainer {
  privatePlayerData: GamePlayer;
}
