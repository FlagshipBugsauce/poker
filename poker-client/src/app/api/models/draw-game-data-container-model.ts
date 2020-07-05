/* tslint:disable */
import {DrawGameDataModel} from './draw-game-data-model';

/**
 * Container for a list of game data, plus some other useful information.
 */
export interface DrawGameDataContainerModel {

  /**
   * The current hand.
   */
  currentHand?: number;
  gameData?: Array<DrawGameDataModel>;
}
