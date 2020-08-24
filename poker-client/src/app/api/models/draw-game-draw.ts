/* tslint:disable */
import {Card} from './card';

/**
 * Model representing data in the draw game.
 */
export interface DrawGameDraw {
  card?: Card;

  /**
   * Flag that is true if this draw won the hand.
   */
  winner?: boolean;
}
