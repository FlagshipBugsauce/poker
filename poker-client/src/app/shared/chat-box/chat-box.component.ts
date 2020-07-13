import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ChatMessageModel} from '../../api/models/chat-message-model';
import {ChatStateContainer} from '../models/app-state.model';
import {Store} from '@ngrx/store';
import {Subject} from 'rxjs';
import {selectGameChat, selectGeneralChat} from '../../state/app.selector';
import {takeUntil} from 'rxjs/operators';
import {sendChatMessage} from '../../state/app.actions';
import {ChatService} from '../web-socket/chat.service';

@Component({
  selector: 'pkr-chat-box',
  templateUrl: './chat-box.component.html',
  styleUrls: ['./chat-box.component.scss']
})
export class ChatBoxComponent implements OnInit, OnDestroy {

  @Input() gameId: string;

  @ViewChild('chatInput') chatInput;

  @ViewChild('chatBox') chatBox;

  public messages: ChatMessageModel[] = [];

  private ngDestroyed$: Subject<any> = new Subject<any>();

  constructor(
    private chatStore: Store<ChatStateContainer>,
    private chatService: ChatService
  ) {
  }

  ngOnInit(): void {
    this.chatStore.select(this.gameId ? selectGameChat : selectGeneralChat)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((message: ChatMessageModel) => {
      if (message.timestamp) {
        this.messages.push(message);
      }

      if (this.chatBox) {
        this.scrollDown().then();
      }
    });

    this.chatService.subscribeToChatTopic(this.gameId);
  }

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
    this.chatService.unsubscribeFromChatTopic();
  }

  public sendMessage() {
    if (this.chatInput.nativeElement.value !== '') {
      this.chatStore.dispatch(sendChatMessage({
        gameId: this.gameId,
        data: this.chatInput.nativeElement.value
      }));
      this.chatInput.nativeElement.value = '';
      this.scrollDown().then();
    }

  }
}
