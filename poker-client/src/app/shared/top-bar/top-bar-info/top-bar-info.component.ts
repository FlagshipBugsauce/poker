import {Component, OnDestroy, OnInit} from '@angular/core';
import {AppStateContainer} from '../../models/app-state.model';
import {Store} from '@ngrx/store';
import {selectCurrentGame} from '../../../state/app.selector';
import {rejoinGame} from '../../../state/app.actions';
import {CurrentGame} from '../../../api/models/current-game';
import {Router} from '@angular/router';
import {APP_ROUTES} from '../../../app-routes';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';

@Component({
  selector: 'pkr-top-bar-info',
  templateUrl: './top-bar-info.component.html',
  styleUrls: ['./top-bar-info.component.scss']
})
export class TopBarInfoComponent implements OnInit, OnDestroy {
  /**
   * Contains information needed to rejoin an active game, if the player is currently in a game,
   * but is not viewing the game interface.
   */

  public currentGame: CurrentGame;
  /**
   * Used to ensure we're not maintaining multiple subscriptions.
   */
  public ngDestroyed$ = new Subject<any>();

  constructor(
    private store: Store<AppStateContainer>,
    private router: Router) {
  }

  /**
   * Flag used to determine if the user is viewing the game interface, which is used to determine
   * whether the rejoin button should be displayed.
   */
  get onGamePage(): boolean {
    return this.router.url.includes(APP_ROUTES.GAME_PREFIX.path);
  }

  public ngOnInit(): void {
    this.store.select(selectCurrentGame)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((currentGame: CurrentGame) => this.currentGame = currentGame);
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }

  public rejoinGame(): void {
    this.store.dispatch(rejoinGame({
      gameId: this.currentGame.id
    }));
  }
}
