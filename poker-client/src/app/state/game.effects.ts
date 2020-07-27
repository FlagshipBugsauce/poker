import {Injectable} from '@angular/core';
import {exhaustMap, map, mergeMap, tap} from 'rxjs/operators';
import {GameService} from '../api/services/game.service';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {
  createGame,
  drawCard,
  gameCreated,
  gamePhaseChanged,
  joinLobby,
  leaveGame,
  leaveLobby,
  readyUp,
  rejoinGame,
  requestGameModelUpdate,
  requestPokerTableUpdate,
  setAwayStatus,
  startGame,
  unsubscribeFromGameTopics
} from './app.actions';
import {Router} from '@angular/router';
import {APP_ROUTES} from '../app-routes';
import {HandService} from '../api/services/hand.service';
import {ActiveStatusModel} from '../api/models/active-status-model';
import {WebSocketService} from '../shared/web-socket/web-socket.service';
import {MessageType} from '../shared/models/message-types.enum';
import {RejoinModel} from '../shared/models/rejoin.model';
import {CreateGameService} from '../shared/web-socket/create-game.service';
import {AppStateContainer} from '../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {selectJwt} from './app.selector';
import {TypedAction} from '@ngrx/store/src/models';
import {GameParameterModel} from '../api/models/game-parameter-model';
import {GamePhase} from '../shared/models/game-phase.enum';

@Injectable()
export class GameEffects {
  /**
   * Leaves the game lobby. Does not redirect because there is a confirmation that handles this.
   */
  leaveLobby$ = createEffect(() => this.actions$.pipe(
    ofType(leaveLobby().type),
    mergeMap(() => this.gameService.leaveLobby()
    .pipe(map(() => this.unsubscribeFromGameTopics())))), {dispatch: false});
  /**
   * Toggles a players ready status in a game lobby.
   */
  readyUp$ = createEffect(() => this.actions$.pipe(
    ofType(readyUp), exhaustMap(() => this.gameService.ready())),
    {dispatch: false});
  /**
   * Starts a game (will only work if dispatched by host).
   */
  startGame$ = createEffect(() => this.actions$.pipe(
    ofType(startGame().type),
    mergeMap(() => this.gameService.startGame())), {dispatch: false});
  /**
   * Draws a card.
   */
  drawCard$ = createEffect(() => this.actions$.pipe(
    ofType(drawCard), exhaustMap(() => this.handService.draw())), {dispatch: false});
  /**
   * Sets the away status of the player.
   */
  setAwayStatus$ = createEffect(() => this.actions$.pipe(
    ofType(setAwayStatus),
    exhaustMap((action: ActiveStatusModel) =>
      this.gameService.setActiveStatus({body: action}))), {dispatch: false});
  /**
   * Creates a game.
   */
  createGame$ = createEffect(() => this.actions$.pipe(
    ofType(createGame),
    tap((action: GameParameterModel & TypedAction<'[Lobby Component] CreateGame'>) => {
      this.webSocketService.send(
        this.createGameService.createGameTopic,
        this.createGameService.createGamePayload(
          {buyIn: action.buyIn, maxPlayers: action.maxPlayers, name: action.name}));
    })
  ), {dispatch: false});
  /**
   * Effect when a game has been created.
   */
  gameCreated$ = createEffect(() => this.actions$.pipe(
    ofType(gameCreated),
    tap((action => {
      this.subscribeToGameTopics(action.message);
      this.requestGameTopicUpdatesInLobbyPhase();
      this.router.navigate([`${APP_ROUTES.GAME_PREFIX.path}/${action.message}`]).then();
    }))
  ), {dispatch: false});
  /**
   * Effect that will unsubscribe from game topics (typically dispatched when leaving the game
   * page).
   */
  unsubscribeFromGameTopics$ = createEffect(() => this.actions$.pipe(
    ofType(unsubscribeFromGameTopics),
    tap(() => this.unsubscribeFromGameTopics())), {dispatch: false});
  /**
   * Effect that will request a game data and hand update when the game starts. This is needed by
   * the UI in order to display the game data table correctly as soon as the game begins.
   */
  gamePhaseChanged$ = createEffect(() => this.actions$.pipe(
    ofType(gamePhaseChanged),
    tap((action: { phase: GamePhase }) => {
      if (action.phase === GamePhase.Play) {
        this.webSocketService.requestGameTopicUpdate(MessageType.Hand);
        this.webSocketService.requestGameTopicUpdate(MessageType.GameData);
        this.webSocketService.requestPlayerDataUpdate();
      }
    })
  ), {dispatch: false});
  /**
   * JWT for the logged in user.
   */
  private jwt: string;
  /**
   * Effect that will join lobby with ID provided in the prop and then navigate to the route that
   * will display the lobby.
   */
  joinLobby$ = createEffect(() => this.actions$.pipe(
    ofType(joinLobby),
    tap(action => {
        this.webSocketService.send('/topic/game/join', {jwt: this.jwt, gameId: action.id});
        // this.lobbyJoined(action.id);
        this.subscribeToGameTopics(action.id);
        this.requestGameTopicUpdatesInLobbyPhase();
        this.router.navigate([`${APP_ROUTES.GAME_PREFIX.path}/${action.id}`]).then();
      }
    )
  ), {dispatch: false});
  /**
   * Effect when player leaves an active game, as in, leaves a game that is in the 'Play' phase.
   * This will send a message to the server and the server will set the players away status to true,
   * which will result in the server performing default actions immediately whenever it is the
   * player's turn to act.
   */
  leaveGame$ = createEffect(() => this.actions$.pipe(
    ofType(leaveGame),
    tap(() => {
      this.webSocketService.send('/topic/game/leave', {jwt: this.jwt});
      this.unsubscribeFromGameTopics();
    })
  ), {dispatch: false});
  /**
   * Effect when a player rejoins an active game that they have left. Note that only a game in the
   * 'Play' phase can be rejoined.
   */
  rejoinGame$ = createEffect(() => this.actions$.pipe(
    ofType(rejoinGame),
    tap((action: RejoinModel & TypedAction<'[Lobby Component] RejoinGame'>) => {
      this.webSocketService.send('/topic/game/rejoin', {jwt: this.jwt});
      this.subscribeToGameTopics(action.gameId);
      this.requestGameTopicUpdatesInPlayPhase();
      this.router.navigate([`${APP_ROUTES.GAME_PREFIX.path}/${action.gameId}`]).then();
    })
  ), {dispatch: false});

