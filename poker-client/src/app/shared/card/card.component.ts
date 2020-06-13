import {Component, Input, OnInit} from '@angular/core';
import {CardModel} from '../../api/models';
import {CardService} from '../card.service';
import {CardSuit, CardValue} from '../models/card.enum';

export enum CardSize {
  ExtraSmall = 'xs',
  Small = 'sm',
  Medium = 'md',
  Large = 'lg',
  ExtraLarge = 'xl'
}

@Component({
  selector: 'pkr-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnInit {
  /**
   * Models of the card (default value of Ace of Spades).
   */
  @Input() card: CardModel = { suit: CardSuit.Spades, value: CardValue.Ace };

  /**
   * Size of the card.
   */
  @Input() size: CardSize;

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
