import {Injectable, OnDestroy} from '@angular/core';
import {WebSocketService} from './web-socket.service';
import {Subject} from 'rxjs';
import {AppStateContainer} from '../models/app-state.model';
import {Store} from '@ngrx/store';
import {selectJwt, selectLoggedInUser} from '../../state/app.selector';
import {takeUntil} from 'rxjs/operators';
import {ClientUser} from '../../api/models/client-user';
import {ClientMessage} from '../../api/models/client-message';
import {gameCreated} from '../../state/app.actions';
import {GameParameter} from '../../api/models/game-parameter';

@Injectable({
  providedIn: 'root'
})
export class CreateGameService implements OnDestroy {

  public createGameTopic = '/topic/game/create';
  private jwt: string;
  private user: ClientUser;
  private ngDestroyed$: Subject<any> = new Subject<any>();
  private createGameTopicUnsubscribe$: Subject<any>;

  constructor(
    private webSocketService: WebSocketService,
    private appStore: Store<AppStateContainer>
  ) {
    this.appStore.select(selectJwt)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe(jwt => this.jwt = jwt);
    this.appStore.select(selectLoggedInUser)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((user: ClientUser) => this.user = user);
  }

  public ngOnDestroy(): void {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }

  public subscribeToCreateGameTopic(): void {
    this.createGameTopicUnsubscribe$ = new Subject<any>();
    this.webSocketService.onMessage(`${this.createGameTopic}/${this.user.id}`)
    .pipe(takeUntil(this.createGameTopicUnsubscribe$))
    .subscribe(data => this.appStore.dispatch(gameCreated(data)));
  }

  public unsubscribeFromCreateGameTopic(): void {
    if (this.createGameTopicUnsubscribe$) {
      this.createGameTopicUnsubscribe$.next();
      this.createGameTopicUnsubscribe$.complete();
    }
  }

  public createGamePayload(data: GameParameter): ClientMessage {
    return {jwt: this.jwt, data} as ClientMessage;
  }
}
