/* tslint:disable */
import {GameParameter} from './game-parameter';
import {LobbyPlayer} from './lobby-player';

/**
 * Game lobby containing information such as game parameters, players, etc...
 */
export interface Lobby {
  host?: LobbyPlayer;

  /**
   * Lobby's ID.
   */
  id?: string;
  parameters?: GameParameter;
  players?: Array<LobbyPlayer>;
}
