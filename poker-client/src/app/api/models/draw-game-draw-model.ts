/* tslint:disable */
import {CardModel} from './card-model';

/**
 * Model representing data in the draw game.
 */
export interface DrawGameDrawModel {

  /**
   * Flag that is true if this draw is next.
   */
  acting?: boolean;
  card?: CardModel;

  /**
   * Flag that is true if this draw won the hand.
   */
  winner?: boolean;
}
