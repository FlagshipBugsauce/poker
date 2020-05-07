import { Injectable } from '@angular/core';
import { CanDeactivate, Router } from '@angular/router';
import { JoinComponent } from '../join/join.component';
import { PopupComponent } from 'src/app/shared/popup/popup.component';
import { SseService } from 'src/app/shared/sse.service';
import { GameService } from 'src/app/api/services';
import { ApiSuccessModel } from 'src/app/api/models';

@Injectable({
  providedIn: 'root'
})
export class LeaveGameGuardService implements CanDeactivate<JoinComponent> {
  public confirmationPopup: PopupComponent;
  public canLeave: boolean = false;
  public link: string;

  constructor(
    private router: Router,
    private sseService: SseService,
    private gameService: GameService) { }

  canDeactivate(
    component: JoinComponent, 
    currentRoute: import("@angular/router").ActivatedRouteSnapshot, 
    currentState: import("@angular/router").RouterStateSnapshot, 
    nextState?: import("@angular/router").RouterStateSnapshot): 
    boolean | import("@angular/router").UrlTree | 
    import("rxjs").Observable<boolean | 
    import("@angular/router").UrlTree> | 
    Promise<boolean | 
    import("@angular/router").UrlTree> {
      if (!this.canLeave && this.confirmationPopup != null) {
        this.confirmationPopup.okCloseProcedure = () => {
          this.canLeave = true;
          this.sseService.closeEvent("game");
          this.gameService.leaveLobby({ Authorization: null }).subscribe((result: ApiSuccessModel) => { });
          this.router.navigate([this.link]);
        };
        this.confirmationPopup.open();
        this.link = nextState.url;
        return false;
      }
      return true;
  }
}
