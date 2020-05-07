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
import { CreateGameModel } from '../models/create-game-model';
import { GetGameModel } from '../models/get-game-model';
import { SseEmitter } from '../models/sse-emitter';


/**
 * Games API handles all game requests, like creating a game, joining a game, etc...
 */
@Injectable({
  providedIn: 'root',
})
export class GameService extends BaseService {
  constructor(
    config: ApiConfiguration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * Path part for operation ready
   */
  static readonly ReadyPath = '/game/ready';

  /**
   * Ready to Start.
   *
   * Request sent when a player is ready for the game to start.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `ready()` instead.
   *
   * This method doesn't expect any request body.
   */
  ready$Response(params: {
    Authorization: string;

  }): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.ReadyPath, 'post');
    if (params) {

      rb.header('Authorization', params.Authorization);

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
   * Ready to Start.
   *
   * Request sent when a player is ready for the game to start.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `ready$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  ready(params: {
    Authorization: string;

  }): Observable<ApiSuccessModel> {

    return this.ready$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

  /**
   * Path part for operation leaveLobby
   */
  static readonly LeaveLobbyPath = '/game/leave-lobby';

  /**
   * Leave Game Lobby.
   *
   * Request sent when a player leaves a game lobby.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `leaveLobby()` instead.
   *
   * This method doesn't expect any request body.
   */
  leaveLobby$Response(params: {
    Authorization: string;

  }): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.LeaveLobbyPath, 'post');
    if (params) {

      rb.header('Authorization', params.Authorization);

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
   * Leave Game Lobby.
   *
   * Request sent when a player leaves a game lobby.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `leaveLobby$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  leaveLobby(params: {
    Authorization: string;

  }): Observable<ApiSuccessModel> {

    return this.leaveLobby$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

  /**
   * Path part for operation refreshGameList
   */
  static readonly RefreshGameListPath = '/game/refresh-game-list';

  /**
   * Refresh Game List.
   *
   * Requests updated list of games.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `refreshGameList()` instead.
   *
   * This method doesn't expect any request body.
   */
  refreshGameList$Response(params: {
    Authorization: string;

  }): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.RefreshGameListPath, 'post');
    if (params) {

      rb.header('Authorization', params.Authorization);

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
   * Refresh Game List.
   *
   * Requests updated list of games.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `refreshGameList$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  refreshGameList(params: {
    Authorization: string;

  }): Observable<ApiSuccessModel> {

    return this.refreshGameList$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

  /**
   * Path part for operation createGame
   */
  static readonly CreateGamePath = '/game/create';

  /**
   * Create a new game.
   *
   * Creates a new game and returns the UUID to the client.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `createGame()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createGame$Response(params: {
    Authorization: string;
      body: CreateGameModel
  }): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.CreateGamePath, 'post');
    if (params) {

      rb.header('Authorization', params.Authorization);

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
   * Create a new game.
   *
   * Creates a new game and returns the UUID to the client.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `createGame$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createGame(params: {
    Authorization: string;
      body: CreateGameModel
  }): Observable<ApiSuccessModel> {

    return this.createGame$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

  /**
   * Path part for operation getGameList
   */
  static readonly GetGameListPath = '/game/getAll';

  /**
   * Get game list.
   *
   * Retrieves a list of games which are not full and have not yet started.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getGameList()` instead.
   *
   * This method doesn't expect any request body.
   */
  getGameList$Response(params: {
    Authorization: string;

  }): Observable<StrictHttpResponse<Array<GetGameModel>>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.GetGameListPath, 'get');
    if (params) {

      rb.header('Authorization', params.Authorization);

    }
    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<GetGameModel>>;
      })
    );
  }

  /**
   * Get game list.
   *
   * Retrieves a list of games which are not full and have not yet started.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `getGameList$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getGameList(params: {
    Authorization: string;

  }): Observable<Array<GetGameModel>> {

    return this.getGameList$Response(params).pipe(
      map((r: StrictHttpResponse<Array<GetGameModel>>) => r.body as Array<GetGameModel>)
    );
  }

  /**
   * Path part for operation joinGame
   */
  static readonly JoinGamePath = '/game/join/{gameId}';

  /**
   * Joins a game.
   *
   * Joins the game with the provided UUID, provided such a game exists.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `joinGame()` instead.
   *
   * This method doesn't expect any request body.
   */
  joinGame$Response(params: {
    Authorization: string;
    gameId: string;

  }): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.JoinGamePath, 'post');
    if (params) {

      rb.header('Authorization', params.Authorization);
      rb.path('gameId', params.gameId);

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
   * Joins a game.
   *
   * Joins the game with the provided UUID, provided such a game exists.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `joinGame$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  joinGame(params: {
    Authorization: string;
    gameId: string;

  }): Observable<ApiSuccessModel> {

    return this.joinGame$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

  /**
   * Path part for operation getGameEmitter
   */
  static readonly GetGameEmitterPath = '/game/emitter/game/{jwt}';

  /**
   * Request SSE Emitter.
   *
   * Request an SSE emitter to be sent updates regarding the game state.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getGameEmitter()` instead.
   *
   * This method doesn't expect any request body.
   */
  getGameEmitter$Response(params: {
    jwt: string;

  }): Observable<StrictHttpResponse<SseEmitter>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.GetGameEmitterPath, 'get');
    if (params) {

      rb.path('jwt', params.jwt);

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
   * Request an SSE emitter to be sent updates regarding the game state.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `getGameEmitter$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getGameEmitter(params: {
    jwt: string;

  }): Observable<SseEmitter> {

    return this.getGameEmitter$Response(params).pipe(
      map((r: StrictHttpResponse<SseEmitter>) => r.body as SseEmitter)
    );
  }

  /**
   * Path part for operation getJoinGameEmitter
   */
  static readonly GetJoinGameEmitterPath = '/game/emitter/join/{jwt}';

  /**
   * Request SSE Emitter.
   *
   * Request an SSE emitter to be sent updates to the list of games.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getJoinGameEmitter()` instead.
   *
   * This method doesn't expect any request body.
   */
  getJoinGameEmitter$Response(params: {
    jwt: string;

  }): Observable<StrictHttpResponse<SseEmitter>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.GetJoinGameEmitterPath, 'get');
    if (params) {

      rb.path('jwt', params.jwt);

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
   * Request an SSE emitter to be sent updates to the list of games.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `getJoinGameEmitter$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getJoinGameEmitter(params: {
    jwt: string;

  }): Observable<SseEmitter> {

    return this.getJoinGameEmitter$Response(params).pipe(
      map((r: StrictHttpResponse<SseEmitter>) => r.body as SseEmitter)
    );
  }

  /**
   * Path part for operation destroyJoinGameEmitter
   */
  static readonly DestroyJoinGameEmitterPath = '/game/destroy-join-game-emitter';

  /**
   * Destroy Join Game Emitter.
   *
   * Destroy the emitter that is sending updated game lists.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `destroyJoinGameEmitter()` instead.
   *
   * This method doesn't expect any request body.
   */
  destroyJoinGameEmitter$Response(params: {
    Authorization: string;

  }): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.DestroyJoinGameEmitterPath, 'post');
    if (params) {

      rb.header('Authorization', params.Authorization);

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
   * Destroy Join Game Emitter.
   *
   * Destroy the emitter that is sending updated game lists.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `destroyJoinGameEmitter$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  destroyJoinGameEmitter(params: {
    Authorization: string;

  }): Observable<ApiSuccessModel> {

    return this.destroyJoinGameEmitter$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

  /**
   * Path part for operation getGameDocumentUpdate
   */
  static readonly GetGameDocumentUpdatePath = '/game/refresh-game-doc';

  /**
   * Request Game Document Update.
   *
   * Request an updated game document.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getGameDocumentUpdate()` instead.
   *
   * This method doesn't expect any request body.
   */
  getGameDocumentUpdate$Response(params: {
    Authorization: string;

  }): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.GetGameDocumentUpdatePath, 'get');
    if (params) {

      rb.header('Authorization', params.Authorization);

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
   * Request Game Document Update.
   *
   * Request an updated game document.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `getGameDocumentUpdate$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getGameDocumentUpdate(params: {
    Authorization: string;

  }): Observable<ApiSuccessModel> {

    return this.getGameDocumentUpdate$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

}
