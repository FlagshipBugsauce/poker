import {Injectable} from '@angular/core';
import {catchError, exhaustMap, map, mergeMap} from 'rxjs/operators';
import {EMPTY} from 'rxjs';
import {GameService} from '../api/services/game.service';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {
  createGame, createGameSuccess,
  drawCard,
  drawCardSuccess,
  joinLobby,
  joinLobbySuccess,
  leaveLobby,
  leaveLobbySuccess,
  readyUp,
  readyUpSuccess,
  startGame,
  startGameSuccess
} from './app.actions';
import {Router} from '@angular/router';
import {APP_ROUTES} from '../app-routes';
import {HandService} from '../api/services/hand.service';
import {CreateGameModel} from '../api/models/create-game-model';

@Injectable()
export class GameEffects {
  /**
   * Leaves the game lobby. Does not redirect because there is a confirmation that handles this.
   * TODO: Investigate if we can have the route here.
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
        map(response => ({type: startGameSuccess().type, payload: response})),
        catchError(() => EMPTY)
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
          return {type: createGameSuccess().type, payload: response};
        }),
        catchError(() => EMPTY)
      )
    ))
  );

  constructor(
    private actions$: Actions,
    private gameService: GameService,
    private handService: HandService,
    private router: Router) {
  }
}
