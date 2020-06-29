import {Injectable, NgZone} from '@angular/core';
import {Observable} from 'rxjs';
import * as _ from 'lodash';
import {EmitterType} from './models/emitter-type.model';
import {EmittersService} from '../api/services';
import {ApiConfiguration} from '../api/api-configuration';
import {Store} from '@ngrx/store';
import {
  gameDataUpdated,
  gameDocumentUpdated,
  gameListUpdated,
  handDocumentUpdated,
  lobbyDocumentUpdated, playerDataUpdated
} from '../state/app.actions';
import {
  AppStateContainer,
  GameDataStateContainer,
  GameListStateContainer,
  GameStateContainer,
  HandStateContainer,
  LobbyStateContainer, PlayerDataStateContainer
} from './models/app-state.model';
import {selectJwt} from '../state/app.selector';

export interface SseTrackerModel {
  open: boolean;
  reference: any;
  defaultValue: any;
  action: any;
  store: any;
}

const getTracker = (defaultValue: any, action: any, store: any) =>
  ({open: false, reference: null, defaultValue, action, store} as SseTrackerModel);

@Injectable({
  providedIn: 'root'
})
export class SseService {
  private sseTracker = {};
  private jwt: string = '';

  constructor(
    private zone: NgZone,
    private emittersService: EmittersService,
    private apiConfiguration: ApiConfiguration,
    private appStore: Store<AppStateContainer>,
    private gameDataStore: Store<GameDataStateContainer>,
    private gameStore: Store<GameStateContainer>,
    private handStore: Store<HandStateContainer>,
    private lobbyStore: Store<LobbyStateContainer>,
    private gameListStore: Store<GameListStateContainer>,
    private playerDataStore: Store<PlayerDataStateContainer>) {

    this.appStore.select(selectJwt).subscribe(jwt => this.jwt = jwt);

    this.sseTracker[EmitterType.GameList] =
      getTracker({gameList: []}, gameListUpdated, this.gameListStore);
    this.sseTracker[EmitterType.Lobby] =
      getTracker({}, lobbyDocumentUpdated, this.lobbyStore);
    this.sseTracker[EmitterType.Game] =
      getTracker({}, gameDocumentUpdated, this.gameStore);
    this.sseTracker[EmitterType.Hand] =
      getTracker({}, handDocumentUpdated, this.handStore);
    this.sseTracker[EmitterType.GameData] =
      getTracker({gameData: [], currentHand: 1}, gameDataUpdated, this.gameDataStore);
    this.sseTracker[EmitterType.PlayerData] =
      getTracker({}, playerDataUpdated, this.playerDataStore);
  }

  /**
   * Requests an SSE emitter of a specific type from the backend. Data can be retrieved using the
   * getter associated with the type specified.
   * @param type The type of emitter being requested.
   */
  public openEvent(type: EmitterType): void {
    if (!this.sseTracker[type].open) {
      this.getEmitter(type).subscribe((data: any) => {
        if (data) {
          this.sseTracker[type].open = true;
          if (type === EmitterType.GameList) {
            this.sseTracker[type].store.dispatch(this.sseTracker[type].action({gameList: data}));
          } else  {
            this.sseTracker[type].store.dispatch(this.sseTracker[type].action(data));
          }
        }
      });
    }
    // Request update so initial data will be sent to the emitter.
    this.requestUpdate(type).then();
  }

  /**
   * Typically used to get the emitter out of pending state, but can also be used to manually
   * request an update.
   * @param type The emitter type the update is being requested for.
   */
  public async requestUpdate(type: EmitterType) {
    await new Promise(resolve => setTimeout(resolve, 100));
    this.emittersService.requestUpdate({type}).subscribe(() => {
    });
  }

  /**
   * Called when the client is done with emitter. Client should only have one type of each emitter
   * at most, so this can be called by providing only the emitter type.
   * @param type The type of emitter to close.
   */
  public closeEvent(type: EmitterType): void {
    if (this.sseTracker[type].reference) {
      try {
        this.emittersService.destroyEmitter({type}).subscribe(() => {
          this.sseTracker[type].reference.close();               // Close the event.
          this.sseTracker[type].reference = null;                // Remove the event reference.
          this.sseTracker[type].open = false;                    // Set open event map to false.

          this.sseTracker[type].store.dispatch(                  // Dispatch default data.
            this.sseTracker[type].action(this.sseTracker[type].defaultData));
        });
      } catch (err) {
        console.log(`Something went wrong trying to close the ${type} emitter.`);
      }
    }
  }

  /**
   * Creates an observable that streams data to the client.
   * @param url URL where the emitter is retrieved.
   * @param type The type of emitter being retrieved.
   * @param converter Converter which transforms the event to usable data.
   */
  private getServerSentEvent<R>(
    url: string, type: EmitterType,
    converter: (data: string) => R = _.identity): Observable<R> {
    return new Observable(observer => {
      this.sseTracker[type].reference = new EventSource(url);
      this.sseTracker[type].reference.onmessage = event => this.zone.run(() => {
        try {
          return observer.next(converter(JSON.parse(event.data)));
        } catch (err) {
          console.log(`Something went wrong with the ${type} emitter.`);
          this.closeEvent(type);
        }
      });
      this.sseTracker[type].reference.onerror = event => this.zone.run(() => observer.next(null));
    });
  }

  /**
   * Helper method which retrieves the URL for the specified type of emitter.
   * @param type The specified type of emitter.
   */
  private getUrl(type: EmitterType): string {
    return `${this.apiConfiguration.rootUrl}/emitters/request/${type}/${this.jwt}`;
  }

  /**
   * Retrieves an observable that will stream data to the client.
   * @param type The type of emitter being requested.
   */
  private getEmitter<R>(type: EmitterType): Observable<R> {
    return this.getServerSentEvent(this.getUrl(type), type);
  }
}
