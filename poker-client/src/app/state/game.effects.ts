import {Injectable} from '@angular/core';
import {catchError, exhaustMap, map, mergeMap} from 'rxjs/operators';
import {EMPTY} from 'rxjs';
import {GameService} from '../api/services/game.service';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {
  joinLobby,
  joinLobbySuccess,
  leaveLobby,
  leaveLobbySuccess,
  startGame,
  startGameSuccess
} from './app.actions';
import {Router} from '@angular/router';
import {APP_ROUTES} from '../app-routes';

@Injectable()
export class GameEffects {
  leaveLobby$ = createEffect(() => this.actions$.pipe(
    ofType(leaveLobby().type),
    mergeMap(() => this.gameService.leaveLobby()
      .pipe(
        map(response => ({type: leaveLobbySuccess().type, payload: response})),
        catchError(() => EMPTY)
      )
    ))
  );

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

  startGame$ = createEffect(() => this.actions$.pipe(
    ofType(startGame().type),
    mergeMap(() => this.gameService.startGame()
      .pipe(
        map(response => ({type: startGameSuccess().type, payload: response})),
        catchError(() => EMPTY)
      )
    ))
  );

  constructor(private actions$: Actions, private gameService: GameService, private router: Router) {
  }
}
