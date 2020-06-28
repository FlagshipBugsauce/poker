import {createAction, props} from '@ngrx/store';
import {TopBarLobbyModel} from '../shared/models/top-bar-lobby.model';
import {
  CreateGameModel,
  DrawGameDataContainerModel,
  GameDocument,
  HandDocument,
  LobbyDocument,
  UserModel
} from '../api/models';
import {GameListContainerModel} from '../shared/models/game-list-container.model';

export const navigate = createAction('[Router Service] Navigate');
export const signIn = createAction(
  '[Auth Service] SignIn',
  props<UserModel>()
);
export const signOut = createAction('[Auth Service] SignOut');
export const joinLobby = createAction('[Join Component] JoinLobby', props<TopBarLobbyModel>());
export const leaveLobby = createAction('[Lobby Component] LeaveLobby');
export const createGame = createAction(
  '[Lobby Component] CreateGame',
  props<CreateGameModel>()
);
export const createGameSuccess = createAction('[Create Component] CreateGameSuccess');
export const rejoinGame = createAction('[Lobby Component] RejoinGame');
export const leaveGame = createAction('[Game Component] LeaveGame');
export const startGame = createAction('[Game Component] StartGame');
export const readyUp = createAction('[Lobby Component] ReadyUp');
export const notReady = createAction('[Lobby Component] NotReady');

export const leaveLobbySuccess = createAction('[Lobby Component] LeaveLobbySuccess');
export const joinLobbySuccess = createAction('[Lobby Component] JoinLobbySuccess');
export const startGameSuccess = createAction('[Game Component] StartGameSuccess');
export const readyUpSuccess = createAction('[Lobby Component] ReadyUpSuccess');

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

export const drawCard = createAction('[Play Component] DrawCard');
export const drawCardSuccess = createAction('[Play Component] DrawCardSuccess');
