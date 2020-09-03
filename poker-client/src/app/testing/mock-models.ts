import {ClientUser} from '../api/models/client-user';
import {DrawGameData} from '../api/models/draw-game-data';
import {Game} from '../api/models/game';
import {Lobby} from '../api/models/lobby';
import {ChatMessage} from '../api/models/chat-message';
import {PokerTable} from '../api/models/poker-table';
import {HandSummary} from '../api/models/hand-summary';
import {CurrentGame, Deal, GamePlayer, Timer} from '../api/models';
import {CardSuit, CardValue} from '../shared/models/card.enum';

export const mockUser: ClientUser = {
  id: 'abc123'
};

export const mockClientUser: ClientUser = {
  id: '24800f75-0353-4c83-b13c-af388bd9a9ac',
  firstName: 'Jackson',
  lastName: 'McGee',
  email: 'jon@domain.com',
  group: 'Administrator'
};

export const mockGameData: DrawGameData[] = [];

export const mockGameModel: Game = {
  phase: 'Play',
  players: []
};

export const mockLobbyModel: Lobby = {
  host: {id: 'jim', firstName: 'jim', lastName: 'bob'},
  players: [{id: 'jim', firstName: 'jim', lastName: 'bob'}],
  parameters: {name: 'some name', maxPlayers: 10, buyIn: 4240}
};

export const mockChatMessage: ChatMessage = {
  timestamp: null,
  author: null,
  message: null
};

export const mockPokerTable: PokerTable = {};

export const mockHandSummaryModel: HandSummary = {
  winner: 0,
  card: {suit: CardSuit.Hearts, value: CardValue.Ace}
};

export const mockPlayerModel: GamePlayer = {id: 'jim', firstName: 'jim', lastName: 'bob'};

export const mockCurrentGame: CurrentGame = {id: 'mockId', inGame: false};

export const mockNoHiddenCards: boolean[][] = Array(10).fill(Array(2).fill(false));

export const mockAllHiddenCards: boolean[][] = Array(10).fill(Array(2).fill(true));

export const mockTimer: Timer = {id: 'test-id', duration: 10};

export const mockDeal: Deal = {id: 'test-id', numCards: 2};
