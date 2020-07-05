/* tslint:disable */

/**
 * Contains information regarding the game a player is currently in.
 */
export interface CurrentGameModel {

  /**
   * ID of the game a player is in, if the player is in a game.
   */
  id?: string;

  /**
   * Flag that represents whether a player is currently in a game.
   */
  inGame?: boolean;
}
