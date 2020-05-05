/* tslint:disable */
export interface GameActionModel {

  /**
   * The action that occurred.
   */
  gameAction?: 'Fold' | 'Check' | 'Raise' | 'Call' | 'Bet' | 'ReRaise';

  /**
   * The ID of the client.
   */
  userID?: string;
}
