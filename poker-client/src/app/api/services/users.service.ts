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
import {AuthRequest} from '../models/auth-request';
import {AuthResponse} from '../models/auth-response';
import {ClientUser} from '../models/client-user';
import {JwtAuthRequest} from '../models/jwt-auth-request';
import {NewAccount} from '../models/new-account';


/**
 * Users API handles all user account related requests, such as authentication, registration, etc...
 */
@Injectable({
  providedIn: 'root',
})
export class UsersService extends BaseService {
  constructor(
    config: ApiConfiguration,
    http: HttpClient
  ) {
    super(config, http);
  }

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
  /**
   * Path part for operation authorizeWithJwt
   */
  static readonly AuthorizeWithJwtPath = '/user/auth-with-jwt';

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

  }): Observable<StrictHttpResponse<ClientUser>> {

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
        return r as StrictHttpResponse<ClientUser>;
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

  }): Observable<ClientUser> {

    return this.getUserInfo$Response(params).pipe(
        map((r: StrictHttpResponse<ClientUser>) => r.body as ClientUser)
    );
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
    body: NewAccount
  }): Observable<StrictHttpResponse<ApiSuccess>> {

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
          return r as StrictHttpResponse<ApiSuccess>;
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
    body: NewAccount
  }): Observable<ApiSuccess> {

    return this.register$Response(params).pipe(
        map((r: StrictHttpResponse<ApiSuccess>) => r.body as ApiSuccess)
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
    body: AuthRequest
  }): Observable<StrictHttpResponse<AuthResponse>> {

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
        return r as StrictHttpResponse<AuthResponse>;
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
    body: AuthRequest
  }): Observable<AuthResponse> {

    return this.authorize$Response(params).pipe(
      map((r: StrictHttpResponse<AuthResponse>) => r.body as AuthResponse)
    );
  }

  /**
   * Authenticate With JWT.
   *
   * If the client has a JWT stored in a cookie, it can call this endpoint to authenticate using the JWT stored in the cookie.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `authorizeWithJwt()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  authorizeWithJwt$Response(params: {
    body: JwtAuthRequest
  }): Observable<StrictHttpResponse<AuthResponse>> {

    const rb = new RequestBuilder(this.rootUrl, UsersService.AuthorizeWithJwtPath, 'post');
    if (params) {


      rb.body(params.body, 'application/json');
    }
    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<AuthResponse>;
      })
    );
  }

  /**
   * Authenticate With JWT.
   *
   * If the client has a JWT stored in a cookie, it can call this endpoint to authenticate using the JWT stored in the cookie.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `authorizeWithJwt$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  authorizeWithJwt(params: {
    body: JwtAuthRequest
  }): Observable<AuthResponse> {

    return this.authorizeWithJwt$Response(params).pipe(
      map((r: StrictHttpResponse<AuthResponse>) => r.body as AuthResponse)
    );
  }

}
