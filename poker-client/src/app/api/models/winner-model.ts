/* tslint:disable */
import {CardModel} from './card-model';

/**
 * A winner of a hand, could be one of several.
 */
export interface WinnerModel {
  cards?: Array<CardModel>;

  /**
   * The ID of the winning player.
   */
  id?: string;

  /**
   * The amount the player won.
   */
  winnings?: number;
}
