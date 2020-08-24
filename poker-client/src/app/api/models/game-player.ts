/* tslint:disable */
import {Card} from './card';
import {TableControls} from './table-controls';

/**
 * Model representing a player in a game.
 */
export interface GamePlayer {

  /**
   * Player bet entire bankroll.
   */
  allIn?: boolean;

  /**
   * Specifies whether a player is active.
   */
  away?: boolean;
  bet?: number;

  /**
   * Cards
   */
  cards?: Array<Card>;
  chips?: number;
  controls?: TableControls;

  /**
   * User's first name.
   */
  firstName?: string;

  /**
   * Player is no longer in the hand when this is true.
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
  toCall?: number;
}
