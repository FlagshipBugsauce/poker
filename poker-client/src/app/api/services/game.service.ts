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
import {CreateGameModel} from '../models/create-game-model';
import {GameDocument} from '../models/game-document';
import {GetGameModel} from '../models/get-game-model';


/**
 * Games API handles all game requests, like creating a game, joining a game, etc...
 */
@Injectable({
  providedIn: 'root',
})
export class GameService extends BaseService {
  /**
   * Path part for operation ready
   */
  static readonly ReadyPath = '/game/ready';
  /**
   * Path part for operation joinGame
   */
  static readonly JoinGamePath = '/game/join/{gameId}';
  /**
   * Path part for operation createGame
   */
  static readonly CreateGamePath = '/game/create';
  /**
   * Path part for operation startGame
   */
  static readonly StartGamePath = '/game/start';
  /**
   * Path part for operation getGameList
   */
  static readonly GetGameListPath = '/game/get-list';
  /**
   * Path part for operation leaveLobby
   */
  static readonly LeaveLobbyPath = '/game/leave-lobby';
  /**
   * Path part for operation getGameDocument
   */
  static readonly GetGameDocumentPath = '/game/get-game-document';

  constructor(
    config: ApiConfiguration,
    http: HttpClient
  ) {
    super(config, http);
  }

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
  ready$Response(params?: {}): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.ReadyPath, 'post');
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
   * Ready to Start.
   *
   * Request sent when a player is ready for the game to start.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `ready$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  ready(params?: {}): Observable<ApiSuccessModel> {

    return this.ready$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

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
    gameId: string;

  }): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.JoinGamePath, 'post');
    if (params) {

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
    gameId: string;

  }): Observable<ApiSuccessModel> {

    return this.joinGame$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

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
    body: CreateGameModel
  }): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.CreateGamePath, 'post');
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
    body: CreateGameModel
  }): Observable<ApiSuccessModel> {

    return this.createGame$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

  /**
   * Start Game.
   *
   * Starts the game, provided all preconditions are satisfied..
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `startGame()` instead.
   *
   * This method doesn't expect any request body.
   */
  startGame$Response(params?: {}): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.StartGamePath, 'post');
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
   * Start Game.
   *
   * Starts the game, provided all preconditions are satisfied..
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `startGame$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  startGame(params?: {}): Observable<ApiSuccessModel> {

    return this.startGame$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

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
  getGameList$Response(params?: {}): Observable<StrictHttpResponse<Array<GetGameModel>>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.GetGameListPath, 'get');
    if (params) {


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
  getGameList(params?: {}): Observable<Array<GetGameModel>> {

    return this.getGameList$Response(params).pipe(
      map((r: StrictHttpResponse<Array<GetGameModel>>) => r.body as Array<GetGameModel>)
    );
  }

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
  leaveLobby$Response(params?: {}): Observable<StrictHttpResponse<ApiSuccessModel>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.LeaveLobbyPath, 'post');
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
   * Leave Game Lobby.
   *
   * Request sent when a player leaves a game lobby.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `leaveLobby$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  leaveLobby(params?: {}): Observable<ApiSuccessModel> {

    return this.leaveLobby$Response(params).pipe(
      map((r: StrictHttpResponse<ApiSuccessModel>) => r.body as ApiSuccessModel)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getGameDocument()` instead.
   *
   * This method doesn't expect any request body.
   */
  getGameDocument$Response(params?: {}): Observable<StrictHttpResponse<GameDocument>> {

    const rb = new RequestBuilder(this.rootUrl, GameService.GetGameDocumentPath, 'post');
    if (params) {


    }
    return this.http.request(rb.build({
      responseType: 'blob',
      accept: '*/*'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<GameDocument>;
      })
    );
  }

  /**
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `getGameDocument$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getGameDocument(params?: {}): Observable<GameDocument> {

    return this.getGameDocument$Response(params).pipe(
      map((r: StrictHttpResponse<GameDocument>) => r.body as GameDocument)
    );
  }

}