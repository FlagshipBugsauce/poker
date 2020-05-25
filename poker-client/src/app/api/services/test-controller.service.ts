/* tslint:disable */
import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {BaseService} from '../base-service';
import {ApiConfiguration} from '../api-configuration';
import {StrictHttpResponse} from '../strict-http-response';
import {RequestBuilder} from '../request-builder';
import {Observable} from 'rxjs';
import {filter, map} from 'rxjs/operators';

import {ApiSuccessModel} from '../models/api-success-model';
import {HandDocument} from '../models/hand-document';
import {LobbyDocument} from '../models/lobby-document';
import {SseEmitter} from '../models/sse-emitter';

@Injectable({
  providedIn: 'root',
})
export class TestControllerService extends BaseService {
  /**
   * Path part for operation test001
   */
  static readonly Test001Path = '/test/test';
  /**
   * Path part for operation test004
   */
  static readonly Test004Path = '/test/sse/test02/{x}/{y}';
  /**
   * Path part for operation test006
   */
  static readonly Test006Path = '/test/test/gameDoc';
  /**
   * Path part for operation test002
   */
  static readonly Test002Path = '/test/get';
  /**
   * Path part for operation test007
   */
  static readonly Test007Path = '/test/test/something';
  /**
   * Path part for operation test003
   */
  static readonly Test003Path = '/test/sse/test01/{x}';
  /**
   * Path part for operation test005
   */
  static readonly Test005Path = '/test/sse/test03/{userID}';

  constructor(
    config: ApiConfiguration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `test001()` instead.
   *
   * This method doesn't expect any request body.
   */
  test001$Response(params?: {}): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.Test001Path, 'post');
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
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `test001$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  test001(params?: {}): Observable<ApiSuccessModel> {

    return this.test001$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `test004()` instead.
   *
   * This method doesn't expect any request body.
   */
  test004$Response(params: {
    'x': string;
    'y': string;

  }): Observable<StrictHttpResponse<void>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.Test004Path, 'get');
    if (params) {

      rb.path('x', params['x']);
      rb.path('y', params['y']);

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
   * To access the full response (for headers, for example), `test004$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  test004(params: {
    'x': string;
    'y': string;

  }): Observable<void> {

    return this.test004$Response(params).pipe(
      map((r: StrictHttpResponse<void>) => r.body as void)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `test006()` instead.
   *
   * This method doesn't expect any request body.
   */
  test006$Response(params?: {}): Observable<StrictHttpResponse<LobbyDocument>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.Test006Path, 'get');
    if (params) {


    }
    return this.http.request(rb.build({
      responseType: 'blob',
      accept: '*/*'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<LobbyDocument>;
      })
    );
  }

  /**
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `test006$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  test006(params?: {}): Observable<LobbyDocument> {

    return this.test006$Response(params).pipe(
      map((r: StrictHttpResponse<LobbyDocument>) => r.body as LobbyDocument)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `test002()` instead.
   *
   * This method doesn't expect any request body.
   */
  test002$Response(params?: {}): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.Test002Path, 'get');
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
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `test002$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  test002(params?: {}): Observable<ApiSuccessModel> {

    return this.test002$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `test007()` instead.
   *
   * This method doesn't expect any request body.
   */
  test007$Response(params?: {}): Observable<StrictHttpResponse<HandDocument>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.Test007Path, 'get');
    if (params) {


    }
    return this.http.request(rb.build({
      responseType: 'blob',
      accept: '*/*'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<HandDocument>;
      })
    );
  }

  /**
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `test007$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  test007(params?: {}): Observable<HandDocument> {

    return this.test007$Response(params).pipe(
      map((r: StrictHttpResponse<HandDocument>) => r.body as HandDocument)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `test003()` instead.
   *
   * This method doesn't expect any request body.
   */
  test003$Response(params: {
    'x': string;

  }): Observable<StrictHttpResponse<SseEmitter>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.Test003Path, 'get');
    if (params) {

      rb.path('x', params['x']);

    }
    return this.http.request(rb.build({
      responseType: 'blob',
      accept: '*/*'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<SseEmitter>;
      })
    );
  }

  /**
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `test003$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  test003(params: {
    'x': string;

  }): Observable<SseEmitter> {

    return this.test003$Response(params).pipe(
      map((r: StrictHttpResponse<SseEmitter>) => r.body as SseEmitter)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `test005()` instead.
   *
   * This method doesn't expect any request body.
   */
  test005$Response(params: {
    userID: string;

  }): Observable<StrictHttpResponse<{}>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.Test005Path, 'get');
    if (params) {

      rb.path('userID', params.userID);

    }
    return this.http.request(rb.build({
      responseType: 'blob',
      accept: '*/*'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<{}>;
      })
    );
  }

  /**
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `test005$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  test005(params: {
    userID: string;

  }): Observable<{}> {

    return this.test005$Response(params).pipe(
      map((r: StrictHttpResponse<{}>) => r.body as {})
    );
  }

}
