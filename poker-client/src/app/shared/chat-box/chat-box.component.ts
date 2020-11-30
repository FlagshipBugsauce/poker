import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ChatMessage} from '../../api/models/chat-message';
import {AppStateContainer, ChatStateContainer} from '../models/app-state.model';
import {Store} from '@ngrx/store';
import {Observable, Subject} from 'rxjs';
import {selectAuthenticated, selectGameChat, selectGeneralChat} from '../../state/app.selector';
import {takeUntil} from 'rxjs/operators';
import {closeChat, sendChatMessage, startChat} from '../../state/app.actions';

@Component({
  selector: 'pkr-chat-box',
  templateUrl: './chat-box.component.html',
  styleUrls: ['./chat-box.component.scss']
})
export class ChatBoxComponent implements OnInit, OnDestroy {

  /**
   * If the chat is for a specific game, this field is the ID of that game and is used to determine
   * which topic should be listened to.
   */
  @Input() gameId: string;

  /**
   * Text input where a user types in their chat message.
   */
  @ViewChild('chatInput') chatInput;

  /**
   * Text box where chat messages appear.
   */
  @ViewChild('chatBox') chatBox;

  /**
   * Messages that have been received by the chat since this component opened.
   */
  public messages: ChatMessage[] = [];

  /**
   * Observable flag which is true if user has authenticated, false otherwise.
   */
  public authenticated$: Observable<boolean>;

  /**
   * Used to ensure we're not maintaining multiple subscriptions.
   */
  private ngDestroyed$: Subject<any> = new Subject<any>();

  constructor(
    private appStore: Store<AppStateContainer>,
    private chatStore: Store<ChatStateContainer>) {
  }

  ngOnInit(): void {
    this.authenticated$ =
      this.appStore.select(selectAuthenticated).pipe(takeUntil(this.ngDestroyed$));
    this.chatStore.select(this.gameId ? selectGameChat : selectGeneralChat)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((message: ChatMessage) => {
        if (message.timestamp) {
          this.messages.push(message);
        }

        if (this.chatBox) {
          this.scrollDown().then();
        }
      });
    this.chatStore.dispatch(startChat({gameId: this.gameId}));
  }

  /**
   * Scrolls down the chat box whenever a message is received.
   * TODO: There is probably a better way of doing this. The issue here is that there is a delay
   *  between a message being received and the scroll height of the chat box increasing, so we need
   *  some way of ensuring that new messages actually appear when they are received, without the
   *  user having to manually scroll down.
   */
  public async scrollDown() {
    let i = 0;
    while (i++ < 100) {
      await new Promise(resolve => setTimeout(resolve, 10));
      this.chatBox.nativeElement.scrollTop = this.chatBox.nativeElement.scrollHeight;
    }
  }

  ngOnDestroy(): void {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
    this.chatStore.dispatch(closeChat());
  }

  /**
   * Sends a message.
   */
  public sendMessage() {
    if (this.chatInput.nativeElement.value !== '') {
      this.chatStore.dispatch(sendChatMessage({
        gameId: this.gameId,
        data: this.chatInput.nativeElement.value
      }));
      this.chatInput.nativeElement.value = '';
    }

  }
}
