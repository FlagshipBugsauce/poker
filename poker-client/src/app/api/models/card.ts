/* tslint:disable */

/**
 * Model of a card.
 */
export interface Card {

  /**
   * The suit of the card.
   */
  suit?: 'Spades' | 'Hearts' | 'Clubs' | 'Diamonds' | 'Back';

  /**
   * The value of the card.
   */
  value?: 'Ace' | 'King' | 'Queen' | 'Jack' | 'Ten' | 'Nine' | 'Eight' | 'Seven' | 'Six' | 'Five' | 'Four' | 'Three' | 'Two' | 'Back';
}
