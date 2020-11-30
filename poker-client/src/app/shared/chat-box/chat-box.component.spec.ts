import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ChatBoxComponent} from './chat-box.component';
import {provideMockStore} from '@ngrx/store/testing';
import {ChatMessageComponent} from './chat-message/chat-message.component';
import {ChatService} from '../web-socket/chat.service';
import {MockChatService} from '../../testing/mock-services';

describe('ChatBoxComponent', () => {
  let component: ChatBoxComponent;
  let fixture: ComponentFixture<ChatBoxComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ChatBoxComponent, ChatMessageComponent],
      providers: [
        provideMockStore(),
        {
          provide: ChatService,
          useClass: MockChatService
        }
      ],
      imports: []
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChatBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
