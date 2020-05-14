/* tslint:disable */
import { PlayerModel } from './player-model';
export interface HandActionModel {

  /**
   * Hand Action ID.
   */
  id?: string;

  /**
   * Message related to action which was performed.
   */
  message?: string;
  player?: PlayerModel;

  /**
   * Type of action.
   */
  type?: 'Roll';
}
