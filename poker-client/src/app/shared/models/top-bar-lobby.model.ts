import {LobbyPlayerModel} from '../../api/models/lobby-player-model';

export interface TopBarLobbyModel {
  id: string;
  name: string;
  host: LobbyPlayerModel;
}
