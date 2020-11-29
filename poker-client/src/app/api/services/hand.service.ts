/* tslint:disable */
import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {BaseService} from '../base-service';
import {ApiConfiguration} from '../api-configuration';
import {StrictHttpResponse} from '../strict-http-response';
import {RequestBuilder} from '../request-builder';
import {Observable} from 'rxjs';
import {filter, map} from 'rxjs/operators';

import {ApiSuccess} from '../models/api-success';


/**
 * Hand API handles all game requests after the game has started.
 */
@Injectable({
  providedIn: 'root',
})
export class HandService extends BaseService {
  /**
   * Path part for operation draw
   */
  static readonly DrawPath = '/game/hand/draw';

  constructor(
    config: ApiConfiguration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * Draws a card.
   *
   * Draws a card from the top of the deck.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `draw()` instead.
   *
   * This method doesn't expect any request body.
   */
  draw$Response(params?: {}): Observable<StrictHttpResponse<ApiSuccess>> {

    const rb = new RequestBuilder(this.rootUrl, HandService.DrawPath, 'post');
    if (params) {


    }
    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<ApiSuccess>;
      })
    );
  }

  /**
   * Draws a card.
   *
   * Draws a card from the top of the deck.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `draw$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  draw(params?: {}): Observable<ApiSuccess> {

    return this.draw$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccess>) => r.body as ApiSuccess)
    );
  }

}
