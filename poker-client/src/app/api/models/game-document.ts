/* tslint:disable */
import {GamePlayerModel} from './game-player-model';
import {GameSummaryModel} from './game-summary-model';

export interface GameDocument {
  hands?: Array<string>;

  /**
   * Game ID (same as game lobby ID).
   */
  id?: string;
  players?: Array<GamePlayerModel>;

  /**
   * Game state.
   */
  state?: 'Lobby' | 'Play' | 'Over';
  summary?: GameSummaryModel;

  /**
   * Amount of time each player has to act.
   */
  timeToAct?: number;

  /**
   * Total number of hands in the game.
   */
  totalHands?: number;
}
