import {createAction, props} from '@ngrx/store';
import {TopBarLobbyModel} from '../shared/models/top-bar-lobby.model';
import {
  ActiveStatusModel,
  ApiSuccessModel,
  AuthRequestModel,
  AuthResponseModel,
  ChatMessageModel,
  ClientMessageModel,
  CurrentGameModel,
  DealModel,
  DrawGameDataContainerModel,
  GameActionModel,
  GameModel,
  GameParameterModel,
  GamePlayerModel,
  HideCardsModel,
  LobbyModel,
  LobbyPlayerModel,
  PokerTableModel,
  TimerModel,
  ToastModel
} from '../api/models';
import {GameListContainerModel} from '../shared/models/game-list-container.model';
import {RejoinModel} from '../shared/models/rejoin.model';
import {GamePhase} from '../shared/models/game-phase.enum';

// Authentication:
export const signIn = createAction('signIn', props<AuthRequestModel>());
export const signInWithJwt = createAction('signInWithJwt', props<{ jwt: string }>());
export const signInSuccess = createAction('signInSuccess', props<AuthResponseModel>());
export const signInFail = createAction('signInFail');
export const hideFailedSignInWarning = createAction('hideFailedSignInWarning');
export const signOut = createAction('signOut');

// Private Topic
export const requestPrivateTopic = createAction('requestPrivateTopic');

// Lobby:
export const joinLobby = createAction('joinLobby', props<TopBarLobbyModel>());
export const leaveLobby = createAction('leaveLobby');
export const createGame = createAction('createGame', props<GameParameterModel>());
export const gameCreated = createAction('gameCreated', props<ApiSuccessModel>());
export const startGame = createAction('startGame');
export const readyUp = createAction('readyUp');
export const notReady = createAction('notReady');
export const playerReadyToggled = createAction('playerReadyToggled', props<LobbyPlayerModel>());
export const playerJoinedLobby = createAction('playerJoinedLobby', props<LobbyPlayerModel>());
export const playerLeftLobby = createAction('playerLeftLobby', props<LobbyPlayerModel>());

// Game Data:
export const gameModelUpdated = createAction('gameModelUpdated', props<GameModel>());
export const gameListUpdated = createAction('gameListUpdated', props<GameListContainerModel>());
export const lobbyModelUpdated = createAction('lobbyModelUpdated', props<LobbyModel>());
export const gameDataUpdated = createAction('gameDataUpdated', props<DrawGameDataContainerModel>());
export const playerDataUpdated = createAction('playerDataUpdated', props<GamePlayerModel>());
export const gameToastReceived = createAction('gameToastReceived', props<ToastModel>());
export const unsubscribeFromGameTopics = createAction('unsubscribeFromGameTopics');
export const performGameAction = createAction('performGameAction', props<GameActionModel>());

// Poker Table:
export const gamePlayerUpdated = createAction('gamePlayerUpdated', props<GamePlayerModel>());
export const pokerTableUpdate = createAction('pokerTableUpdate', props<PokerTableModel>());
export const requestPokerTableUpdate = createAction('requestPokerTableUpdate');

// Game Actions:
export const gamePhaseChanged = createAction('gamePhaseChanged', props<{ phase: GamePhase }>());
export const handCompleted = createAction('handCompleted', props<{ id: string }>());
export const playerAwayToggled = createAction('playerAwayToggled', props<GamePlayerModel>());
export const setAwayStatus = createAction('setAwayStatus', props<ActiveStatusModel>());
export const rejoinGame = createAction('[Lobby Component] RejoinGame', props<RejoinModel>());
export const leaveGame = createAction('[Game Component] LeaveGame');
export const updateCurrentGame = createAction('updateCurrentGame', props<CurrentGameModel>());
export const requestCurrentGameUpdate = createAction('requestCurrentGameUpdate', props<ClientMessageModel>());
export const requestGameModelUpdate = createAction('requestGameModelUpdate');
export const drawCard = createAction('drawCard');

// Chat Actions:
export const startChat = createAction('startChat', props<ClientMessageModel>());
export const closeChat = createAction('closeChat');
export const sendChatMessage = createAction('sendChatMessage', props<ClientMessageModel>());
export const generalChatMsgReceived = createAction('generalChatMsgReceived', props<ChatMessageModel>());
export const gameChatMsgReceived = createAction('gameChatMsgReceived', props<ChatMessageModel>());

// Misc Events Actions:
export const startTimer = createAction('startTimer', props<TimerModel>());
export const dealCards = createAction('dealCards', props<DealModel>());
export const hideCards = createAction('hideCards', props<HideCardsModel>());
export const showCard = createAction('showCard', props<{ card: number }>());

// Private Player Data:
export const privatePlayerDataUpdated = createAction(
  'privatePlayerDataUpdated', props<GamePlayerModel>());
