import { Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';
import * as _ from 'lodash';

@Injectable({
  providedIn: 'root'
})
export class SseService {

  constructor(private zone: NgZone) { }

  public getEventSource(url: string): EventSource {
    return new EventSource(url);
  }

  getServerSentEvent<R>(url: string, converter: (data: string) => R = _.identity): Observable<R> {
    return Observable.create(observer => {
      const eventSource = this.getEventSource(url);

      eventSource.onmessage = event => this.zone.run(() => observer.next(converter(event.data)));
      eventSource.onerror = event => this.zone.run(() => observer.next(event));
    });
  }
}
