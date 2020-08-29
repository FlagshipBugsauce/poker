/* tslint:disable */
import {Card} from './card';

/**
 * A winner of a hand, could be one of several.
 */
export interface Winner {
  cards?: Array<Card>;

  /**
   * The ID of the winning player.
   */
  id?: string;

  /**
   * Type of hand.
   */
  type?: 'StraightFlush' | 'FourOfAKind' | 'FullHouse' | 'Flush' | 'Straight' | 'Set' | 'TwoPair' | 'Pair' | 'HighCard' | 'NotShown';

  /**
   * The amount the player won.
   */
  winnings?: number;
}
