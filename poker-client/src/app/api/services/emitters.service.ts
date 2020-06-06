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
import { SseEmitter } from '../models/sse-emitter';


/**
 * Handles all requests related to SSE emitters
 */
@Injectable({
  providedIn: 'root',
})
export class EmittersService extends BaseService {
  constructor(
    config: ApiConfiguration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * Path part for operation requestUpdate
   */
  static readonly RequestUpdatePath = '/emitters/update/{type}';

  /**
   * Request Update.
   *
   * Requests an update from the SSE emitter specified.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `requestUpdate()` instead.
   *
   * This method doesn't expect any request body.
   */
  requestUpdate$Response(params: {
    type: 'GameList' | 'Game' | 'Lobby' | 'Hand';

  }): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, EmittersService.RequestUpdatePath, 'post');
    if (params) {

      rb.path('type', params.type);

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
   * Request Update.
   *
   * Requests an update from the SSE emitter specified.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `requestUpdate$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  requestUpdate(params: {
    type: 'GameList' | 'Game' | 'Lobby' | 'Hand';

  }): Observable<ApiSuccessModel> {

    return this.requestUpdate$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

  /**
   * Path part for operation requestEmitter
   */
  static readonly RequestEmitterPath = '/emitters/request/{type}/{jwt}';

  /**
   * Request SSE Emitter.
   *
   * Request an SSE emitter of the specified type.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `requestEmitter()` instead.
   *
   * This method doesn't expect any request body.
   */
  requestEmitter$Response(params: {
    jwt: string;
    type: 'GameList' | 'Game' | 'Lobby' | 'Hand';

  }): Observable<StrictHttpResponse<SseEmitter>> {

    const rb = new RequestBuilder(this.rootUrl, EmittersService.RequestEmitterPath, 'get');
    if (params) {

      rb.path('jwt', params.jwt);
      rb.path('type', params.type);

    }
    return this.http.request(rb.build({
      responseType: 'text',
      accept: 'text/event-stream'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<SseEmitter>;
      })
    );
  }

  /**
   * Request SSE Emitter.
   *
   * Request an SSE emitter of the specified type.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `requestEmitter$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  requestEmitter(params: {
    jwt: string;
    type: 'GameList' | 'Game' | 'Lobby' | 'Hand';

  }): Observable<SseEmitter> {

    return this.requestEmitter$Response(params).pipe(
      map((r: StrictHttpResponse<SseEmitter>) => r.body as SseEmitter)
    );
  }

  /**
   * Path part for operation destroyEmitter
   */
  static readonly DestroyEmitterPath = '/emitters/destroy/{type}';

  /**
   * Destroy Emitter.
   *
   * Destroy the emitter that is sending updated game lists.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `destroyEmitter()` instead.
   *
   * This method doesn't expect any request body.
   */
  destroyEmitter$Response(params: {
    type: 'GameList' | 'Game' | 'Lobby' | 'Hand';

  }): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, EmittersService.DestroyEmitterPath, 'post');
    if (params) {

      rb.path('type', params.type);

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
   * Destroy Emitter.
   *
   * Destroy the emitter that is sending updated game lists.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `destroyEmitter$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  destroyEmitter(params: {
    type: 'GameList' | 'Game' | 'Lobby' | 'Hand';

  }): Observable<ApiSuccessModel> {

    return this.destroyEmitter$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

}
