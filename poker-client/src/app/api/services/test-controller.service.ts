/* tslint:disable */
import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {BaseService} from '../base-service';
import {ApiConfiguration} from '../api-configuration';
import {StrictHttpResponse} from '../strict-http-response';
import {RequestBuilder} from '../request-builder';
import {Observable} from 'rxjs';
import {filter, map} from 'rxjs/operators';

import {ActionModel} from '../models/action-model';
import {CurrentGameModel} from '../models/current-game-model';
import {DrawGameDataContainerModel} from '../models/draw-game-data-container-model';
import {DrawGameDataModel} from '../models/draw-game-data-model';
import {GameDocument} from '../models/game-document';
import {HandDocument} from '../models/hand-document';
import {LobbyDocument} from '../models/lobby-document';
import {ToastModel} from '../models/toast-model';
import {WebSocketUpdateModel} from '../models/web-socket-update-model';

@Injectable({
  providedIn: 'root',
})
export class TestControllerService extends BaseService {
  /**
   * Path part for operation gameData
   */
  static readonly GameDataPath = '/test/test/gameData';
  /**
   * Path part for operation currentGameModel
   */
  static readonly CurrentGameModelPath = '/test/test/websocket/models/current-game-model';
  /**
   * Path part for operation handDocument
   */
  static readonly HandDocumentPath = '/test/test/handDoc';
  /**
   * Path part for operation lobbyDocument
   */
  static readonly LobbyDocumentPath = '/test/test/lobbyDoc';
  /**
   * Path part for operation toastModel
   */
  static readonly ToastModelPath = '/test/test/websocket/models/toast-model';
  /**
   * Path part for operation actionModel
   */
  static readonly ActionModelPath = '/test/test/websocket/models/action-model';
  /**
   * Path part for operation gameDocument
   */
  static readonly GameDocumentPath = '/test/test/gameDoc';
  /**
   * Path part for operation gameDataContainer
   */
  static readonly GameDataContainerPath = '/test/test/gameDataContainer';
  /**
   * Path part for operation webSocketUpdateModel
   */
  static readonly WebSocketUpdateModelPath = '/test/test/websocketUpdateModel';

