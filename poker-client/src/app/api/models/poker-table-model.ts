/* tslint:disable */
import {GamePlayerModel} from './game-player-model';
import {HandSummaryModel} from './hand-summary-model';

export interface PokerTableModel {

  /**
   * Position of the player who is acting.
   */
  actingPlayer?: number;

  /**
   * Position of the dealer.
   */
  dealer?: number;

  /**
   * Flag to determine whether the summary of winning hand should be displayed.
   */
  displayHandSummary?: boolean;

  /**
   * This is incremented whenever some action is performed.
   */
  eventTracker?: number;

  /**
   * Position of the player that acted.
   */
  playerThatActed?: number;
  players?: Array<GamePlayerModel>;
  summary?: HandSummaryModel;
}
