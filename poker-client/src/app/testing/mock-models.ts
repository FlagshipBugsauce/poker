import {UserModel} from '../api/models/user-model';
import {DrawGameDataModel} from '../api/models/draw-game-data-model';
import {GameModel} from '../api/models/game-model';
import {LobbyModel} from '../api/models/lobby-model';
import {HandModel} from '../api/models/hand-model';

export const mockHandDocument: HandModel = {};

export const mockUser: UserModel = {
  id: 'abc123'
};

export const mockGameData: DrawGameDataModel[] = [];

export const mockGameModel: GameModel = {
  phase: 'Play'
};

export const mockLobbyModel: LobbyModel = {
  host: {id: 'jim', firstName: 'jim', lastName: 'bob'},
  players: [{id: 'jim', firstName: 'jim', lastName: 'bob'}],
  parameters: {name: 'some name', maxPlayers: 10, buyIn: 4240}
};
