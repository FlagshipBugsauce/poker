/* tslint:disable */
import {GamePlayerModel} from './game-player-model';

export interface PokerTableModel {
  actingPlayer?: number;
  dealer?: number;
  displayHandSummary?: boolean;
  playerThatActed?: number;
  players?: Array<GamePlayerModel>;
  startTurnTimer?: number;
}
