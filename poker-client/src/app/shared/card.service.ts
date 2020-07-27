import {Injectable} from '@angular/core';
import {CardSuit, CardValue} from './models/card.enum';
import {CardModel} from '../api/models/card-model';

export enum CardSize {
  ExtraSmall = 'xs',
  Small = 'sm',
  Medium = 'md',
  Large = 'lg',
  ExtraLarge = 'xl'
}


@Injectable({
  providedIn: 'root'
})
export class CardService {
  /**
   * Mapping of card suits to characters that represent each suit.
   */
  public suitMapping = {};

  /**
   * Mapping of card values to characters that represent each value.
   */
  public valueMapping = {};

  constructor() {
    this.suitMapping[CardSuit.Spades] = 'S';
    this.suitMapping[CardSuit.Hearts] = 'H';
    this.suitMapping[CardSuit.Clubs] = 'C';
    this.suitMapping[CardSuit.Diamonds] = 'D';
    this.valueMapping[CardValue.Ace] = 'A';
    this.valueMapping[CardValue.King] = 'K';
    this.valueMapping[CardValue.Queen] = 'Q';
    this.valueMapping[CardValue.Jack] = 'J';
    this.valueMapping[CardValue.Ten] = 'T';
    this.valueMapping[CardValue.Nine] = '9';
    this.valueMapping[CardValue.Eight] = '8';
    this.valueMapping[CardValue.Seven] = '7';
    this.valueMapping[CardValue.Six] = '6';
    this.valueMapping[CardValue.Five] = '5';
    this.valueMapping[CardValue.Four] = '4';
    this.valueMapping[CardValue.Three] = '3';
    this.valueMapping[CardValue.Two] = '2';
  }

  /**
   * Returns a symbolic representation of a card, i.e. AS for Ace of Spades.
   * @param card The card being converted to alphanumeric characters.
   */
  public getCardShortName(card: CardModel): string {
    return `${this.valueMapping[card.value]}${this.suitMapping[card.suit]}`;
  }

  public cardName(card: CardModel): string {
    return `${card.value} of ${card.suit}`;
  }

  /**
   * Returns the path to the image of a card.
   * @param card The card.
   */
  public getCardImagePath(card: CardModel): string {
    return `assets/icons/cards/${this.getCardShortName(card)}.svg`;
  }

  public getFlippedPath(): string {
    return 'assets/icons/cards/2B.svg';
  }

  public cardSizeToPxSize(size: CardSize): number {
    switch (size) {
      case CardSize.ExtraSmall:
        return 40;
      case CardSize.Small:
        return 80;
      case CardSize.Medium:
        return 100;
      case CardSize.Large:
        return 160;
      case CardSize.ExtraLarge:
        return 220;
      default:
        return 100;
    }
  }
}
