/* tslint:disable */

/**
 * Card that was drawn.
 */
export interface CardModel {

  /**
   * The suit of the card.
   */
  suit?: 'Spades' | 'Hearts' | 'Clubs' | 'Diamonds';

  /**
   * The value of the card.
   */
  value?: 'Ace' | 'King' | 'Queen' | 'Jack' | 'Ten' | 'Nine' | 'Eight' | 'Seven' | 'Six' | 'Five' | 'Four' | 'Three' | 'Two';
}
