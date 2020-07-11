/* tslint:disable */
import {Injectable, OnDestroy} from '@angular/core';
import {Client, over, Subscription} from 'stompjs';
import {BehaviorSubject, Observable, Subject} from 'rxjs';
import * as SockJS from 'sockjs-client';
import {filter, first, switchMap, takeUntil} from 'rxjs/operators';
import {environment} from '../../../environments/environment';
import {Store} from '@ngrx/store';
import {
  AppStateContainer, DrawnCardsStateContainer,
  GameDataStateContainer,
  GameListStateContainer,
  GameStateContainer,
  HandStateContainer,
  LobbyStateContainer,
  PlayerDataStateContainer
} from '../models/app-state.model';
import {MessageType} from '../models/message-types.enum';
import {
  gameDataUpdated,
  gameModelUpdated,
  gameListUpdated,
  handModelUpdated,
  lobbyModelUpdated,
  playerDataUpdated,
  updateCurrentGame,
  cardDrawn,
  handOver,
  playerReadyToggled,
  playerJoinedLobby,
  playerLeftLobby,
  gamePhaseChanged, handCompleted, playerAwayToggled, handActionPerformed, actingPlayerChanged
} from '../../state/app.actions';
import {selectLoggedInUser} from '../../state/app.selector';
import {UserModel} from '../../api/models/user-model';
import {WebSocketUpdateModel} from '../../api/models/web-socket-update-model';
import {ToastService} from "../toast.service";

