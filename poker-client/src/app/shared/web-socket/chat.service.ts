import {Injectable, OnDestroy} from '@angular/core';
import {WebSocketService} from './web-socket.service';
import {ChatStateContainer} from '../models/app-state.model';
import {Store} from '@ngrx/store';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {gameChatMsgReceived, generalChatMsgReceived} from '../../state/app.actions';

@Injectable({
  providedIn: 'root'
})
export class ChatService implements OnDestroy {

  public chatTopicBase = '/topic/chat';
  private ngDestroyed$: Subject<any> = new Subject<any>();
  private chatTopicUnsubscribe$: Subject<any>;

  constructor(
    private webSocketService: WebSocketService,
    private chatStore: Store<ChatStateContainer>
  ) {
  }

  public ngOnDestroy(): void {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }

  public subscribeToChatTopic(gameId: string = null) {
    this.chatTopicUnsubscribe$ = new Subject<any>();
    const topic = gameId ? `${this.chatTopicBase}/${gameId}` : `${this.chatTopicBase}/general`;
    this.webSocketService.onMessage(topic)
    .pipe(takeUntil(this.chatTopicUnsubscribe$))
    .subscribe(data => {
      if (!gameId) {
        this.chatStore.dispatch(generalChatMsgReceived(data));
      } else {
        this.chatStore.dispatch(gameChatMsgReceived(data));
      }
    });
  }

  public unsubscribeFromChatTopic() {
    this.chatTopicUnsubscribe$.next();
    this.chatTopicUnsubscribe$.complete();
  }
}
