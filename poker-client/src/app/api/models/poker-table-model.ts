/* tslint:disable */
import {CardModel} from './card-model';
import {GamePlayerModel} from './game-player-model';
import {HandSummaryModel} from './hand-summary-model';
import {PotModel} from './pot-model';
import {WinnerModel} from './winner-model';

export interface PokerTableModel {

  /**
   * Position of the player who is acting.
   */
  actingPlayer?: number;

  /**
   * Flag that is true when a betting round is taking place.
   */
  betting?: boolean;

  /**
   * Blinds.
   */
  blind?: number;

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
   * The round will end once this player has acted.
   */
  lastToAct?: number;

  /**
   * Minimum raise amount.
   */
  minRaise?: number;

  /**
   * Position of the player that acted.
   */
  playerThatActed?: number;

  /**
   * List of players in the game.
   */
  players?: Array<GamePlayerModel>;

  /**
   * Total amount in the pot.
   */
  pot?: number;

  /**
   * Collection of side-pots.
   */
  pots?: Array<PotModel>;

  /**
   * Current round.
   */
  round?: number;
  sharedCards?: Array<CardModel>;
  summary?: HandSummaryModel;
  winners?: Array<WinnerModel>;
}
