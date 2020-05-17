/* tslint:disable */
import { HandActionModel } from './hand-action-model';
export interface HandModel {
  actions?: Array<HandActionModel>;

  /**
   * Game ID.
   */
  gameId?: string;

  /**
   * Hand ID.
   */
  id?: string;

  /**
   * Temporary message.
   */
  message?: string;

  /**
   * ID of the player whose turn it is.
   */
  playerToAct?: string;
}
