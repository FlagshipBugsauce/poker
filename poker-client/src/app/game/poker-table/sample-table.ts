import {PokerTableModel} from '../../api/models/poker-table-model';

export const table: PokerTableModel = {
  players: [
    {
      id: '181acdbf-bb98-4ae1-895c-187d79f793b6',
      firstName: 'Jimmy',
      lastName: 'McGee',
      away: true,
      out: true,
      cards: [{suit: 'Clubs', value: 'Queen'}],
      controls: {
        bankRoll: 420,
        currentBet: 80,
        toCall: 40
      }
    }, {
      id: 'fddf6fbc-4176-4050-bd21-7b05f87b3f29',
      firstName: 'Randall',
      lastName: 'Maloy',
      away: true,
      out: true,
      cards: [{suit: 'Hearts', value: 'Five'}],
      controls: {
        bankRoll: 420,
        currentBet: 80,
        toCall: 40
      }
    }, {
      id: 'abbff0ee-c671-4e2b-84b9-7ae16de3088e',
      firstName: 'Bugsy',
      lastName: 'McGillicutty',
      away: true,
      out: false,
      cards: [{suit: 'Diamonds', value: 'Nine'}],
      controls: {
        bankRoll: 420,
        currentBet: 80,
        toCall: 40
      }
    }, {
      id: '15c39a26-6abd-4690-a1e2-f96094a4bf44',
      firstName: 'Bucephalus',
      lastName: 'Johnson',
      away: true,
      out: false,
      cards: [{suit: 'Hearts', value: 'Jack'}],
      controls: {
        bankRoll: 420,
        currentBet: 80,
        toCall: 40
      }
    }, {
      id: '24800f75-0353-4c83-b13c-af388bd9a9ac',
      firstName: 'Jackson',
      lastName: 'McGee',
      away: false,
      out: false,
      cards: [],
      controls: {
        bankRoll: 42069,
        currentBet: 20,
        toCall: 400
      }
    }, {
      id: '1c20765d-4245-4943-a55b-78ce098349ad',
      firstName: 'Edith',
      lastName: 'Winslow',
      away: false,
      out: false,
      cards: [],
      controls: {
        bankRoll: 420,
        currentBet: 80,
        toCall: 40
      }
    }, {
      id: '3eb401ed-f9c4-4dfe-9920-3dffdc52d9ba',
      firstName: 'Testy',
      lastName: 'McGoo',
      away: false,
      out: true,
      cards: [],
      controls: {
        bankRoll: 420,
        currentBet: 80,
        toCall: 40
      }
    }, {
      id: '00000000-0000-0000-0000-000000000000',
      firstName: 'admin',
      lastName: 'admin',
      away: false,
      out: false,
      cards: [],
      controls: {
        bankRoll: 420,
        currentBet: 80,
        toCall: 40
      }
    }, {
      id: '49c7cdb3-8139-4d23-aba1-e4492846d2a0',
      firstName: 'Cooter',
      lastName: 'McGraw',
      away: false,
      out: false,
      cards: [],
      controls: {
        bankRoll: 420,
        currentBet: 80,
        toCall: 40
      }
    }, {
      id: 'f9ee2f0a-fc87-4a57-b436-aeff792a5dd6',
      firstName: 'Testy',
      lastName: 'McGee',
      away: false,
      out: false,
      cards: [],
      controls: {
        bankRoll: 420,
        currentBet: 80,
        toCall: 40
      }
    }
  ],
  actingPlayer: 1,
  dealer: 0,
  displayHandSummary: false,
  summary: null,
  eventTracker: 0,
  minRaise: 420,
  pot: 420
};
