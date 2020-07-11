/* tslint:disable */
import { CardModel } from './card-model';
import { GamePlayerModel } from './game-player-model';
export interface HandActionModel {
  drawnCard?: CardModel;
  player?: GamePlayerModel;

  /**
   * Type of action performed.
   */
  type?: 'Start' | 'Roll' | 'Draw';
}
