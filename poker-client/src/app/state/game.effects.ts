import {Injectable} from '@angular/core';
import {catchError, exhaustMap, map, mergeMap, tap} from 'rxjs/operators';
import {EMPTY, of} from 'rxjs';
import {GameService} from '../api/services/game.service';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {
  createGame,
  drawCard,
  drawCardSuccess,
  gameCreated,
  joinLobby,
  leaveGame,
  leaveLobby,
  leaveLobbySuccess,
  readyUp,
  readyUpSuccess,
  rejoinGame,
  setActiveStatusFail,
  setAwayStatus,
  startGame,
  startGameSuccess,
  updateAwayStatus
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

@Injectable()
export class GameEffects {
  /**
   * Leaves the game lobby. Does not redirect because there is a confirmation that handles this.
   */
  leaveLobby$ = createEffect(() => this.actions$.pipe(
    ofType(leaveLobby().type),
    mergeMap(() => this.gameService.leaveLobby()
      .pipe(
        map(response => ({type: leaveLobbySuccess().type, payload: response})),
        catchError(() => EMPTY)
      )
    ))
  );
  /**
   * Toggles a players ready status in a game lobby.
   */
  readyUp$ = createEffect(() => this.actions$.pipe(
    ofType(readyUp),
    exhaustMap(() => this.gameService.ready()
      .pipe(
        map(response => ({type: readyUpSuccess().type, payload: response})),
        catchError(() => EMPTY)
      )
    ))
  );
  /**
   * Starts a game (will only work if dispatched by host).
   */
  startGame$ = createEffect(() => this.actions$.pipe(
    ofType(startGame().type),
    mergeMap(() => this.gameService.startGame()
      .pipe(
        map(response => {
          return {type: startGameSuccess().type, payload: response};
        }), catchError(() => EMPTY)
      )
    ))
  );
  /**
   * Draws a card.
   */
  drawCard$ = createEffect(() => this.actions$.pipe(
    ofType(drawCard),
    exhaustMap(() => this.handService.draw()
      .pipe(
        map(response => ({type: drawCardSuccess().type, payload: response})),
        catchError(() => EMPTY)
      )
    ))
  );
  /**
   * Sets the away status of the player.
   */
  setAwayStatus$ = createEffect(() => this.actions$.pipe(
    ofType(setAwayStatus),
    exhaustMap((action: ActiveStatusModel) => this.gameService.setActiveStatus({body: action})
      .pipe(
        map(
          () => updateAwayStatus(action),
          catchError(() => of({type: setActiveStatusFail().type}))
        )
      )
    ))
  );
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
    tap((action => this.lobbyJoined(action.message)))
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
        this.lobbyJoined(action.id);
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
    tap(() => this.webSocketService.send('/topic/game/leave', {jwt: this.jwt}))
  ), {dispatch: false});
  /**
   * Effect when a player rejoins an active game that they have left. Note that only a game in the
   * 'Play' phase can be rejoined.
   */
  rejoinGame$ = createEffect(() => this.actions$.pipe(
    ofType(rejoinGame),
    tap((action: RejoinModel & TypedAction<'[Lobby Component] RejoinGame'>) => {
      this.webSocketService.send('/topic/game/rejoin', {jwt: this.jwt});
      this.gameRejoined(action.gameId);
    })
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
   * Helper that will subscribe to various topics and request any updates that are required by the
   * UI when a player joins a lobby.
   * @param gameId The ID of the lobby (lobby ID is the same as game ID) that was joined.
   */
  private lobbyJoined(gameId: string): void {
    this.webSocketService.subscribeToGameTopic(gameId);
    this.webSocketService.requestGameTopicUpdate(MessageType.Game);
    this.webSocketService.requestGameTopicUpdate(MessageType.Lobby);
    this.webSocketService.gameListTopicUnsubscribe();
    this.router.navigate([`${APP_ROUTES.GAME_PREFIX.path}/${gameId}`]).then();
  }

  /**
   * Helper that will subscribe to various topics and request any updates that are required by the
   * UI when a player rejoins an active game (i.e. a game that is in 'Play' phase).
   * @param gameId The ID of the game being rejoined.
   */
  private gameRejoined(gameId: string): void {
    this.webSocketService.subscribeToGameTopic(gameId);
    this.webSocketService.subscribeToPlayerDataTopic();
    this.webSocketService.requestGameTopicUpdate(MessageType.Game);
    this.webSocketService.requestGameTopicUpdate(MessageType.GameData);
    this.webSocketService.requestGameTopicUpdate(MessageType.Hand);
    this.webSocketService.requestPlayerDataUpdate();
    this.router.navigate([`${APP_ROUTES.GAME_PREFIX.path}/${gameId}`]).then();
  }
}
