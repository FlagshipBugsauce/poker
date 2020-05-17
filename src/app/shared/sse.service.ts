import {Injectable, NgZone} from '@angular/core';
import {Observable} from 'rxjs';
import * as _ from 'lodash';
import {EmitterType} from './models/emitter-type.model';
import {EmittersService} from '../api/services';

@Injectable({
  providedIn: 'root'
})
export class SseService {
  private eventDictionary: EventSourceKVP[] = [] as EventSourceKVP[];

  constructor(private zone: NgZone, private emittersService: EmittersService) {
  }

  public getEventSource(url: string): EventSource {
    return new EventSource(url);
  }

  getServerSentEvent<R>(url: string, type: EmitterType, converter: (data: string) => R = _.identity): Observable<R> {
    return new Observable(observer => {
      const eventSource = this.getEventSource(url);

      this.eventDictionary.push({type, event: eventSource} as EventSourceKVP);

      eventSource.onmessage = event => this.zone.run(() => observer.next(converter(event.data)));
      // @ts-ignore
      eventSource.onerror = event => this.zone.run(() => observer.next(event));
    });
  }

  public closeEvent(type: EmitterType): void {
    // TODO: Call the destroy emitter API here instead of in the components/guards
    if (this.eventDictionary.find((kvp: EventSourceKVP) => kvp.type === type) !== undefined) {
      try {
        const event = this.eventDictionary.find((kvp: EventSourceKVP) => kvp.type === type).event;
        // Remove the KVP from array.
        this.eventDictionary = this.eventDictionary.filter((kvp: EventSourceKVP) => kvp.type !== type);
        // Tell the server there is no more use for this emitter.
        this.emittersService.destroyEmitter({type}).subscribe(() => {
          // Close the event.
          event.close();
        });
      } catch (error) {
        console.log('Something went wrong trying to close the emitter.');
      }
    }

  }
}

export interface EventSourceKVP {
  type: EmitterType;
  event: EventSource;
}
