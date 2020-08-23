/* tslint:disable */
import {GameParameter} from './game-parameter';
import {LobbyPlayer} from './lobby-player';

export interface GameList {

  /**
   * The current number of players in the game
   */
  currentPlayers?: number;
  host?: LobbyPlayer;

  /**
   * The ID of the game.
   */
  id?: string;
  parameters?: GameParameter;
}
