/* tslint:disable */

/**
 * The ID of the host.
 */
export interface LobbyPlayerModel {

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
   * Specifies whether the player created the game.
   */
  host?: boolean;

  /**
   * User's ID.
   */
  id?: string;

  /**
   * User's last name.
   */
  lastName?: string;

  /**
   * Specifies whether a player is ready to start the game.
   */
  ready?: boolean;
}
