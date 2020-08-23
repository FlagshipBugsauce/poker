import {LobbyPlayer} from '../../api/models/lobby-player';

export interface TopBarLobbyModel {
  id: string;
  name: string;
  host: LobbyPlayer;
}
