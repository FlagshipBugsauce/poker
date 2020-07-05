/* tslint:disable */
import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';
import { RequestBuilder } from '../request-builder';
import { Observable } from 'rxjs';
import { map, filter } from 'rxjs/operators';

import { ApiSuccessModel } from '../models/api-success-model';
import { PlayerModel } from '../models/player-model';


/**
 * Hand API handles all game requests after the game has started.
 */
@Injectable({
  providedIn: 'root',
})
export class HandService extends BaseService {
  constructor(
    config: ApiConfiguration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * Path part for operation determineWinner
   */
  static readonly DetermineWinnerPath = '/game/hand/determine-winner/{handId}';

  /**
   * Determines winner of hand.
   *
   * Responds with the player model of the winner of a hand.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `determineWinner()` instead.
   *
   * This method doesn't expect any request body.
   */
  determineWinner$Response(params: {
    handId: string;

  }): Observable<StrictHttpResponse<PlayerModel>> {

    const rb = new RequestBuilder(this.rootUrl, HandService.DetermineWinnerPath, 'post');
    if (params) {

      rb.path('handId', params.handId);

    }
    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<PlayerModel>;
      })
    );
  }

  /**
   * Determines winner of hand.
   *
   * Responds with the player model of the winner of a hand.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `determineWinner$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  determineWinner(params: {
    handId: string;

  }): Observable<PlayerModel> {

    return this.determineWinner$Response(params).pipe(
      map((r: StrictHttpResponse<PlayerModel>) => r.body as PlayerModel)
    );
  }

  /**
   * Path part for operation draw
   */
  static readonly DrawPath = '/game/hand/draw';

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
  draw$Response(params?: {

  }): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, HandService.DrawPath, 'post');
    if (params) {


    }
    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<ApiSuccessModel>;
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
  draw(params?: {

  }): Observable<ApiSuccessModel> {

    return this.draw$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

}
