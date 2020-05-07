import { Injectable } from '@angular/core';
import { CanDeactivate } from '@angular/router';
import { JoinComponent } from './join.component';
import { SseService } from 'src/app/shared/sse.service';
import { GameService } from 'src/app/api/services';

@Injectable({
  providedIn: 'root'
})
export class LeaveJoinPageGuardService implements CanDeactivate<JoinComponent> {

  constructor(
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
    // Make sure we close the event and destroy the game emitter before leaving the join game page.
    this.sseService.closeEvent("joinGame");
    this.gameService.destroyJoinGameEmitter({ Authorization: null }).subscribe(() => { });
    return true;
  }
}
