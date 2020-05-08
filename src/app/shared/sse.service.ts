import { Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';
import * as _ from 'lodash';

@Injectable({
  providedIn: 'root'
})
export class SseService {

  private eventSources: EventSourceContainer = <EventSourceContainer> { };

  constructor(private zone: NgZone) { }

  public getEventSource(url: string): EventSource {
    return new EventSource(url);
  }

  getServerSentEvent<R>(url: string, type: string, converter: (data: string) => R = _.identity): Observable<R> {
    return Observable.create(observer => {
      const eventSource = this.getEventSource(url);

      // TODO: Need a more elegant solution for this. Possibly an enum...
      if (type == "joinGame") this.eventSources.joinGame = eventSource;
      if (type == "game") this.eventSources.game = eventSource;

      eventSource.onmessage = event => this.zone.run(() => observer.next(converter(event.data)));
      eventSource.onerror = event => this.zone.run(() => observer.next(event));
    });
  }

  public closeEvent(type: string): void {
    if (type == "joinGame") this.eventSources.joinGame.close();
    if (type == "game") this.eventSources.game.close();
  }
}

export interface EventSourceContainer {
  joinGame: EventSource;
  game: EventSource;
}
