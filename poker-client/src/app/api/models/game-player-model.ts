/* tslint:disable */
import {CardModel} from './card-model';
import {TableControlsModel} from './table-controls-model';

/**
 * Model representing a player in a game.
 */
export interface GamePlayerModel {

  /**
   * Specifies whether a player is active.
   */
  away?: boolean;

  /**
   * Cards
   */
  cards?: Array<CardModel>;
  controls?: TableControlsModel;

  /**
   * User's first name.
   */
  firstName?: string;

  /**
   * Player is no longer in the hand.
   */
  folded?: boolean;

  /**
   * User's ID.
   */
  id?: string;

  /**
   * User's last name.
   */
  lastName?: string;

  /**
   * Specifies whether a player is out of the game.
   */
  out?: boolean;
}
