/* tslint:disable */

/**
 * Deal model that is sent to the client to trigger a deal event.
 */
export interface Deal {

  /**
   * ID of the deal event.
   */
  id?: string;

  /**
   * Number of cards to deal.
   */
  numCards?: number;
}
