/* tslint:disable */
import { GamePlayerModel } from './game-player-model';
export interface RollActionModel {

  /**
   * Hand Action ID.
   */
  id?: string;

  /**
   * Message related to action which was performed.
   */
  message?: string;
  player?: GamePlayerModel;

  /**
   * Value that was rolled.
   */
  value?: number;
}
