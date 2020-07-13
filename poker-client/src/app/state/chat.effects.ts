import {Injectable} from '@angular/core';
import {WebSocketService} from '../shared/web-socket/web-socket.service';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {sendChatMessage} from './app.actions';
import {tap} from 'rxjs/operators';
import {ClientMessageModel} from '../api/models/client-message-model';
import {AppStateContainer} from '../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {selectJwt} from './app.selector';

@Injectable()
export class ChatEffects {

  private jwt;

  sendChatMessage$ = createEffect(() => this.actions$.pipe(
    ofType(sendChatMessage),
    tap((action: ClientMessageModel) =>
      this.webSocketService.send('/topic/chat/send', {jwt: this.jwt, ...action}))
  ), {dispatch: false});

  constructor(
    private actions$: Actions,
    private webSocketService: WebSocketService,
    private appStore: Store<AppStateContainer>
  ) {
    this.appStore.select(selectJwt).subscribe(jwt => this.jwt = jwt);
  }
}
