/* tslint:disable */
import {GamePlayer} from './game-player';

/**
 * Information that defines the game state, such as phase, players, etc...
 */
export interface Game {

  /**
   * Game ID (same as game lobby ID).
   */
  id?: string;

  /**
   * Game phase.
   */
  phase?: 'Lobby' | 'Play' | 'Over';
  players?: Array<GamePlayer>;

  /**
   * Amount of time each player has to act.
   */
  timeToAct?: number;
}
