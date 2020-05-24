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
import {AuthRequestModel} from '../models/auth-request-model';
import {AuthResponseModel} from '../models/auth-response-model';
import {NewAccountModel} from '../models/new-account-model';
import {UserModel} from '../models/user-model';


/**
 * Users API handles all user account related requests, such as authentication, registration, etc...
 */
@Injectable({
  providedIn: 'root',
})
export class UsersService extends BaseService {
  /**
   * Path part for operation register
   */
  static readonly RegisterPath = '/user/register';
  /**
   * Path part for operation getUserInfo
   */
  static readonly GetUserInfoPath = '/user/getUserInfo/{userId}';
  /**
   * Path part for operation authorize
   */
  static readonly AuthorizePath = '/user/auth';

  constructor(
    config: ApiConfiguration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * Register.
   *
   * Create an account.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `register()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  register$Response(params: {
    body: NewAccountModel
  }): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, UsersService.RegisterPath, 'post');
    if (params) {


      rb.body(params.body, 'application/json');
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
   * Register.
   *
   * Create an account.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `register$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  register(params: {
    body: NewAccountModel
  }): Observable<ApiSuccessModel> {

    return this.register$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

  /**
   * Get User Info.
   *
   * Retrieve user information for user with provided ID.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getUserInfo()` instead.
   *
   * This method doesn't expect any request body.
   */
  getUserInfo$Response(params: {
    userId: string;

  }): Observable<StrictHttpResponse<UserModel>> {

    const rb = new RequestBuilder(this.rootUrl, UsersService.GetUserInfoPath, 'get');
    if (params) {

      rb.path('userId', params.userId);

    }
    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<UserModel>;
      })
    );
  }

  /**
   * Get User Info.
   *
   * Retrieve user information for user with provided ID.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `getUserInfo$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getUserInfo(params: {
    userId: string;

  }): Observable<UserModel> {

    return this.getUserInfo$Response(params).pipe(
      map((r: StrictHttpResponse<UserModel>) => r.body as UserModel)
    );
  }

  /**
   * Authenticate.
   *
   * The client must call this endpoint in order to obtain a JWT, which must be passed in the header of most requests.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `authorize()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  authorize$Response(params: {
    body: AuthRequestModel
  }): Observable<StrictHttpResponse<AuthResponseModel>> {

    const rb = new RequestBuilder(this.rootUrl, UsersService.AuthorizePath, 'post');
    if (params) {


      rb.body(params.body, 'application/json');
    }
    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<AuthResponseModel>;
      })
    );
  }

  /**
   * Authenticate.
   *
   * The client must call this endpoint in order to obtain a JWT, which must be passed in the header of most requests.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `authorize$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  authorize(params: {
    body: AuthRequestModel
  }): Observable<AuthResponseModel> {

    return this.authorize$Response(params).pipe(
      map((r: StrictHttpResponse<AuthResponseModel>) => r.body as AuthResponseModel)
    );
  }

}
