/* tslint:disable */
import { GamePlayerModel } from './game-player-model';
import { HandActionModel } from './hand-action-model';
export interface HandDocument {
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
  playerToAct?: GamePlayerModel;
}
