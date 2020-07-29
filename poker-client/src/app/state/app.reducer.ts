import {createReducer, on} from '@ngrx/store';
import {
  actingPlayerChanged,
  cardDrawn,
  closeChat,
  gameChatMessageReceived,
  gameDataUpdated,
  gameListUpdated,
  gameModelUpdated,
  gamePhaseChanged,
  gamePlayerUpdated,
  gameToastReceived,
  generalChatMessageReceived,
  handActionPerformed,
  handCompleted,
  handModelUpdated,
  handOver,
  hideFailedSignInWarning,
  joinLobby,
  leaveLobby,
  lobbyModelUpdated,
  notReady,
  playerAwayToggled,
  playerDataUpdated,
  playerJoinedLobby,
  playerLeftLobby,
  playerReadyToggled,
  pokerTableUpdate,
  readyUp,
  signInFail,
  signInSuccess,
  signOut,
  startTimer,
  TimerModel,
  updateCurrentGame
} from './app.actions';
import {
  AppState,
  ChatContainer,
  DrawnCardsContainer,
  TimerState
} from '../shared/models/app-state.model';
import {TopBarLobbyModel} from '../shared/models/top-bar-lobby.model';
import {
  AuthResponseModel,
  CardModel,
  ChatMessageModel,
  CurrentGameModel,
  DrawGameDataContainerModel,
  GameModel,
  GamePlayerModel,
  HandActionModel,
  HandModel,
  LobbyModel,
  LobbyPlayerModel,
  PokerTableModel,
  ToastModel
} from '../api/models';
import {GameListContainerModel} from '../shared/models/game-list-container.model';
import {GamePhase} from '../shared/models/game-phase.enum';

/**
 * Initial application state.
 */
export const initialState: AppState = {
  showSignInFail: false,
  jwt: '',
  authenticated: false,
  ready: false,
};
const appReducerLocal = createReducer<AppState>(
  initialState,
  on(signOut, (state: AppState) => ({
    ...state, authenticated: false,
    loggedInUser: null,
    jwt: ''
  })),
  on(
    joinLobby,
    (state: AppState, lobbyInfo: TopBarLobbyModel) =>
      ({...state, lastLobbyInfo: null, lobbyInfo})
  ),
  on(
    leaveLobby,
    (state: AppState) => ({...state, lastLobbyInfo: state.lobbyInfo, lobbyInfo: null})
  ),
  on(readyUp, (state: AppState) => ({...state, ready: !state.ready})),
  on(notReady, (state: AppState) => ({...state, ready: false})),
  on(
    signInSuccess,
    (state: AppState, response: AuthResponseModel) =>
      ({
        ...state,
        jwt: response.jwt,
        authenticated: true,
        loggedInUser: response.userDetails
      })
  ),
  on(signInFail, (state: AppState) => ({...state, showSignInFail: true})),
  on(hideFailedSignInWarning, (state: AppState) => ({...state, showSignInFail: false})),
  on(updateCurrentGame,
    (state: AppState, currentGame: CurrentGameModel) => ({...state, currentGame}))
);

export function appReducer(state: AppState, action) {
  return appReducerLocal(state, action);
}

/**
 * GameData reducer and initial state.
 */
export const gameDataInitialState: DrawGameDataContainerModel = {
  currentHand: 0,
  gameData: []
};
const gameDataReducerInternal = createReducer<DrawGameDataContainerModel>(
  gameDataInitialState,
  on(gameDataUpdated,
    (state: DrawGameDataContainerModel, newState: DrawGameDataContainerModel) => newState));

export function gameDataReducer(state: DrawGameDataContainerModel, action) {
  return gameDataReducerInternal(state, action);
}

/**
 * GameModel reducer and initial state.
 */
export const gameModelInitialState: GameModel = {
  players: []
} as GameModel;
const gameModelReducerInternal = createReducer<GameModel>(
  gameModelInitialState,
  on(gameModelUpdated, (state: GameModel, newState: GameModel) => newState),
  on(gamePhaseChanged, (state: GameModel, phase: { phase: GamePhase }) =>
    ({...state, phase: phase.phase})),
  on(handCompleted, (state: GameModel, id: { id: string }) => {
    const hands: string[] = state.hands.map(h => h);
    hands.push(id.id);
    return ({...state, hands});
  }),
  on(playerAwayToggled, (state: GameModel, player: GamePlayerModel) => {
    const players: GamePlayerModel[] = state
    .players.map(p => p.id === player.id ? player : ({...p}));
    return ({...state, players});
  }),
  on(gamePlayerUpdated, (state: GameModel, player: GamePlayerModel) => {
    const players: GamePlayerModel[] = state
    .players.map(p => p.id === player.id ? player : ({...p}));
    return ({...state, players});
  }),
  on(actingPlayerChanged, (state: GameModel, player: GamePlayerModel) => {
    const players: GamePlayerModel[] = state
    .players.map(p => p.id === player.id ? player : ({...p}));
    return ({...state, players});
  })
);

export function gameModelReducer(state: GameModel, action) {
  return gameModelReducerInternal(state, action);
}

/**
 * LobbyModel reducer and initial state.
 */
