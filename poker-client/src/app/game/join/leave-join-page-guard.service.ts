import {Injectable} from '@angular/core';
import {CanDeactivate} from '@angular/router';
import {JoinComponent} from './join.component';
import {WebSocketService} from '../../shared/web-socket.service';

@Injectable({
  providedIn: 'root'
})
export class LeaveJoinPageGuardService implements CanDeactivate<JoinComponent> {

  constructor(
    private webSocketService: WebSocketService) {
  }

  public canDeactivate(
    component: JoinComponent,
    currentRoute: import('@angular/router').ActivatedRouteSnapshot,
    currentState: import('@angular/router').RouterStateSnapshot,
    nextState?: import('@angular/router').RouterStateSnapshot):
    boolean | import('@angular/router').UrlTree |
    import('rxjs').Observable<boolean |
      import('@angular/router').UrlTree> |
    Promise<boolean |
      import('@angular/router').UrlTree> {
    // Unsubscribe from the game list topic
    this.webSocketService.gameListTopicUnsubscribe();
    return true;
  }
}
