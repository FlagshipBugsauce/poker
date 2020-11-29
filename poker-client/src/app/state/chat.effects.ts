import {Injectable} from '@angular/core';
import {WebSocketService} from '../shared/web-socket/web-socket.service';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {closeChat, sendChatMessage, startChat} from './app.actions';
import {tap} from 'rxjs/operators';
import {ClientMessage} from '../api/models/client-message';
import {AppStateContainer} from '../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {selectJwt} from './app.selector';
import {ChatService} from '../shared/web-socket/chat.service';

@Injectable()
export class ChatEffects {

  startChat$ = createEffect(() => this.actions$.pipe(
    ofType(startChat),
    tap((action: ClientMessage) => this.chatService.subscribeToChatTopic(action.gameId))
  ), {dispatch: false});
  closeChat$ = createEffect(() => this.actions$.pipe(
    ofType(closeChat), tap(() => this.chatService.unsubscribeFromChatTopic())
  ), {dispatch: false});
  private jwt;
  sendChatMessage$ = createEffect(() => this.actions$.pipe(
    ofType(sendChatMessage),
    tap((action: ClientMessage) =>
      this.webSocketService.send('/topic/chat/send', {jwt: this.jwt, ...action}))
  ), {dispatch: false});

  constructor(
    private actions$: Actions,
    private webSocketService: WebSocketService,
    private chatService: ChatService,
    private appStore: Store<AppStateContainer>
  ) {
    this.appStore.select(selectJwt).subscribe(jwt => this.jwt = jwt);
  }
}
