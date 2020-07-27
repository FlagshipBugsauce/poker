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

}
