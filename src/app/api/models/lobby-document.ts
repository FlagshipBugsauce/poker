/* tslint:disable */
import { GameActionModel } from './game-action-model';
import { PlayerModel } from './player-model';
export interface LobbyDocument {

  /**
   * Buy-in required to play in the game.
   */
  buyIn?: number;
  gameActions?: Array<GameActionModel>;

  /**
   * Host's ID.
   */
  host?: string;

  /**
   * Lobby's ID.
   */
  id?: string;

  /**
   * Maximum number of players allowed in the game.
   */
  maxPlayers?: string;

  /**
   * Name of the game.
   */
  name?: string;
  players?: Array<PlayerModel>;
}