export enum SocketClientState {
  ATTEMPTING, CONNECTED
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService implements OnDestroy {

  /** Stores game ID when user is in a game. */
  public gameId: string = '';
  /** Subject to unsubscribe from game topic. */
  public gameTopicUnsubscribe$: Subject<any>;
  /** Subject to unsubscribe from game list topic. */
  public gameListTopicUnsubscribe$: Subject<any>;
  /** Subject to unsubscribe from player data topic. */
  public playerDataTopicUnsubscribe$: Subject<any>;
  /** Subject to disconnect. */
  public ngDestroyed$ = new Subject<any>();
  /** Subject to unsubscribe from the user toast topic. */
  userToastTopicUnsubscribe$: Subject<any>;
  /** SockJS client. */
  private readonly client: Client;
  /** Socket state. */
  private state: BehaviorSubject<SocketClientState>;
  /** Model for the logged in user. */
  private user: UserModel;

  constructor(
    private toastService: ToastService,
    private appStore: Store<AppStateContainer>,
    private gameDataStore: Store<GameDataStateContainer>,
    private gameStore: Store<GameStateContainer>,
    private handStore: Store<HandStateContainer>,
    private lobbyStore: Store<LobbyStateContainer>,
    private gameListStore: Store<GameListStateContainer>,
    private playerDataStore: Store<PlayerDataStateContainer>,
    private drawnCardsStore: Store<DrawnCardsStateContainer>
  ) {
    this.client = over(new SockJS(environment.api));
    this.client.debug = () => {
    }; // TODO: Gets rid of debugging messages in console
    this.state = new BehaviorSubject<SocketClientState>(SocketClientState.ATTEMPTING);
    this.client.connect({}, () => {
      this.state.next(SocketClientState.CONNECTED);
    });

    this.appStore.select(selectLoggedInUser)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((user: UserModel) => this.user = user);
  }

  public ngOnDestroy(): void {
    this.connect().pipe(first()).subscribe(client => client.disconnect(null));
    this.ngDestroyed$.next();
  }

  /**
   * Creates and returns an observable that can be subscribed to to receive messages from the
   * provided topic.
   * @param topic The provided topic.
   */
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

  /**
   * Sends data to the provided topic.
   * @param topic The provided topic.
   * @param payload The data being sent.
   */
  public send(topic: string, payload: any): void {
    this.connect()
    .pipe(first())
    .subscribe(client => client.send(topic, {}, JSON.stringify(payload)));
  }

  /**
   * Helper that returns an observable which NgRx effects can use.
   */
  public sendFromStore(): Observable<any> {
    return this.connect();
  }

  /**
   * Subscribes to the game topic, which broadcasts game related updates.
   * @param gameId The ID of the game.
   */
  public subscribeToGameTopic(gameId: string): void {
    this.gameId = gameId;
    this.gameTopicUnsubscribe$ = new Subject<any>();
    this.onMessage(`/topic/game/${gameId}`).pipe(takeUntil(this.gameTopicUnsubscribe$)).subscribe(data => {
      switch (data.type) {
        case MessageType.Lobby:
          this.lobbyStore.dispatch(lobbyModelUpdated(data.data));
          break;
        case MessageType.Game:
          this.gameStore.dispatch(gameModelUpdated(data.data));
          break;
        case MessageType.Hand:
          this.handStore.dispatch(handModelUpdated(data.data));
          break;
        case MessageType.GameData:
          this.gameDataStore.dispatch(gameDataUpdated(data.data));
          break;
        case MessageType.Toast:
          this.toastService.show(data.data.message, data.data.options);
          break;
        case MessageType.ReadyToggled:
          this.lobbyStore.dispatch(playerReadyToggled(data.data));
          break;
        case MessageType.PlayerJoinedLobby:
          this.lobbyStore.dispatch(playerJoinedLobby(data.data));
          break;
        case MessageType.PlayerLeftLobby:
          this.lobbyStore.dispatch(playerLeftLobby(data.data));
          break;
        case MessageType.GamePhaseChanged:
          this.gameStore.dispatch(gamePhaseChanged({phase: data.data}));
          break;
        case MessageType.HandStarted:
          this.gameStore.dispatch(handCompleted({id: data.data}));
          break;
        case MessageType.PlayerAwayToggled:
          this.gameStore.dispatch(playerAwayToggled(data.data));
          break;
        case MessageType.HandActionPerformed:
          this.handStore.dispatch(handActionPerformed(data.data));
          break;
        case MessageType.ActingPlayerChanged:
          this.handStore.dispatch(actingPlayerChanged(data.data));
          break;
      }
    });
  }

  /** Unsubscribes from the game topic. */
  public gameTopicUnsubscribe(): void {
    if (this.gameTopicUnsubscribe$) {
      this.gameTopicUnsubscribe$.next();
      this.gameTopicUnsubscribe$.complete();
    }
  }

  /**
   * Requests an update of the specified type from the game topic.
   * @param type The type of data being requested.
   */
  public requestGameTopicUpdate(type: MessageType): void {
    this.requestUpdate(type, `/topic/game/${this.gameId}`, this.gameId).then();
  }

  /**
   * Requests an update from the specified topic, of the specified type and optionally specifies
   * an ID (could be a game ID or user ID, etc...) that may be needed by the backend to retrieve
   * the desired data.
   * @param type The type of data being requested.
   * @param topic The topic the data is being broadcast to.
   * @param id Optional ID parameter that may be needed by the backend to retrieve the desired data.
   */
  public async requestUpdate(type: MessageType, topic: string, id: string = null): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, 100));
    this.send(`/topic/game/update`, {type, topic, id} as WebSocketUpdateModel);
    return null;
  }

  public drawnCardsTopicUnsubscribe$: Subject<any>;
  public subscribeToDrawnCardsTopic(gameId: string): void {
    this.drawnCardsTopicUnsubscribe$ = new Subject<any>();
    this.onMessage(`/topic/game/${gameId}/drawn-cards`)
    .pipe(takeUntil(this.drawnCardsTopicUnsubscribe$))
    .subscribe(data => this.drawnCardsStore.dispatch(data.suit ? cardDrawn(data) : handOver()))
  }
  public drawnCardsTopicUnsubscribe() {
    this.drawnCardsTopicUnsubscribe$.next();
    this.drawnCardsTopicUnsubscribe$.complete();
  }

  /** Subscribes to the game list topic. */
  public subscribeToGameListTopic(): void {
    this.gameListTopicUnsubscribe$ = new Subject<any>();
    this.onMessage(`/topic/games`)
    .pipe(takeUntil(this.gameListTopicUnsubscribe$))
    .subscribe(data => {
      this.gameListStore.dispatch(gameListUpdated({gameList: data.data}));
    });
    this.requestUpdate(MessageType.GameList, '/topic/games').then();
  }

  /** Unsubscribes from the game list topic. */
  public gameListTopicUnsubscribe() {
    if (this.gameListTopicUnsubscribe$) {
      this.gameListTopicUnsubscribe$.next();
      this.gameListTopicUnsubscribe$.complete();
    }
  }

  /**
   * Requests a player data update. This method uses the user field to retrieve the correct player
   * information.
   */
  public requestPlayerDataUpdate(): void {
    this.requestUpdate(MessageType.PlayerData, `/topic/game/${this.user.id}`, this.user.id).then();
  }

  /** Subscribes to player data topic. */
  public subscribeToPlayerDataTopic() {
    this.playerDataTopicUnsubscribe$ = new Subject<any>();
    this.onMessage(`/topic/game/${this.user.id}`)
    .pipe(takeUntil(this.playerDataTopicUnsubscribe$))
    .subscribe(data => {
      this.playerDataStore.dispatch(playerDataUpdated(data.data));
    });
    this.requestPlayerDataUpdate();
  }

  /** Unsubscribes from the player data topic. */
  public playerDataTopicUnsubscribe(): void {
    if (this.playerDataTopicUnsubscribe$) {
      this.playerDataTopicUnsubscribe$.next();
      this.playerDataTopicUnsubscribe$.complete();
    }
  }

  /**
   * Subscribes to toast topic that broadcasts to the logged in user only (not securely though).
   */
  public subscribeToUserToastTopic(): void {
    this.onMessage(`/topic/toasts/${this.user.id}`)
    .pipe(takeUntil(this.userToastTopicUnsubscribe$))
    .subscribe(data => {
      this.toastService.show(data.data.message, data.data.options);
    });
  }

  public subscribeToCurrentGameTopic(userId: string): void {
    this.onMessage(`/topic/game/current/${userId}`)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe(data => {
      this.appStore.dispatch(updateCurrentGame(data));
    });
  }

  /** Unsubscribes from the user toast topic. */
  userToastTopicUnsubscribe(): void {
    if (this.userToastTopicUnsubscribe$) {
      this.userToastTopicUnsubscribe$.next();
      this.userToastTopicUnsubscribe$.complete();
    }
  }

  /** Connects to the server. */
  private connect(): Observable<Client> {
    return new Observable<Client>(observer => {
      this.state.pipe(filter(state => state === SocketClientState.CONNECTED)).subscribe(() => {
        observer.next(this.client);
      });
    });
  }
}
