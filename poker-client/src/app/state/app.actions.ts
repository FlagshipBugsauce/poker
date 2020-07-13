import {createAction, props} from '@ngrx/store';
import {TopBarLobbyModel} from '../shared/models/top-bar-lobby.model';
import {
  ActionModel,
  ActiveStatusModel,
  ApiSuccessModel,
  AuthRequestModel,
  AuthResponseModel,
  CardModel,
  ChatMessageModel,
  ClientMessageModel,
  CurrentGameModel,
  DrawGameDataContainerModel,
  GameModel,
  GameParameterModel,
  GamePlayerModel,
  HandActionModel,
  HandModel,
  LobbyModel,
  LobbyPlayerModel,
  ToastModel
} from '../api/models';
import {GameListContainerModel} from '../shared/models/game-list-container.model';
import {RejoinModel} from '../shared/models/rejoin.model';
import {GamePhase} from '../shared/models/game-phase.enum';

export const navigate = createAction('[Router Service] Navigate');
export const signIn = createAction('[Auth Service] SignIn', props<AuthRequestModel>());
export const signInWithJwt = createAction(
  '[AuthGuardService] SignInWithJwt', props<{ jwt: string }>());
export const signInSuccess = createAction(
  '[Auth Service] SignInSuccess',
  props<AuthResponseModel>()
);
export const signInFail = createAction('[Auth Service] SignInFail');
export const hideFailedSignInWarning = createAction('[Login Component] HideFailWarning');
export const signOut = createAction('[Auth Service] SignOut');
export const joinLobby = createAction('[Join Component] JoinLobby', props<TopBarLobbyModel>());
export const leaveLobby = createAction('[Lobby Component] LeaveLobby');
export const createGame = createAction(
  '[Lobby Component] CreateGame',
  props<GameParameterModel>()
);
export const gameCreated = createAction(
  '[CreateGameService] GameCreated', props<ApiSuccessModel>());

export const updateCurrentGame = createAction(
  '[TopBar Component] UpdateCurrentGame', props<CurrentGameModel>());
export const requestCurrentGameUpdate = createAction(
  '[TopBar Component] RequestCurrentGameUpdate', props<ActionModel>());
export const requestCurrentGameUpdateSuccess = createAction(
  '[TopBar Component] RequestCurrentGameUpdateSuccess');
export const createGameSuccess = createAction('[Create Component] CreateGameSuccess');
export const rejoinGame = createAction('[Lobby Component] RejoinGame', props<RejoinModel>());
export const leaveGame = createAction('[Game Component] LeaveGame', props<ActionModel>());
export const unsubscribeFromGameTopics =
  createAction('[Game Components] UnsubscribeFromGameTopics');
export const leaveGameSuccess = createAction('[Game Component] LeaveGameSuccess');
export const rejoinGameSuccess = createAction('[Game Component] RejoinGameSuccess');
export const startGame = createAction('[Game Component] StartGame');
export const readyUp = createAction('[Lobby Component] ReadyUp');
export const notReady = createAction('[Lobby Component] NotReady');
export const leaveLobbySuccess = createAction('[Lobby Component] LeaveLobbySuccess');
export const joinLobbySuccess = createAction('[Lobby Component] JoinLobbySuccess');
export const startGameSuccess = createAction('[Game Component] StartGameSuccess');
export const readyUpSuccess = createAction('[Lobby Component] ReadyUpSuccess');

/* Game data actions */ // TODO: Change these strings to WebSocketService
export const gameModelUpdated = createAction(
  '[SSE Service] GameModelUpdated', props<GameModel>());
export const gameListUpdated = createAction(
  '[SSE Service] GameListDataUpdated', props<GameListContainerModel>());
export const lobbyModelUpdated = createAction(
  '[SSE Service] LobbyModelUpdated', props<LobbyModel>());
export const handModelUpdated = createAction(
  '[SSE Service] HandDocumentUpdated', props<HandModel>());
export const gameDataUpdated = createAction(
  '[SSE Service] GameDataUpdated', props<DrawGameDataContainerModel>());
export const playerDataUpdated = createAction(
  '[SSE Service] PlayerDataUpdated', props<GamePlayerModel>());
export const gameToastReceived = createAction(
  '[WebSocketService] GameToastReceived', props<ToastModel>());

// Lobby Actions
export const playerReadyToggled = createAction(
  '[WebSocketService] PlayerReadyToggled', props<LobbyPlayerModel>());
export const playerJoinedLobby = createAction(
  '[WebSocketService] PlayerJoinedLobby', props<LobbyPlayerModel>());
export const playerLeftLobby = createAction(
  '[WebSocketService] PlayerLeftLobby', props<LobbyPlayerModel>());

// Game Actions
export const gamePhaseChanged = createAction(
  '[WebSocketService] GamePhaseChanged', props<{ phase: GamePhase }>());
export const handCompleted = createAction(
  '[WebSocketService] HandCompleted', props<{ id: string }>());
export const playerAwayToggled = createAction(
  '[WebSocketService] PlayerAwayToggled', props<GamePlayerModel>());

// Hand Actions
export const handActionPerformed = createAction(
  '[WebSocketService] HandActionPerformed', props<HandActionModel>());
export const actingPlayerChanged = createAction(
  '[WebSocketService] ActingPlayerChanged', props<GamePlayerModel>());


export const drawCard = createAction('[Play Component] DrawCard');
export const drawCardSuccess = createAction('[Play Component] DrawCardSuccess');
export const cardDrawn = createAction('[WebSocketService] CardDrawn', props<CardModel>());
export const handOver = createAction('[WebSocketService] HandOver');

export const setAwayStatus = createAction(
  '[Play Component] SetActiveStatus',
  props<ActiveStatusModel>()
);
export const updateAwayStatus = createAction(
  '[Play Component] UpdateActiveStatus',
  props<ActiveStatusModel>()
);
export const setActiveStatusFail = createAction('[Play Component] SetActiveStatusFail');

// Chat Actions
export const startChat = createAction('[ChatBox] StartChat', props<{ gameId: string }>());
export const closeChat = createAction('[ChatBox] CloseChat');
export const sendChatMessage = createAction(
  '[ChatBox] SendMessage', props<ClientMessageModel>());
export const generalChatMessageReceived = createAction(
  '[WebSocketService] GeneralMessageReceived', props<ChatMessageModel>());
export const gameChatMessageReceived = createAction(
  '[WebSocketService] GameMessageReceived', props<ChatMessageModel>());
