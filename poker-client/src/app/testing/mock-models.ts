import {HandDocument} from '../api/models/hand-document';
import {CardSuit, CardValue} from '../shared/models/card.enum';
import {UserModel} from '../api/models/user-model';
import {DrawGameDataModel} from '../api/models/draw-game-data-model';
import {GameDocument} from '../api/models/game-document';
import {LobbyDocument} from '../api/models/lobby-document';

export const mockHandDocument: HandDocument = {
  drawnCards: [{suit: CardSuit.Spades, value: CardValue.Ace}]
};

export const mockUser: UserModel = {
  id: 'abc123'
};

export const mockGameData: DrawGameDataModel[] = [];

export const mockGameDocument: GameDocument = {
  state: 'Play',
  summary: {message: 'end game summary'}
};

export const mockLobbyDocument: LobbyDocument = {
  host: 'jim',
  players: [{id: 'jim', firstName: 'jim', lastName: 'bob'}]
};
