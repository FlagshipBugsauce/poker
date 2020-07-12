/* tslint:disable */
import {GameParameterModel} from './game-parameter-model';
import {LobbyPlayerModel} from './lobby-player-model';

/**
 * Game lobby containing information such as game parameters, players, etc...
 */
export interface LobbyModel {
  host?: LobbyPlayerModel;

  /**
   * Lobby's ID.
   */
  id?: string;
  parameters?: GameParameterModel;
  players?: Array<LobbyPlayerModel>;
}
