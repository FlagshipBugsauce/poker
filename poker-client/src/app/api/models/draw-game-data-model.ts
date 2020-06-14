/* tslint:disable */
import { DrawGameDrawModel } from './draw-game-draw-model';
import { GamePlayerModel } from './game-player-model';

/**
 * Model representing player data in a game, i.e. cards drawn, etc...
 */
export interface DrawGameDataModel {

  /**
   * Flag that is true if this draw is next.
   */
  acting?: boolean;
  draws?: Array<DrawGameDrawModel>;
  player?: GamePlayerModel;
}
