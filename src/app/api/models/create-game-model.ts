/* tslint:disable */
export interface CreateGameModel {

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
}
