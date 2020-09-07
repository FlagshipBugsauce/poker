/* tslint:disable */

/**
 * The parameters of a game.
 */
export interface GameParameter {

  /**
   * Buy-in required to play in the game.
   */
  buyIn: number;

  /**
   * The maximum number of players allowed in the game
   */
  maxPlayers: number;

  /**
   * The name of the game to be created
   */
  name: string;

  /**
   * The value of the small blind when the game starts.
   */
  startingBlinds?: number;

  /**
   * Amount of time (in seconds) players have to act when it is their turn.
   */
  turnDuration?: number;
}
