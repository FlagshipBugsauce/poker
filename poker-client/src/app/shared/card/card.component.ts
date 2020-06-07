import {Component, Input, OnInit} from '@angular/core';
import {CardModel} from '../../api/models';
import {CardService} from '../card.service';

@Component({
  selector: 'pkr-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnInit {
  @Input() card: CardModel;
  @Input() size: string;

  /**
   * Getter that will return a textual representation of a card, i.e. "Ace of Spades".
   */
  public get cardText(): string {
    return this.card != null ? `${this.card.value} of ${this.card.suit}` : 'n/a';
  }

  constructor(public cardService: CardService) { }

  ngOnInit(): void {
  }

}
