import {Injectable, OnDestroy} from '@angular/core';
import {WebSocketService} from './web-socket.service';
import {Subject} from 'rxjs';
import {AppStateContainer} from '../models/app-state.model';
import {Store} from '@ngrx/store';
import {selectJwt, selectLoggedInUser} from '../../state/app.selector';
import {takeUntil} from 'rxjs/operators';
import {ClientUserModel} from '../../api/models/client-user-model';
import {ClientMessageModel} from '../../api/models/client-message-model';
import {gameCreated} from '../../state/app.actions';
import {GameParameterModel} from '../../api/models/game-parameter-model';

@Injectable({
  providedIn: 'root'
})
export class CreateGameService implements OnDestroy {

  public createGameTopic = '/topic/game/create';
  private jwt: string;
  private user: ClientUserModel;
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
    .subscribe((user: ClientUserModel) => this.user = user);
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

  public createGamePayload(data: GameParameterModel): ClientMessageModel {
    return {jwt: this.jwt, data} as ClientMessageModel;
  }
}
