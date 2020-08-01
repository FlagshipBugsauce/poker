/* tslint:disable */
import {CardModel} from './card-model';

/**
 * Model representing data in the draw game.
 */
export interface DrawGameDrawModel {
  card?: CardModel;

  /**
   * Flag that is true if this draw won the hand.
   */
  winner?: boolean;
}
