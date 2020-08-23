/* tslint:disable */
import {DrawGameDraw} from './draw-game-draw';
import {GamePlayer} from './game-player';

/**
 * Model representing player data in a game, i.e. cards drawn, etc...
 */
export interface DrawGameData {

  /**
   * Flag that is true if this draw is next.
   */
  acting?: boolean;
  draws?: Array<DrawGameDraw>;
  player?: GamePlayer;
}
