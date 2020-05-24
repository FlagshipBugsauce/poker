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
    private gameService: GameService) { }

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
    if (this.sseService.gameDocument.state != null && this.sseService.gameDocument.state === GameState.Over) {
      this.sseService.closeEvent(EmitterType.Game);
      this.sseService.gameDocument = {} as GameDocument;
      return true;
    }
    if (!this.canLeave && this.confirmationPopup != null) {
      this.confirmationPopup.okCloseProcedure = () => {
        this.canLeave = true;
        this.sseService.closeEvent(EmitterType.Game);
        this.sseService.closeEvent(EmitterType.Lobby);
        this.sseService.closeEvent(EmitterType.Hand);

        if (this.sseService.gameDocument.state != null && this.sseService.gameDocument.state === GameState.Lobby) {
          this.gameService.leaveLobby().subscribe(() => { });
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
