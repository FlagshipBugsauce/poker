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


/**
 * Hand API handles all game requests after the game has started.
 */
@Injectable({
  providedIn: 'root',
})
export class HandService extends BaseService {
  /**
   * Path part for operation roll
   */
  static readonly RollPath = '/game/hand/roll';

  constructor(
    config: ApiConfiguration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * Roll a random number.
   *
   * Generates a random number.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `roll()` instead.
   *
   * This method doesn't expect any request body.
   */
  roll$Response(params?: {}): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, HandService.RollPath, 'post');
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
   * Roll a random number.
   *
   * Generates a random number.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `roll$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  roll(params?: {}): Observable<ApiSuccessModel> {

    return this.roll$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

}
