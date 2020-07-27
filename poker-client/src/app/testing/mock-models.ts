import {UserModel} from '../api/models/user-model';
import {DrawGameDataModel} from '../api/models/draw-game-data-model';
import {GameModel} from '../api/models/game-model';
import {LobbyModel} from '../api/models/lobby-model';
import {HandModel} from '../api/models/hand-model';
import {ChatMessageModel} from '../api/models/chat-message-model';
import {PokerTableModel} from '../api/models/poker-table-model';
import {HandSummaryModel} from '../api/models/hand-summary-model';
import {GamePlayerModel} from '../api/models';
import {CardSuit, CardValue} from '../shared/models/card.enum';

export const mockHandDocument: HandModel = {};

export const mockUser: UserModel = {
  id: 'abc123'
};

export const mockGameData: DrawGameDataModel[] = [];

export const mockGameModel: GameModel = {
  phase: 'Play',
  players: []
};

export const mockLobbyModel: LobbyModel = {
  host: {id: 'jim', firstName: 'jim', lastName: 'bob'},
  players: [{id: 'jim', firstName: 'jim', lastName: 'bob'}],
  parameters: {name: 'some name', maxPlayers: 10, buyIn: 4240}
};

export const mockChatMessage: ChatMessageModel = {
  timestamp: null,
  author: null,
  message: null
};

export const mockPokerTable: PokerTableModel = {};

export const mockHandSummaryModel: HandSummaryModel = {
  winner: 0,
  card: {suit: CardSuit.Hearts, value: CardValue.Ace}
};

export const mockPlayerModel: GamePlayerModel = {id: 'jim', firstName: 'jim', lastName: 'bob'};