  requestUpdate$ = createEffect(() => this.actions$.pipe(
    ofType(requestGameModelUpdate),
    tap(() => this.webSocketService.requestGameTopicUpdate(MessageType.Game))),
    {dispatch: false});

  requestPokerTableUpdate$ = createEffect(() => this.actions$.pipe(
    ofType(requestPokerTableUpdate),
    tap(() => this.webSocketService.requestGameTopicUpdate(MessageType.PokerTable))
  ), {dispatch: false});

  constructor(
    private actions$: Actions,
    private webSocketService: WebSocketService,
    private createGameService: CreateGameService,
    private gameService: GameService,
    private handService: HandService,
    private router: Router,
    private appStore: Store<AppStateContainer>) {
    this.appStore.select(selectJwt).subscribe(jwt => this.jwt = jwt);
  }

  /**
   * Helper that will subscribe to all game topics required by the UI.
   * @param gameId ID of the game, used to determine which topic to subscribe to.
   */
  private subscribeToGameTopics(gameId: string): void {
    this.webSocketService.subscribeToGameTopic(gameId);
    this.webSocketService.subscribeToPlayerDataTopic();
    this.webSocketService.subscribeToDrawnCardsTopic(gameId);
  }

  /**
   * Requests updates that are required to load the UI in the 'Lobby' phase of the game.
   */
  private requestGameTopicUpdatesInLobbyPhase(): void {
    this.webSocketService.requestGameTopicUpdate(MessageType.Game);
    this.webSocketService.requestGameTopicUpdate(MessageType.Lobby);
  }

  /**
   * Requests updates that are required to load the UI in the 'Play' phase of the game.
   */
  private requestGameTopicUpdatesInPlayPhase(): void {
    this.webSocketService.requestGameTopicUpdate(MessageType.Game);
    this.webSocketService.requestGameTopicUpdate(MessageType.GameData);
    this.webSocketService.requestGameTopicUpdate(MessageType.Hand);
    this.webSocketService.requestPlayerDataUpdate();
  }

  /**
   * Helper that unsubscribes from all game topics.
   */
  private unsubscribeFromGameTopics(): void {
    this.webSocketService.gameTopicUnsubscribe();
    this.webSocketService.playerDataTopicUnsubscribe();
    this.webSocketService.drawnCardsTopicUnsubscribe();
  }
}