export const lobbyModelInitialState: LobbyModel = {
  parameters: {
    maxPlayers: 0,
    name: '',
    buyIn: 0
  },
  host: {
    id: '',
    firstName: '',
    lastName: ''
  },
  players: []
} as LobbyModel;
const lobbyModelReducerInternal = createReducer<LobbyModel>(
  lobbyModelInitialState,
  on(lobbyModelUpdated, (state: LobbyModel, newState: LobbyModel) => newState),
  on(playerReadyToggled, (state: LobbyModel, player: LobbyPlayerModel) =>
    ({...state, players: state.players.map(p => p.id === player.id ? player : p)})),
  on(playerJoinedLobby, (state: LobbyModel, player: LobbyPlayerModel) => {
    const players: LobbyPlayerModel[] = state.players.map(p => ({...p}));
    players.push(player);
    return ({...state, players});
  }),
  on(playerLeftLobby, (state: LobbyModel, player: LobbyPlayerModel) => {
    // Filter out player that left.
    const players: LobbyPlayerModel[] =
      state.players.map(p => ({...p})).filter(p => p.id !== player.id);
    // Updating host (host is always players[0]).
    let host: LobbyPlayerModel;
    if (players.length > 0) {
      players[0].host = true;
      host = players[0];
    }
    return ({...state, players, host});
  })
);

export function lobbyModelReducer(state: LobbyModel, action) {
  return lobbyModelReducerInternal(state, action);
}

/**
 * HandDocument initial state.
 */
export const handModelInitialState: HandModel = {
  actions: [] as HandActionModel[]
} as HandModel;
const handModelReducerInternal = createReducer<HandModel>(
  handModelInitialState,
  on(handModelUpdated, (state: HandModel, newState: HandModel) => newState),
  on(handActionPerformed, (state: HandModel, action: HandActionModel) => {
    const actions: HandActionModel[] = state.actions.map(a => ({...a}));
    actions.push(action);
    return ({...state, actions});
  }),
  on(actingPlayerChanged, (state: HandModel, player: GamePlayerModel) =>
    ({...state, acting: player}))
);

export function handModelReducer(state: HandModel, action) {
  return handModelReducerInternal(state, action);
}

/**
 * GameList initial state.
 */
export const gameListInitialState: GameListContainerModel = {gameList: []};
const gameListReducerInternal = createReducer<GameListContainerModel>(
  gameListInitialState,
  on(gameListUpdated,
    (state: GameListContainerModel, newState: GameListContainerModel) => newState));

export function gameListReducer(state: GameListContainerModel, action) {
  return gameListReducerInternal(state, action);
}

/**
 * Player data initial state.
 */
export const playerDataInitialState: GamePlayerModel = {} as GamePlayerModel;
const playerDataReducerInternal = createReducer<GamePlayerModel>(
  playerDataInitialState,
  on(playerDataUpdated,
    (state: GamePlayerModel, newState: GamePlayerModel) => newState));

export function playerDataReducer(state: GamePlayerModel, action) {
  return playerDataReducerInternal(state, action);
}

export const drawnCardsInitialState: DrawnCardsContainer = {drawnCards: []};
const drawnCardsReducerInternal = createReducer<DrawnCardsContainer>(
  drawnCardsInitialState,
  on(cardDrawn,
    (state: DrawnCardsContainer, card: CardModel) => {
      const cards: CardModel[] = state.drawnCards.map((c: CardModel) => ({...c}));
      cards.push(card);
      return {drawnCards: cards};
    }),
  on(handOver, () => ({drawnCards: []}))
);

export function drawnCardsReducer(state: DrawnCardsContainer, action) {
  return drawnCardsReducerInternal(state, action);
}

// TODO: Don't think I actually need this...
export const toastDataInitialState: ToastModel = {} as ToastModel;
const toastDataReducerInternal = createReducer<ToastModel>(
  toastDataInitialState,
  on(gameToastReceived, (state: ToastModel, newState: ToastModel) => newState));

export function toastDataReducer(state, action) {
  return toastDataReducerInternal(state, action);
}

/**
 * Chat initial state.
 */
export const chatInitialState: ChatContainer = {
  generalChat: {
    timestamp: null,
    author: null,
    message: null
  },
  gameChat: {
    timestamp: null,
    author: null,
    message: null
  }
};
const chatReducerInternal = createReducer<ChatContainer>(
  chatInitialState,
  on(generalChatMessageReceived,
    (state: ChatContainer, message: ChatMessageModel) =>
      ({...chatInitialState, generalChat: message})),
  on(gameChatMessageReceived,
    (state: ChatContainer, message: ChatMessageModel) =>
      ({...chatInitialState, gameChat: message})),
  on(closeChat, (state: ChatContainer) => chatInitialState)
);

export const pokerTableInitialState: PokerTableModel = {
  players: []
} as PokerTableModel;

export function chatReducer(state, action) {
  return chatReducerInternal(state, action);
}

const pokerTableReducerInternal = createReducer<PokerTableModel>(
  pokerTableInitialState,
  on(pokerTableUpdate,
    (state: PokerTableModel, table: PokerTableModel) => table),
  on(playerAwayToggled, (state: PokerTableModel, player: GamePlayerModel) => {
    const players: GamePlayerModel[] = state
    .players.map(p => p.id === player.id ? player : ({...p}));
    return ({...state, players});
  })
);

export function pokerTableReducer(state, action) {
  return pokerTableReducerInternal(state, action);
}

export const timerInitialState: TimerState = {timer: {id: '0', duration: -1}};

const timerReducerInternal = createReducer<TimerState>(
  timerInitialState,
  on(startTimer, (state: TimerState, timer: TimerModel) => ({timer: ({...timer})})));

export function timerReducer(state, action) {
  return timerReducerInternal(state, action);
}
