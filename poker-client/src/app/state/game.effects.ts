import {Injectable} from '@angular/core';
import {catchError, exhaustMap, map, mergeMap} from 'rxjs/operators';
import {EMPTY, of} from 'rxjs';
import {GameService} from '../api/services/game.service';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {
  createGame,
  createGameSuccess,
  drawCard,
  drawCardSuccess,
  joinLobby,
  joinLobbySuccess,
  leaveGame,
  leaveGameSuccess,
  leaveLobby,
  leaveLobbySuccess,
  readyUp,
  readyUpSuccess,
  rejoinGame,
  rejoinGameSuccess,
  setActiveStatusFail,
  setAwayStatus,
  startGame,
  startGameSuccess,
  updateAwayStatus
} from './app.actions';
import {Router} from '@angular/router';
import {APP_ROUTES} from '../app-routes';
import {HandService} from '../api/services/hand.service';
import {CreateGameModel} from '../api/models/create-game-model';
import {ActiveStatusModel} from '../api/models/active-status-model';
import {WebSocketService} from '../shared/web-socket.service';
import {MessageType} from '../shared/models/message-types.enum';
import {ActionModel} from '../api/models';
import {RejoinModel} from "../shared/models/rejoin.model";

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
   * Effect that will join lobby with ID provided in the prop and then navigate to the route that
   * will display the lobby.
   */
  joinLobby$ = createEffect(() => this.actions$.pipe(
    ofType(joinLobby),
    exhaustMap(action => this.gameService.joinGame({gameId: action.id})
      .pipe(
        map(response => {
          this.router.navigate([`/${APP_ROUTES.GAME_PREFIX.path}/${action.id}`]).then();
          this.gameJoined(action.id);
          return {type: joinLobbySuccess().type, payload: response};
        })
      )
    ))
  );

  /**
   * Toggles a players ready status in a game lobby.
   */
  readyUp$ = createEffect(() => this.actions$.pipe(
    ofType(readyUp),
    exhaustMap(action => this.gameService.ready()
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
   * Creates a game.
   */
  createGame$ = createEffect(() => this.actions$.pipe(
    ofType(createGame),
    mergeMap((action: CreateGameModel) => this.gameService.createGame({body: action})
      .pipe(
        map(response => {
          // TODO: Figure out how to set the top bar info
          this.router.navigate([`/${APP_ROUTES.GAME_PREFIX.path}/${response.message}`]).then();
          this.gameJoined(response.message);
          return {type: createGameSuccess().type, payload: response};
        }), catchError(() => EMPTY)
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
          response => updateAwayStatus(action),
          catchError(() => of({type: setActiveStatusFail().type}))
        )
      )
    ))
  );

  leaveGame$ = createEffect(() => this.actions$.pipe(
    ofType(leaveGame),
    mergeMap((action: ActionModel) => this.webSocketService.sendFromStore()
    .pipe(map(client => {
        client.send('/topic/game/leave', {}, JSON.stringify({
          actionType: action.actionType,
          jwt: action.jwt
        }));
        return {type: leaveGameSuccess.type};
      }, catchError(() => EMPTY)
      )
    ))
  ));

  rejoinGame$ = createEffect(() => this.actions$.pipe(
    ofType(rejoinGame),
    mergeMap((action: RejoinModel) => this.webSocketService.sendFromStore()
    .pipe(map(client => {
        client.send('/topic/game/rejoin', {}, JSON.stringify({
          actionType: 'ReJoinGame',
          jwt: action.jwt
        }));
        this.webSocketService.subscribeToGameTopic(action.gameId);
        this.webSocketService.subscribeToPlayerDataTopic();
        this.webSocketService.requestGameTopicUpdate(MessageType.Game);
        this.webSocketService.requestGameTopicUpdate(MessageType.GameData);
        this.webSocketService.requestGameTopicUpdate(MessageType.Hand);
        this.webSocketService.requestPlayerDataUpdate();
        this.router.navigate([`${APP_ROUTES.GAME_PREFIX.path}/${action.gameId}`]).then();
        return {type: rejoinGameSuccess.type};
      }, catchError(() => EMPTY)
      )
    ))
  ));

  constructor(
    private actions$: Actions,
    private webSocketService: WebSocketService,
    private gameService: GameService,
    private handService: HandService,
    private router: Router) {
  }

  private gameJoined(gameId: string): void {
    this.webSocketService.subscribeToGameTopic(gameId);
    this.webSocketService.requestGameTopicUpdate(MessageType.Game);
    this.webSocketService.requestGameTopicUpdate(MessageType.Lobby);
    this.webSocketService.gameListTopicUnsubscribe();
  }
}
