/* tslint:disable */
import {Component, Input, OnInit} from '@angular/core';
import {CardModel} from '../../api/models';
import {CardService, CardSize} from '../card.service';
import {CardSuit, CardValue} from '../models/card.enum';


@Component({
  selector: 'pkr-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnInit {
  /**
   * Models of the card (default value of Ace of Spades).
   */
  @Input() card: CardModel = {suit: CardSuit.Spades, value: CardValue.Ace};

  /**
   * Size of the card.
   */
  @Input() size: CardSize;
  @Input() pxSize: number = -1;
  @Input() faceDown: boolean = false;
  public rotateY: number = 0;

  constructor(public cardService: CardService) {
  }

  /**
   * Getter that will return a textual representation of a card, i.e. "Ace of Spades".
   */
  public get cardText(): string {
    return this.card != null ? `${this.card.value} of ${this.card.suit}` : 'n/a';
  }

  ngOnInit(): void {
  }
}
