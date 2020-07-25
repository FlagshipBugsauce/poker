/* tslint:disable */
import {CardModel} from './card-model';

/**
 * Model representing a player in a game.
 */
export interface GamePlayerModel {

  /**
   * Specifies whether a player needs to act.
   */
  acting?: boolean;

  /**
   * Specifies whether a player is active.
   */
  away?: boolean;

  /**
   * Size of the players bank roll.
   */
  bankRoll?: number;

  /**
   * Cards
   */
  cards?: Array<CardModel>;

  /**
   * User's first name.
   */
  firstName?: string;

  /**
   * User's ID.
   */
  id?: string;

  /**
   * User's last name.
   */
  lastName?: string;

  /**
   * The players current score.
   */
  score?: number;
}
