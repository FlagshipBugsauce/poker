/* tslint:disable */
import { GameSummaryModel } from './game-summary-model';
import { PlayerModel } from './player-model';
export interface GameDocument {
  hands?: Array<string>;

  /**
   * Game ID (same as game lobby ID).
   */
  id?: string;
  players?: Array<PlayerModel>;

  /**
   * Game state.
   */
  state?: 'Lobby' | 'Play' | 'Over';
  summary?: GameSummaryModel;
}
