/* tslint:disable */
import { LobbyPlayerModel } from './lobby-player-model';
export interface GetGameModel {

  /**
   * The buy-in required to play.
   */
  buyIn?: number;

  /**
   * The current number of players in the game
   */
  currentPlayers?: number;
  host?: LobbyPlayerModel;

  /**
   * The ID of the game.
   */
  id?: string;

  /**
   * The maximum number of players allowed in the game
   */
  maxPlayers?: number;

  /**
   * The name of the game.
   */
  name?: string;
}
