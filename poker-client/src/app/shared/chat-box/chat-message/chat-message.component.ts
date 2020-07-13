import {Component, Input, OnInit} from '@angular/core';
import {ChatMessageModel} from '../../../api/models';

@Component({
  selector: 'pkr-chat-message',
  templateUrl: './chat-message.component.html',
  styleUrls: ['./chat-message.component.scss']
})
export class ChatMessageComponent implements OnInit {

  @Input() message: ChatMessageModel;

  constructor() {
  }

  ngOnInit(): void {
  }

}
