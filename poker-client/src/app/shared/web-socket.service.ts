import {Injectable, OnDestroy} from '@angular/core';
import {Client, over, Subscription} from 'stompjs';
import {BehaviorSubject, Observable, Subject} from 'rxjs';
import * as SockJS from 'sockjs-client';
import {filter, first, switchMap, takeUntil} from 'rxjs/operators';
import {environment} from '../../environments/environment';
import {Store} from '@ngrx/store';
import {
  AppStateContainer,
  GameDataStateContainer,
  GameListStateContainer,
  GameStateContainer,
  HandStateContainer,
  LobbyStateContainer,
  PlayerDataStateContainer
} from './models/app-state.model';
import {MessageType} from './models/message-types.enum';
import {
  gameDataUpdated,
  gameDocumentUpdated,
  gameListUpdated,
  handDocumentUpdated,
  lobbyDocumentUpdated,
  playerDataUpdated
} from '../state/app.actions';
import {selectLoggedInUser} from '../state/app.selector';
import {UserModel} from '../api/models/user-model';
import {WebSocketUpdateModel} from '../api/models/web-socket-update-model';

export enum SocketClientState {
  ATTEMPTING, CONNECTED
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService implements OnDestroy {

  constructor(
    private appStore: Store<AppStateContainer>,
    private gameDataStore: Store<GameDataStateContainer>,
    private gameStore: Store<GameStateContainer>,
    private handStore: Store<HandStateContainer>,
    private lobbyStore: Store<LobbyStateContainer>,
    private gameListStore: Store<GameListStateContainer>,
    private playerDataStore: Store<PlayerDataStateContainer>
  ) {
    this.client = over(new SockJS(environment.api));
    this.client.debug = () => {}; // TODO: Gets rid of debugging messages in console
    this.state = new BehaviorSubject<SocketClientState>(SocketClientState.ATTEMPTING);
    this.client.connect({}, () => {
      this.state.next(SocketClientState.CONNECTED);
    });

    this.appStore.select(selectLoggedInUser)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((user: UserModel) => this.user = user);
  }
  private readonly client: Client;
  private state: BehaviorSubject<SocketClientState>;
  public ngDestroyed$ = new Subject();
  private user: UserModel;

  public gameId: string = '';
  public gameTopicUnsubscribe$: Subject<any>;

  public gameListTopicUnsubscribe$: Subject<any>;
  public playerDataTopicUnsubscribe$: Subject<any>;

  private connect(): Observable<Client> {
    return new Observable<Client>(observer => {
      this.state.pipe(filter(state => state === SocketClientState.CONNECTED)).subscribe(() => {
        observer.next(this.client);
      });
    });
  }

  public ngOnDestroy(): void {
    this.connect().pipe(first()).subscribe(client => client.disconnect(null));
    this.ngDestroyed$.next();
  }

  public onMessage(topic: string): Observable<any> {
    return this.connect().pipe(first(), switchMap(client => {
      return new Observable<any>(observer => {
        const subscription: Subscription = client.subscribe(topic, message => {
          observer.next(JSON.parse(message.body));
        });
        return () => client.unsubscribe(subscription.id);
      });
    }));
  }

  send(topic: string, payload: any): void {
    this.connect()
    .pipe(first())
    .subscribe(client => client.send(topic, {}, JSON.stringify(payload)));
  }
  public subscribeToGameTopic(gameId: string): void {
    this.gameId = gameId;
    this.gameTopicUnsubscribe$ = new Subject<any>();
    this.onMessage(`/topic/game/${gameId}`).pipe(takeUntil(this.gameTopicUnsubscribe$)).subscribe(data => {
      switch (data.type) {
        case MessageType.Lobby:
          this.lobbyStore.dispatch(lobbyDocumentUpdated(data.data));
          break;
        case MessageType.Game:
          this.gameStore.dispatch(gameDocumentUpdated(data.data));
          break;
        case MessageType.Hand:
          this.handStore.dispatch(handDocumentUpdated(data.data));
          break;
        case MessageType.GameData:
          this.gameDataStore.dispatch(gameDataUpdated(data.data));
          break;
      }
    });
  }
  public gameTopicUnsubscribe(): void {
    if (this.gameTopicUnsubscribe$) {
      this.gameTopicUnsubscribe$.next();
      this.gameTopicUnsubscribe$.complete();
    }
  }

  public requestGameTopicUpdate(type: MessageType): void {
    this.requestUpdate(type, `/topic/game/${this.gameId}`, this.gameId).then();
  }

  public async requestUpdate(type: MessageType, topic: string, id: string = null): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, 100));
    this.send(`/topic/game/update`, {type, topic, id} as WebSocketUpdateModel);
    return null;
  }
  public subscribeToGameListTopic(): void {
    this.gameListTopicUnsubscribe$ = new Subject<any>();
    this.onMessage(`/topic/games`)
      .pipe(takeUntil(this.gameListTopicUnsubscribe$))
      .subscribe(data => {
        this.gameListStore.dispatch(gameListUpdated({gameList: data.data}));
      });
    this.requestUpdate(MessageType.GameList, '/topic/games').then();
  }
  public gameListTopicUnsubscribe() {
    if (this.gameListTopicUnsubscribe$) {
      this.gameListTopicUnsubscribe$.next();
      this.gameListTopicUnsubscribe$.complete();
    }
  }

  public requestPlayerDataUpdate(): void {
    this.requestUpdate(MessageType.PlayerData, `/topic/game/${this.user.id}`, this.user.id).then();
  }
  public subscribeToPlayerDataTopic() {
    this.playerDataTopicUnsubscribe$ = new Subject<any>();
    this.onMessage(`/topic/game/${this.user.id}`)
      .pipe(takeUntil(this.playerDataTopicUnsubscribe$))
      .subscribe(data => {
        this.playerDataStore.dispatch(playerDataUpdated(data.data));
      });
  }
  public playerDataTopicUnsubscribe(): void {
    if (this.playerDataTopicUnsubscribe$) {
      this.playerDataTopicUnsubscribe$.next();
      this.playerDataTopicUnsubscribe$.complete();
    }
  }
}
