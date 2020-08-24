import {createAction, props} from '@ngrx/store';
import {TopBarLobbyModel} from '../shared/models/top-bar-lobby.model';
import {
  ActiveStatus,
  ApiSuccess,
  AuthRequest,
  AuthResponse,
  ChatMessage,
  ClientMessage,
  CurrentGame,
  Deal,
  DrawGameDataContainer,
  Game,
  GameActionData,
  GameParameter,
  GamePlayer,
  HideCards,
  Lobby,
  LobbyPlayer,
  PokerTable,
  Timer,
  Toast
} from '../api/models';
import {GameListContainerModel} from '../shared/models/game-list-container.model';
import {RejoinModel} from '../shared/models/rejoin.model';
import {GamePhase} from '../shared/models/game-phase.enum';

// Authentication:
export const signIn = createAction('signIn', props<AuthRequest>());
export const signInWithJwt = createAction('signInWithJwt', props<{ jwt: string }>());
export const signInSuccess = createAction('signInSuccess', props<AuthResponse>());
export const signInFail = createAction('signInFail');
export const hideFailedSignInWarning = createAction('hideFailedSignInWarning');
export const signOut = createAction('signOut');

// Private Topic
export const requestPrivateTopic = createAction('requestPrivateTopic');

// Lobby:
export const joinLobby = createAction('joinLobby', props<TopBarLobbyModel>());
export const leaveLobby = createAction('leaveLobby');
export const createGame = createAction('createGame', props<GameParameter>());
export const gameCreated = createAction('gameCreated', props<ApiSuccess>());
export const startGame = createAction('startGame');
export const readyUp = createAction('readyUp');
export const notReady = createAction('notReady');
export const playerReadyToggled = createAction('playerReadyToggled', props<LobbyPlayer>());
export const playerJoinedLobby = createAction('playerJoinedLobby', props<LobbyPlayer>());
export const playerLeftLobby = createAction('playerLeftLobby', props<LobbyPlayer>());

// Game Data:
export const gameModelUpdated = createAction('gameModelUpdated', props<Game>());
export const gameListUpdated = createAction('gameListUpdated', props<GameListContainerModel>());
export const lobbyModelUpdated = createAction('lobbyModelUpdated', props<Lobby>());
export const gameDataUpdated = createAction('gameDataUpdated', props<DrawGameDataContainer>());
export const playerDataUpdated = createAction('playerDataUpdated', props<GamePlayer>());
export const gameToastReceived = createAction('gameToastReceived', props<Toast>());
export const unsubscribeFromGameTopics = createAction('unsubscribeFromGameTopics');
export const performGameAction = createAction('performGameAction', props<GameActionData>());

// Poker Table:
export const gamePlayerUpdated = createAction('gamePlayerUpdated', props<GamePlayer>());
export const pokerTableUpdate = createAction('pokerTableUpdate', props<PokerTable>());
export const requestPokerTableUpdate = createAction('requestPokerTableUpdate');

// Game Actions:
export const gamePhaseChanged = createAction('gamePhaseChanged', props<{ phase: GamePhase }>());
export const handCompleted = createAction('handCompleted', props<{ id: string }>());
export const playerAwayToggled = createAction('playerAwayToggled', props<GamePlayer>());
export const setAwayStatus = createAction('setAwayStatus', props<ActiveStatus>());
export const rejoinGame = createAction('[Lobby Component] RejoinGame', props<RejoinModel>());
export const leaveGame = createAction('[Game Component] LeaveGame');
export const updateCurrentGame = createAction('updateCurrentGame', props<CurrentGame>());
export const requestCurrentGameUpdate = createAction('requestCurrentGameUpdate', props<ClientMessage>());
export const requestGameModelUpdate = createAction('requestGameModelUpdate');
export const drawCard = createAction('drawCard');

// Chat Actions:
export const startChat = createAction('startChat', props<ClientMessage>());
export const closeChat = createAction('closeChat');
export const sendChatMessage = createAction('sendChatMessage', props<ClientMessage>());
export const generalChatMsgReceived = createAction('generalChatMsgReceived', props<ChatMessage>());
export const gameChatMsgReceived = createAction('gameChatMsgReceived', props<ChatMessage>());

// Misc Events Actions:
export const startTimer = createAction('startTimer', props<Timer>());
export const dealCards = createAction('dealCards', props<Deal>());
export const hideCards = createAction('hideCards', props<HideCards>());
export const showCard = createAction('showCard', props<{ player: number, card: number }>());

// Private Player Data:
export const privatePlayerDataUpdated = createAction(
  'privatePlayerDataUpdated', props<GamePlayer>());
