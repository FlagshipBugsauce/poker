/* tslint:disable */
import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {BaseService} from '../base-service';
import {ApiConfiguration} from '../api-configuration';
import {StrictHttpResponse} from '../strict-http-response';
import {RequestBuilder} from '../request-builder';
import {Observable} from 'rxjs';
import {filter, map} from 'rxjs/operators';

import {PrivateTopicModel} from '../models/private-topic-model';


/**
 * WebSocket controller.
 */
@Injectable({
  providedIn: 'root',
})
export class WebsocketService extends BaseService {
  /**
   * Path part for operation getPrivateTopic
   */
  static readonly GetPrivateTopicPath = '/private-topic';

  constructor(
    config: ApiConfiguration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * Request a private topic.
   *
   * Creates a private topic so that the backend can communicate securely to one client.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getPrivateTopic()` instead.
   *
   * This method doesn't expect any request body.
   */
  getPrivateTopic$Response(params?: {}): Observable<StrictHttpResponse<PrivateTopicModel>> {

    const rb = new RequestBuilder(this.rootUrl, WebsocketService.GetPrivateTopicPath, 'get');
    if (params) {


    }
    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<PrivateTopicModel>;
      })
    );
  }

  /**
   * Request a private topic.
   *
   * Creates a private topic so that the backend can communicate securely to one client.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `getPrivateTopic$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getPrivateTopic(params?: {}): Observable<PrivateTopicModel> {

    return this.getPrivateTopic$Response(params).pipe(
      map((r: StrictHttpResponse<PrivateTopicModel>) => r.body as PrivateTopicModel)
    );
  }

}
