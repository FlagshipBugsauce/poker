import {createAction, props} from '@ngrx/store';
import {TopBarLobbyModel} from '../shared/models/top-bar-lobby.model';
import {
  ActionModel,
  ActiveStatusModel,
  AuthRequestModel,
  AuthResponseModel,
  CreateGameModel,
  CurrentGameModel,
  DrawGameDataContainerModel,
  GameDocument,
  GamePlayerModel,
  HandDocument,
  LobbyDocument,
  ToastModel
} from '../api/models';
import {GameListContainerModel} from '../shared/models/game-list-container.model';
import {RejoinModel} from '../shared/models/rejoin.model';

export const navigate = createAction('[Router Service] Navigate');
export const signIn = createAction('[Auth Service] SignIn', props<AuthRequestModel>());
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
  props<CreateGameModel>()
);

export const updateCurrentGame = createAction(
  '[TopBar Component] UpdateCurrentGame', props<CurrentGameModel>());
export const requestCurrentGameUpdate = createAction(
  '[TopBar Component] RequestCurrentGameUpdate', props<ActionModel>());
export const requestCurrentGameUpdateSuccess = createAction(
  '[TopBar Component] RequestCurrentGameUpdateSuccess');
export const createGameSuccess = createAction('[Create Component] CreateGameSuccess');
export const rejoinGame = createAction('[Lobby Component] RejoinGame', props<RejoinModel>());
export const leaveGame = createAction('[Game Component] LeaveGame', props<ActionModel>());
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
export const gameDocumentUpdated = createAction(
  '[SSE Service] GameDocumentUpdated', props<GameDocument>());
export const gameListUpdated = createAction(
  '[SSE Service] GameListDataUpdated', props<GameListContainerModel>());
export const lobbyDocumentUpdated = createAction(
  '[SSE Service] LobbyDocumentUpdated', props<LobbyDocument>());
export const handDocumentUpdated = createAction(
  '[SSE Service] HandDocumentUpdated', props<HandDocument>());
export const gameDataUpdated = createAction(
  '[SSE Service] GameDataUpdated', props<DrawGameDataContainerModel>());
export const playerDataUpdated = createAction(
  '[SSE Service] PlayerDataUpdated', props<GamePlayerModel>());
export const gameToastReceived = createAction(
  '[WebSocketService] GameToastReceived', props<ToastModel>());

export const drawCard = createAction('[Play Component] DrawCard');
export const drawCardSuccess = createAction('[Play Component] DrawCardSuccess');

export const setAwayStatus = createAction(
  '[Play Component] SetActiveStatus',
  props<ActiveStatusModel>()
);
export const updateAwayStatus = createAction(
  '[Play Component] UpdateActiveStatus',
  props<ActiveStatusModel>()
);
export const setActiveStatusFail = createAction('[Play Component] SetActiveStatusFail');
