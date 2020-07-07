/* tslint:disable */
import { CardModel } from './card-model';
import { GamePlayerModel } from './game-player-model';
export interface HandActionModel {
  drawnCard?: CardModel;

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
   * Type of action performed.
   */
  type?: 'Start' | 'Roll' | 'Draw';

  /**
   * Value that was rolled.
   */
  value?: number;
}
