import {Injectable} from '@angular/core';
import {CanDeactivate, Router} from '@angular/router';
import {PopupComponent} from 'src/app/shared/popup/popup.component';
import {SseService} from 'src/app/shared/sse.service';
import {GameService} from 'src/app/api/services';
import {GameComponent} from './game.component';
import {EmitterType} from 'src/app/shared/models/emitter-type.model';
import {GameState} from 'src/app/shared/models/game-state.enum';
import {GameDocument} from '../../api/models/game-document';

@Injectable({
  providedIn: 'root'
})
export class LeaveGameGuardService implements CanDeactivate<GameComponent> {
  /**
   * Popup that will appear when attempting to leave a page to notify the user of the consequences for attempting to leave.
   */
  public confirmationPopup: PopupComponent;

  /**
   * Flag that is used to determine whether the user has clicked OK on the popup.
   */
  public canLeave: boolean = false;

  /**
   * The page the user is attempting to access.
   */
  public link: string;

  constructor(
    private router: Router,
    private sseService: SseService,
    private gameService: GameService) {
  }

  canDeactivate(
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
    if (!this.sseService.gameDocument.state ||
      this.sseService.gameDocument.state === GameState.Over) {
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
        this.sseService.closeEvent(EmitterType.Game);
        this.sseService.closeEvent(EmitterType.Lobby);
        this.sseService.closeEvent(EmitterType.Hand);
        this.sseService.closeEvent(EmitterType.GameData);

        if (this.sseService.gameDocument.state && this.sseService.gameDocument.state === GameState.Lobby) {
          this.gameService.leaveLobby().subscribe();
        }

        this.router.navigate([this.link]).then();
      };
      this.confirmationPopup.open();
      this.link = nextState.url;
      return false;
    }
    return true;
  }
}
