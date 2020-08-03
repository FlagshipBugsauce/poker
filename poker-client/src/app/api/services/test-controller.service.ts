/* tslint:disable */
import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {BaseService} from '../base-service';
import {ApiConfiguration} from '../api-configuration';
import {StrictHttpResponse} from '../strict-http-response';
import {RequestBuilder} from '../request-builder';
import {Observable} from 'rxjs';
import {filter, map} from 'rxjs/operators';

import {ModelModel} from '../models/model-model';

@Injectable({
  providedIn: 'root',
})
export class TestControllerService extends BaseService {
  constructor(
    config: ApiConfiguration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * Path part for operation models
   */
  static readonly ModelsPath = '/test/models';

  /**
   * Path part for operation dealCards
   */
  static readonly DealCardsPath = '/test/deal';
  /**
   * Path part for operation sendPrivateMessage
   */
  static readonly SendPrivateMessagePath = '/test/send-private-message';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `models()` instead.
   *
   * This method doesn't expect any request body.
   */
  models$Response(params?: {}): Observable<StrictHttpResponse<ModelModel>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.ModelsPath, 'get');
    if (params) {


    }
    return this.http.request(rb.build({
      responseType: 'blob',
      accept: '*/*'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<ModelModel>;
      })
    );
  }

  /**
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `models$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  models(params?: {}): Observable<ModelModel> {

    return this.models$Response(params).pipe(
      map((r: StrictHttpResponse<ModelModel>) => r.body as ModelModel)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `dealCards()` instead.
   *
   * This method doesn't expect any request body.
   */
  dealCards$Response(params: {
    gameId: string;

  }): Observable<StrictHttpResponse<void>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.DealCardsPath, 'get');
    if (params) {

      rb.query('gameId', params.gameId);

    }
    return this.http.request(rb.build({
      responseType: 'text',
      accept: '*/*'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return (r as HttpResponse<any>).clone({body: undefined}) as StrictHttpResponse<void>;
      })
    );
  }

  /**
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `dealCards$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  dealCards(params: {
    gameId: string;

  }): Observable<void> {

    return this.dealCards$Response(params).pipe(
      map((r: StrictHttpResponse<void>) => r.body as void)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `sendPrivateMessage()` instead.
   *
   * This method doesn't expect any request body.
   */
  sendPrivateMessage$Response(params: {
    message: string;

  }): Observable<StrictHttpResponse<void>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.SendPrivateMessagePath, 'get');
    if (params) {

      rb.query('message', params.message);

    }
    return this.http.request(rb.build({
      responseType: 'text',
      accept: '*/*'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return (r as HttpResponse<any>).clone({body: undefined}) as StrictHttpResponse<void>;
      })
    );
  }

  /**
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `sendPrivateMessage$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  sendPrivateMessage(params: {
    message: string;

  }): Observable<void> {

    return this.sendPrivateMessage$Response(params).pipe(
      map((r: StrictHttpResponse<void>) => r.body as void)
    );
  }

}