  constructor(
    config: ApiConfiguration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `gameData()` instead.
   *
   * This method doesn't expect any request body.
   */
  gameData$Response(params?: {}): Observable<StrictHttpResponse<Array<DrawGameDataModel>>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.GameDataPath, 'get');
    if (params) {


    }
    return this.http.request(rb.build({
      responseType: 'blob',
      accept: '*/*'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<DrawGameDataModel>>;
      })
    );
  }

  /**
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `gameData$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  gameData(params?: {}): Observable<Array<DrawGameDataModel>> {

    return this.gameData$Response(params).pipe(
      map((r: StrictHttpResponse<Array<DrawGameDataModel>>) => r.body as Array<DrawGameDataModel>)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `currentGameModel()` instead.
   *
   * This method doesn't expect any request body.
   */
  currentGameModel$Response(params?: {}): Observable<StrictHttpResponse<CurrentGameModel>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.CurrentGameModelPath, 'get');
    if (params) {


    }
    return this.http.request(rb.build({
      responseType: 'blob',
      accept: '*/*'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<CurrentGameModel>;
      })
    );
  }

  /**
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `currentGameModel$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  currentGameModel(params?: {}): Observable<CurrentGameModel> {

    return this.currentGameModel$Response(params).pipe(
      map((r: StrictHttpResponse<CurrentGameModel>) => r.body as CurrentGameModel)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `handDocument()` instead.
   *
   * This method doesn't expect any request body.
   */
  handDocument$Response(params?: {}): Observable<StrictHttpResponse<HandDocument>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.HandDocumentPath, 'get');
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
   * To access the full response (for headers, for example), `handDocument$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  handDocument(params?: {}): Observable<HandDocument> {

    return this.handDocument$Response(params).pipe(
      map((r: StrictHttpResponse<HandDocument>) => r.body as HandDocument)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `lobbyDocument()` instead.
   *
   * This method doesn't expect any request body.
   */
  lobbyDocument$Response(params?: {}): Observable<StrictHttpResponse<LobbyDocument>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.LobbyDocumentPath, 'get');
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
   * To access the full response (for headers, for example), `lobbyDocument$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  lobbyDocument(params?: {}): Observable<LobbyDocument> {

    return this.lobbyDocument$Response(params).pipe(
      map((r: StrictHttpResponse<LobbyDocument>) => r.body as LobbyDocument)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `toastModel()` instead.
   *
   * This method doesn't expect any request body.
   */
  toastModel$Response(params?: {}): Observable<StrictHttpResponse<ToastModel>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.ToastModelPath, 'get');
    if (params) {


    }
    return this.http.request(rb.build({
      responseType: 'blob',
      accept: '*/*'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<ToastModel>;
      })
    );
  }

  /**
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `toastModel$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  toastModel(params?: {}): Observable<ToastModel> {

    return this.toastModel$Response(params).pipe(
      map((r: StrictHttpResponse<ToastModel>) => r.body as ToastModel)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `actionModel()` instead.
   *
   * This method doesn't expect any request body.
   */
  actionModel$Response(params?: {}): Observable<StrictHttpResponse<ActionModel>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.ActionModelPath, 'get');
    if (params) {


    }
    return this.http.request(rb.build({
      responseType: 'blob',
      accept: '*/*'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<ActionModel>;
      })
    );
  }

  /**
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `actionModel$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  actionModel(params?: {}): Observable<ActionModel> {

    return this.actionModel$Response(params).pipe(
      map((r: StrictHttpResponse<ActionModel>) => r.body as ActionModel)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `gameDocument()` instead.
   *
   * This method doesn't expect any request body.
   */
  gameDocument$Response(params?: {}): Observable<StrictHttpResponse<GameDocument>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.GameDocumentPath, 'get');
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
   * To access the full response (for headers, for example), `gameDocument$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  gameDocument(params?: {}): Observable<GameDocument> {

    return this.gameDocument$Response(params).pipe(
      map((r: StrictHttpResponse<GameDocument>) => r.body as GameDocument)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `gameDataContainer()` instead.
   *
   * This method doesn't expect any request body.
   */
  gameDataContainer$Response(params?: {}): Observable<StrictHttpResponse<DrawGameDataContainerModel>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.GameDataContainerPath, 'get');
    if (params) {


    }
    return this.http.request(rb.build({
      responseType: 'blob',
      accept: '*/*'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<DrawGameDataContainerModel>;
      })
    );
  }

  /**
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `gameDataContainer$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  gameDataContainer(params?: {}): Observable<DrawGameDataContainerModel> {

    return this.gameDataContainer$Response(params).pipe(
      map((r: StrictHttpResponse<DrawGameDataContainerModel>) => r.body as DrawGameDataContainerModel)
    );
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `webSocketUpdateModel()` instead.
   *
   * This method doesn't expect any request body.
   */
  webSocketUpdateModel$Response(params?: {}): Observable<StrictHttpResponse<WebSocketUpdateModel>> {

    const rb = new RequestBuilder(this.rootUrl, TestControllerService.WebSocketUpdateModelPath, 'get');
    if (params) {


    }
    return this.http.request(rb.build({
      responseType: 'blob',
      accept: '*/*'
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<WebSocketUpdateModel>;
      })
    );
  }

  /**
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `webSocketUpdateModel$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  webSocketUpdateModel(params?: {}): Observable<WebSocketUpdateModel> {

    return this.webSocketUpdateModel$Response(params).pipe(
      map((r: StrictHttpResponse<WebSocketUpdateModel>) => r.body as WebSocketUpdateModel)
    );
  }

}
