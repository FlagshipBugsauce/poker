/* tslint:disable */
import {GamePlayerModel} from './game-player-model';
import {HandSummaryModel} from './hand-summary-model';

export interface PokerTableModel {
  actingPlayer?: number;
  dealer?: number;
  displayHandSummary?: boolean;
  playerThatActed?: number;
  players?: Array<GamePlayerModel>;
  startTurnTimer?: number;
  summary?: HandSummaryModel;
}
