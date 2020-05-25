/* tslint:disable */
import {GamePlayerModel} from './game-player-model';
import {RollActionModel} from './roll-action-model';

export interface HandDocument {
  actions?: Array<RollActionModel>;

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
  playerToAct?: GamePlayerModel;
}
