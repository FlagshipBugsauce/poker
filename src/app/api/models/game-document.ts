/* tslint:disable */
import { GameActionModel } from './game-action-model';
import { PlayerModel } from './player-model';
export interface GameDocument {

  /**
   * Buy-in required to play in the game.
   */
  buyIn?: number;

  /**
   * Current state of the game.
   */
  currentGameState?: 'PreGame' | 'Game' | 'PostGame';
  gameActions?: Array<GameActionModel>;

  /**
   * Host's ID.
   */
  host?: string;

  /**
   * Game's ID.
   */
  id?: string;

  /**
   * Maximum number of players allowed in the game.
   */
  maxPlayers?: number;

  /**
   * Name of the game.
   */
  name?: string;
  players?: Array<PlayerModel>;
}
