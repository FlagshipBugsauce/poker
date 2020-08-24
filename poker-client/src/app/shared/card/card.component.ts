/* tslint:disable */
import {Component, Input, OnInit} from '@angular/core';
import {Card} from '../../api/models';
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
  @Input() card: Card = {suit: CardSuit.Spades, value: CardValue.Ace};

  /**
   * Size of the card.
   */
  @Input() size: CardSize;
  @Input() pxSize: number = -1;

  constructor(public cardService: CardService) {
  }

  /**
   * Getter that will return a textual representation of a card, i.e. "Ace of Spades".
   */
  public get cardText(): string {
    return this.card ? this.cardService.cardName(this.card) : 'n/a';
  }

  ngOnInit(): void {
  }
}
