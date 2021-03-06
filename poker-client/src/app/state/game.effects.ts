import {Injectable} from '@angular/core';
import {exhaustMap, first, map, mergeMap, switchMap, tap} from 'rxjs/operators';
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
  performGameAction,
  readyUp,
  refreshTable,
  rejoinGame,
  requestGameModelUpdate,
  requestPokerTableUpdate,
  setAwayStatus,
  showAllCards,
  startGame,
  unsubscribeFromGameTopics
} from './app.actions';
import {Router} from '@angular/router';
import {APP_ROUTES} from '../app-routes';
import {HandService} from '../api/services/hand.service';
import {ActiveStatus, GameActionData, GameParameter} from '../api/models';
import {WebSocketService} from '../shared/web-socket/web-socket.service';
import {MessageType} from '../shared/models/message-types.enum';
import {RejoinModel} from '../shared/models/rejoin.model';
import {CreateGameService} from '../shared/web-socket/create-game.service';
import {AppStateContainer} from '../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {selectJwt} from './app.selector';
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
    exhaustMap((action: ActiveStatus) =>
      this.gameService.setActiveStatus({body: action}))), {dispatch: false});
  /**
   * Creates a game.
   */
  createGame$ = createEffect(() => this.actions$.pipe(
    ofType(createGame),
    tap((action: GameParameter) => {
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
   * Requests update of game model.
   */
  requestUpdate$ = createEffect(() => this.actions$.pipe(
    ofType(requestGameModelUpdate),
    tap(() => this.webSocketService.requestGameTopicUpdate(MessageType.Game))),
    {dispatch: false});
  /**
   * Requests update of poker table.
   */
  requestPokerTableUpdate$ = createEffect(() => this.actions$.pipe(
    ofType(requestPokerTableUpdate),
    tap(() => this.webSocketService.requestGameTopicUpdate(MessageType.PokerTable))
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
    exhaustMap((action: RejoinModel) =>
      this.webSocketService.connect().pipe(first())
        .pipe(
          switchMap(client => {
            client.send('/topic/game/rejoin', {}, JSON.stringify({jwt: this.jwt}));
            this.subscribeToGameTopics(action.gameId);
            this.requestGameTopicUpdatesInPlayPhase();
            this.router.navigate([`${APP_ROUTES.GAME_PREFIX.path}/${action.gameId}`]).then();
            return [refreshTable(), showAllCards()];
          })
        )
    )
  ));

  /**
   * Manually requests an update from the backend, ensuring table data is accurate.
   */
  refreshTable$ = createEffect(() => this.actions$.pipe(
    ofType(refreshTable),
    tap(() => this.webSocketService.send('/topic/game/refresh-table', {jwt: this.jwt}))
  ), {dispatch: false});
  /**
   * Performs a game action (Fold, Check, Call or Raise).
   */
  performGameAction$ = createEffect(() => this.actions$.pipe(
    ofType(performGameAction),
    tap((action: GameActionData) =>
      this.webSocketService.send('/topic/game/act', {jwt: this.jwt, data: action}))
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
  }
}
