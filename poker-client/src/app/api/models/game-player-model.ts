/* tslint:disable */

/**
 * Model representing a player in a game.
 */
export interface GamePlayerModel {

  /**
   * Specifies whether a player is active.
   */
  active?: boolean;

  /**
   * User's email address.
   */
  email?: string;

  /**
   * User's first name.
   */
  firstName?: string;

  /**
   * User's user group.
   */
  group?: 'Administrator' | 'Client' | 'Guest';

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
