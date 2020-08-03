/* tslint:disable */

/**
 * Model containing fields needed by the UI's game controls component.
 */
export interface TableControlsModel {

  /**
   * Size of the players bank roll.
   */
  bankRoll?: number;

  /**
   * Amount invested into current round of betting.
   */
  currentBet?: number;

  /**
   * Amount required for the player to call.
   */
  toCall?: number;
}
