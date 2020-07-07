import {HandDocument} from '../api/models/hand-document';
import {CardSuit, CardValue} from '../shared/models/card.enum';
import {UserModel} from '../api/models/user-model';
import {DrawGameDataModel} from '../api/models/draw-game-data-model';
import {GameModel} from '../api/models/game-model';
import {LobbyModel} from '../api/models/lobby-model';

export const mockHandDocument: HandDocument = {
  drawnCards: [{suit: CardSuit.Spades, value: CardValue.Ace}]
};

export const mockUser: UserModel = {
  id: 'abc123'
};

export const mockGameData: DrawGameDataModel[] = [];

export const mockGameModel: GameModel = {
  phase: 'Play'
};

export const mockLobbyModel: LobbyModel = {
  host: {id: 'jim', firstName: 'jim', lastName: 'bob'},
  players: [{id: 'jim', firstName: 'jim', lastName: 'bob'}]
};
