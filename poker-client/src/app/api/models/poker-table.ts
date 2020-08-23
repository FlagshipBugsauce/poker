/* tslint:disable */
import {Card} from './card';
import {GamePlayer} from './game-player';
import {HandSummary} from './hand-summary';
import {Pot} from './pot';
import {Winner} from './winner';

export interface PokerTable {

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
  players?: Array<GamePlayer>;

  /**
   * Total amount in the pot.
   */
  pot?: number;
  pots?: Array<Pot>;

  /**
   * Current round.
   */
  round?: number;
  sharedCards?: Array<Card>;
  summary?: HandSummary;
  winners?: Array<Winner>;
}
