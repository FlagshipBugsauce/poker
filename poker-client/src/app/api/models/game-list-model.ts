/* tslint:disable */
import { GameParameterModel } from './game-parameter-model';
import { LobbyPlayerModel } from './lobby-player-model';
export interface GameListModel {

  /**
   * The current number of players in the game
   */
  currentPlayers?: number;
  host?: LobbyPlayerModel;

  /**
   * The ID of the game.
   */
  id?: string;
  parameters?: GameParameterModel;
}
