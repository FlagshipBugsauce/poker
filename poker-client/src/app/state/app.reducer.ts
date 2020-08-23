import {createReducer, on} from '@ngrx/store';
import {
  closeChat,
  dealCards,
  gameChatMsgReceived,
  gameDataUpdated,
  gameListUpdated,
  gameModelUpdated,
  gamePhaseChanged,
  gamePlayerUpdated,
  gameToastReceived,
  generalChatMsgReceived,
  hideCards,
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
  privatePlayerDataUpdated,
  readyUp,
  showCard,
  signInFail,
  signInSuccess,
  signOut,
  startTimer,
  updateCurrentGame
} from './app.actions';
import {AppState, ChatContainer, MiscEventsState} from '../shared/models/app-state.model';
import {TopBarLobbyModel} from '../shared/models/top-bar-lobby.model';
import {
  AuthResponse,
  ChatMessage,
  CurrentGame,
  Deal,
  DrawGameDataContainer,
  Game,
  GamePlayer,
  HideCards,
  Lobby,
  LobbyPlayer,
  PokerTable,
  Timer,
  Toast
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
    (state: AppState, response: AuthResponse) =>
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
    (state: AppState, currentGame: CurrentGame) => ({...state, currentGame}))
);

export function appReducer(state: AppState, action) {
  return appReducerLocal(state, action);
}

/**
 * GameData reducer and initial state.
 */
export const gameDataInitialState: DrawGameDataContainer = {
  gameData: []
};
const gameDataReducerInternal = createReducer<DrawGameDataContainer>(
  gameDataInitialState,
  on(gameDataUpdated,
    (state: DrawGameDataContainer, newState: DrawGameDataContainer) => newState));

export function gameDataReducer(state: DrawGameDataContainer, action) {
  return gameDataReducerInternal(state, action);
}

/**
 * GameModel reducer and initial state.
 */
export const gameModelInitialState: Game = {
  players: []
} as Game;
const gameModelReducerInternal = createReducer<Game>(
  gameModelInitialState,
  on(gameModelUpdated, (state: Game, newState: Game) => newState),
  on(gamePhaseChanged, (state: Game, phase: { phase: GamePhase }) =>
    ({...state, phase: phase.phase})),
  on(playerAwayToggled, (state: Game, player: GamePlayer) => {
    const players: GamePlayer[] = state
    .players.map(p => p.id === player.id ? player : ({...p}));
    return ({...state, players});
  }),
  on(gamePlayerUpdated, (state: Game, player: GamePlayer) => {
    const players: GamePlayer[] = state
    .players.map(p => p.id === player.id ? player : ({...p}));
    return ({...state, players});
  })
);

export function gameModelReducer(state: Game, action) {
  return gameModelReducerInternal(state, action);
}

/**
 * LobbyModel reducer and initial state.
 */
export const lobbyModelInitialState: Lobby = {
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
} as Lobby;
const lobbyModelReducerInternal = createReducer<Lobby>(
  lobbyModelInitialState,
  on(lobbyModelUpdated, (state: Lobby, newState: Lobby) => newState),
  on(playerReadyToggled, (state: Lobby, player: LobbyPlayer) =>
    ({...state, players: state.players.map(p => p.id === player.id ? player : p)})),
  on(playerJoinedLobby, (state: Lobby, player: LobbyPlayer) => {
    const players: LobbyPlayer[] = state.players.map(p => ({...p}));
    players.push(player);
    return ({...state, players});
  }),
  on(playerLeftLobby, (state: Lobby, player: LobbyPlayer) => {
    // Filter out player that left.
    const players: LobbyPlayer[] =
      state.players.map(p => ({...p})).filter(p => p.id !== player.id);
    // Updating host (host is always players[0]).
    let host: LobbyPlayer;
    if (players.length > 0) {
      players[0].host = true;
      host = players[0];
    }
    return ({...state, players, host});
  })
);

export function lobbyModelReducer(state: Lobby, action) {
  return lobbyModelReducerInternal(state, action);
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
export const playerDataInitialState: GamePlayer = {} as GamePlayer;
const playerDataReducerInternal = createReducer<GamePlayer>(
  playerDataInitialState,
  on(playerDataUpdated,
    (state: GamePlayer, newState: GamePlayer) => newState));

export function playerDataReducer(state: GamePlayer, action) {
  return playerDataReducerInternal(state, action);
}

// TODO: Don't think I actually need this...
export const toastDataInitialState: Toast = {} as Toast;
const toastDataReducerInternal = createReducer<Toast>(
  toastDataInitialState,
  on(gameToastReceived, (state: Toast, newState: Toast) => newState));

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
  on(generalChatMsgReceived,
    (state: ChatContainer, message: ChatMessage) =>
      ({...chatInitialState, generalChat: message})),
  on(gameChatMsgReceived,
    (state: ChatContainer, message: ChatMessage) =>
      ({...chatInitialState, gameChat: message})),
  on(closeChat, (state: ChatContainer) => chatInitialState)
);

export function chatReducer(state, action) {
  return chatReducerInternal(state, action);
}

export const pokerTableInitialState: PokerTable = {
  players: []
} as PokerTable;

const pokerTableReducerInternal = createReducer<PokerTable>(
  pokerTableInitialState,
  on(pokerTableUpdate,
    (state: PokerTable, table: PokerTable) => table),
  on(playerAwayToggled, (state: PokerTable, player: GamePlayer) => {
    const players: GamePlayer[] = state
    .players.map(p => p.id === player.id ? player : ({...p}));
    return ({...state, players});
  })
);

export function pokerTableReducer(state, action) {
  return pokerTableReducerInternal(state, action);
}

export const timerInitialState: MiscEventsState = {
  timer: {id: '0', duration: -1},
  deal: {id: '0', numCards: -1},
  hide: {id: '0'},
  hiddenCards: Array(10).fill(Array(2).fill(true))
};

const miscEventsReducerInternal = createReducer<MiscEventsState>(
  timerInitialState,
  on(startTimer, (state: MiscEventsState, timer: Timer) => ({...state, timer})),
  on(dealCards, (state: MiscEventsState, deal: Deal) => ({...state, deal})),
  on(hideCards, (state: MiscEventsState, hide: HideCards) =>
    ({...state, hide, hiddenCards: Array(10).fill(Array(2).fill(true))})),
  on(showCard, (state: MiscEventsState, cards: { player: number; card: number }) => ({
    ...state,
    hiddenCards: state.hiddenCards.map((c: boolean[], i: number) =>
      i === cards.player ? state.hiddenCards[cards.player].map((hide: boolean, j: number) =>
        j === cards.card ? false : hide) : c)
  }))
);

export function miscEventsReducer(state, action) {
  return miscEventsReducerInternal(state, action);
}

export const privatePlayerDataInitialState: GamePlayer = {};

const privatePlayerDataReducerInternal = createReducer<GamePlayer>(
  privatePlayerDataInitialState,
  on(privatePlayerDataUpdated,
    (state: GamePlayer, newState: GamePlayer) => newState));

export function privatePlayerDataReducer(state, action) {
  return privatePlayerDataReducerInternal(state, action);
}
