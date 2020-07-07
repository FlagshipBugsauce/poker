/* tslint:disable */

/**
 * The ID of the host.
 */
export interface LobbyPlayerModel {

  /**
   * User's first name.
   */
  firstName?: string;

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
