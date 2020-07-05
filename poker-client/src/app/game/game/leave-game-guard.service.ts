import {Injectable} from '@angular/core';
import {CanDeactivate, Router} from '@angular/router';
import {PopupComponent} from 'src/app/shared/popup/popup.component';
import {GameService} from 'src/app/api/services';
import {GameComponent} from './game.component';
import {GameState} from 'src/app/shared/models/game-state.enum';
import {AppStateContainer, GameStateContainer} from '../../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {leaveGame, leaveLobby} from '../../state/app.actions';
import {GameDocument} from '../../api/models/game-document';
import {selectGameDocument, selectJwt} from '../../state/app.selector';
import {WebSocketService} from '../../shared/web-socket.service';
import {ActionModel} from '../../api/models/action-model';

@Injectable({
  providedIn: 'root'
})
export class LeaveGameGuardService implements CanDeactivate<GameComponent> {
  /**
   * Popup that will appear when attempting to leave a page to notify the user of the consequences for attempting to leave.
   */
  public confirmationPopup: PopupComponent;

  /** Flag that is used to determine whether the user has clicked OK on the popup. */
  public canLeave: boolean = false;

  /** The page the user is attempting to access. */
  public link: string;

  public gameModel: GameDocument;

  private jwt: string;

  constructor(
    private webSocketService: WebSocketService,
    private router: Router,
    private gameService: GameService,
    private appStore: Store<AppStateContainer>,
    private gameStore: Store<GameStateContainer>) {
    this.gameStore.select(selectGameDocument)
    .subscribe((gameDocument: GameDocument) => this.gameModel = gameDocument);
    this.appStore.select(selectJwt).subscribe(jwt => this.jwt = jwt);
  }

  public canDeactivate(
    component: GameComponent,
    currentRoute: import('@angular/router').ActivatedRouteSnapshot,
    currentState: import('@angular/router').RouterStateSnapshot,
    nextState?: import('@angular/router').RouterStateSnapshot):
    boolean | import('@angular/router').UrlTree |
    import('rxjs').Observable<boolean |
      import('@angular/router').UrlTree> |
    Promise<boolean |
      import('@angular/router').UrlTree> {
    // If state is null or the game is over, then we should just let the player leave the page.
    if (!this.gameModel.state || this.gameModel.state === GameState.Over) {
      this.unsubscribe();
      return true;
    }

    /*
        If canLeave is false and the confirmationPopup reference is non-null, then we want to set
        the okCloseProcedure on the popup to set the canLeave flag to true, close any events which
        are potentially open and finally, navigate to wherever the user attempted to go. This will
        direct the user off the current page only after they confirm this is what they want, to
        avoid inadvertently leaving a game.
     */
    if (!this.canLeave && this.confirmationPopup) {
      this.confirmationPopup.okCloseProcedure = () => {
        this.canLeave = true;
        this.unsubscribe();

        if (this.gameModel.state && this.gameModel.state === GameState.Lobby) {
          this.appStore.dispatch(leaveLobby());  // Leave the lobby.
        }
        if (this.gameModel.state && this.gameModel.state === GameState.Play) {
          this.appStore.dispatch(leaveGame({
            jwt: this.jwt,
            actionType: 'LeaveGame'
          } as ActionModel));
        }

        this.router.navigate([this.link]).then();
      };
      this.confirmationPopup.open();
      this.link = nextState.url;
      return false;
    }
    return true;
  }

  /** Helper which will unsubscribe from websocket topics when they aren't needed. */
  private unsubscribe(): void {
    this.webSocketService.gameTopicUnsubscribe();
    this.webSocketService.playerDataTopicUnsubscribe();
  }
}
