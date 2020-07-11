/* tslint:disable */
import { GamePlayerModel } from './game-player-model';
export interface GameModel {
  hands?: Array<string>;

  /**
   * Game ID (same as game lobby ID).
   */
  id?: string;

  /**
   * Game phase.
   */
  phase?: 'Lobby' | 'Play' | 'Over';
  players?: Array<GamePlayerModel>;

  /**
   * Amount of time each player has to act.
   */
  timeToAct?: number;

  /**
   * Total number of hands in the game.
   */
  totalHands?: number;
}
