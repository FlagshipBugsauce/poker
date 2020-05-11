import { Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';
import * as _ from 'lodash';
import { EmitterType } from './models/emitter-type.model';

@Injectable({
  providedIn: 'root'
})
export class SseService {
  private eventDictionary: EventSourceKVP[] = <EventSourceKVP[]> [];

  constructor(private zone: NgZone) { }

  public getEventSource(url: string): EventSource {
    return new EventSource(url);
  }

  getServerSentEvent<R>(url: string, type: EmitterType, converter: (data: string) => R = _.identity): Observable<R> {
    return Observable.create(observer => {
      const eventSource = this.getEventSource(url);

      switch (type) {
        case EmitterType.GameList:
          this.eventDictionary.push(<EventSourceKVP> { type: EmitterType.GameList, event: eventSource });
          break;
        case EmitterType.Game:
          this.eventDictionary.push(<EventSourceKVP> { type: EmitterType.Game, event: eventSource });
          break;
        case EmitterType.Lobby:
          this.eventDictionary.push(<EventSourceKVP> { type: EmitterType.Lobby, event: eventSource });
          break;
      }

      eventSource.onmessage = event => this.zone.run(() => observer.next(converter(event.data)));
      eventSource.onerror = event => this.zone.run(() => observer.next(event));
    });
  }

  public closeEvent(type: EmitterType): void {
    if (this.eventDictionary.find((kvp: EventSourceKVP) => kvp.type == type) != undefined) {
      try {
        // Close the event.
        this.eventDictionary.find((kvp: EventSourceKVP) => kvp.type == type).event.close();
        // Remove the KVP from array.
        this.eventDictionary = this.eventDictionary.filter((kvp: EventSourceKVP) => kvp.type != type);
      } catch (error) {
        console.log("Something went wrong trying to close the emitter.");
      }
    }
    
  }
}

export interface EventSourceKVP {
  type: EmitterType;
  event: EventSource;
}
