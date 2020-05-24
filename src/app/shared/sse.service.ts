import {Injectable, NgZone} from '@angular/core';
import {Observable} from 'rxjs';
import * as _ from 'lodash';
import {EmitterType} from './models/emitter-type.model';
import {EmittersService} from '../api/services';
import {ApiConfiguration} from '../api/api-configuration';
import {ApiInterceptor} from '../api-interceptor.service';
import {GetGameModel} from '../api/models/get-game-model';
import {GameDocument} from '../api/models/game-document';
import {LobbyDocument} from '../api/models/lobby-document';
import {HandDocument} from '../api/models/hand-document';

@Injectable({
  providedIn: 'root'
})
export class SseService {
  /**
   * Dictionary that stores all game models.
   */
  private data = {};

  /**
   * Default data (typically empty object/list) to be placed in data dictionary to avoid null pointer exceptions.
   */
  private defaultData = {};

  /**
   * Getter for the game list.
   */
  public get gameList(): GetGameModel[] {
    return this.data[EmitterType.GameList];
  }

  /**
   * Setter for the game list.
   * @param gameList List of games.
   */
  public set gameList(gameList: GetGameModel[]) {
    this.data[EmitterType.GameList] = gameList;
  }

  /**
   * Getter for the lobby model.
   */
  public get lobbyDocument(): LobbyDocument {
    return this.data[EmitterType.Lobby];
  }

  /**
   * Setter for the lobby model.
   * @param lobbyDocument Lobby model.
   */
  public set lobbyDocument(lobbyDocument: LobbyDocument) {
    this.data[EmitterType.Lobby] = lobbyDocument;
  }

  /**
   * Getter for the game model.
   */
  public get gameDocument(): GameDocument {
    return this.data[EmitterType.Game];
  }

  /**
   * Setter for the game model.
   * @param gameDocument Game model.
   */
  public set gameDocument(gameDocument: GameDocument) {
    this.data[EmitterType.Game] = gameDocument;
  }

  /**
   * Getter for hand model.
   */
  public get handDocument(): HandDocument {
    return this.data[EmitterType.Hand];
  }

  /**
   * Setter for hand model.
   * @param handDocument Hand model.
   */
  public set handDocument(handDocument: HandDocument) {
    this.data[EmitterType.Hand] = handDocument;
  }

  /**
   * Dictionary of callbacks to be executed when data is received. Callbacks are mapped to specific emitter type. Each type has a list of
   * callbacks that will be executed so that different components can be updated.
   */
  private callbacks = {};

  /**
   * Dictionary of events that currently open. Event types are mapped to the events themselves.
   */
  public openEvents = {};

  /**
   * Dictionary of references. Event types are mapped to an event reference.
   */
  public eventReferences = {};

  constructor(
    private zone: NgZone,
    private emittersService: EmittersService,
    private apiConfiguration: ApiConfiguration,
    private apiInterceptor: ApiInterceptor) {
    this.openEvents[EmitterType.GameList] = false;
    this.openEvents[EmitterType.Lobby] = false;
    this.openEvents[EmitterType.Game] = false;
    this.openEvents[EmitterType.Hand] = false;
    this.callbacks[EmitterType.GameList] = [] as Array<() => void>;
    this.callbacks[EmitterType.Lobby] = [] as Array<() => void>;
    this.callbacks[EmitterType.Game] = [] as Array<() => void>;
    this.callbacks[EmitterType.Hand] = [] as Array<() => void>;
    this.gameList = this.defaultData[EmitterType.GameList] = [] as GetGameModel[];
    this.lobbyDocument = this.defaultData[EmitterType.Lobby] = {} as LobbyDocument;
    this.gameDocument = this.defaultData[EmitterType.Game] = {} as GameDocument;
    this.handDocument = this.defaultData[EmitterType.Hand] = {} as HandDocument;
  }

  /**
   * Creates an observable that streams data to the client.
   * @param url URL where the emitter is retrieved.
   * @param type The type of emitter being retrieved.
   * @param converter Converter which transforms the event to usable data.
   */
  private getServerSentEvent<R>(url: string, type: EmitterType, converter: (data: string) => R = _.identity): Observable<R> {
    return new Observable(observer => {
      this.eventReferences[type] = new EventSource(url);
      this.eventReferences[type].onmessage = event => this.zone.run(() => {
        try {
          return observer.next(converter(JSON.parse(event.data)));
        } catch (err) {
          console.log(`Something went wrong with the ${type} emitter.`);
          this.closeEvent(type);
        }
      });
      this.eventReferences[type].onerror = event => this.zone.run(() => observer.next(null));
    });
  }

  /**
   * Helper method which retrieves the URL for the specified type of emitter.
   * @param type The specified type of emitter.
   */
  private getUrl(type: EmitterType): string {
    return `${this.apiConfiguration.rootUrl}/emitters/request/${type}/${this.apiInterceptor.jwt}`;
  }

  /**
   * Retrieves an observable that will stream data to the client.
   * @param type The type of emitter being requested.
   */
  private getEmitter<R>(type: EmitterType): Observable<R> {
    return this.getServerSentEvent(this.getUrl(type), type);
  }

  /**
   * Requests an SSE emitter of a specific type from the backend. Data can be retrieved using the getter associated with the type specified.
   * @param type The type of emitter being requested.
   * @param callback A procedure that will be run each time data is received.
   */
  public openEvent(type: EmitterType, callback: () => void = null): void {
    if (!this.openEvents[type]) {
      if (callback != null) { this.callbacks[type].push(callback); }
      this.getEmitter(type).subscribe((data: any) => {
        if (data != null) {
          this.data[type] = data;
          this.callbacks[type].forEach(cb => cb()); // Execute callbacks.
          this.openEvents[type] = true;
        }
      });
    }
    // Request update so initial data will be sent to the emitter.
    this.requestUpdate(type).then();
  }

  /**
   * Adds a callback which will be run when data is received by the emitter of the specified type. Since multiple components may want data
   * from the same emitter, this is needed to make life easier.
   * @param type The type of emitter the callback should be executed for.
   * @param callback The procedure executed when data is received.
   */
  public addCallback(type: EmitterType, callback: () => void): void {
    this.callbacks[type].push(callback);
  }

  /**
   * Typically used to get the emitter out of pending state, but can also be used to manually request an update.
   * @param type The emitter type the update is being requested for.
   */
  public async requestUpdate(type: EmitterType) {
    await new Promise(resolve => setTimeout(resolve, 100));
    this.emittersService.requestUpdate({type}).subscribe(() => {
    });
  }

  /**
   * Called when the client is done with emitter. Client should only have one type of each emitter at most, so this can be called by
   * providing only the emitter type.
   * @param type The type of emitter to close.
   */
  public closeEvent(type: EmitterType): void {
    if (this.eventReferences[type] != null) {
      try {
        this.emittersService.destroyEmitter({type}).subscribe(() => {
          this.eventReferences[type].close();               // Close the event.
          this.eventReferences[type] = null;                // Remove the event reference.
          this.openEvents[type] = false;                    // Set open event map to false.
          this.callbacks[type] = [] as Array<() => void>;   // Reset callbacks.
        });
      } catch (err) {
        console.log(`Something went wrong trying to close the ${type} emitter.`);
      }
    }
  }
}
