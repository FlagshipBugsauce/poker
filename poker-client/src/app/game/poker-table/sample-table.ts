import {PokerTableModel} from '../../api/models/poker-table-model';

export const table: PokerTableModel = {
  players: [
    {
      id: '181acdbf-bb98-4ae1-895c-187d79f793b6',
      firstName: 'Jimmy',
      lastName: 'McGee',
      score: 0,
      away: true,
      acting: false,
      bankRoll: null,
      cards: [{suit: 'Clubs', value: 'Queen'}]
    }, {
      id: 'fddf6fbc-4176-4050-bd21-7b05f87b3f29',
      firstName: 'Randall',
      lastName: 'Maloy',
      score: 0,
      away: true,
      acting: false,
      bankRoll: null,
      cards: [{suit: 'Hearts', value: 'Five'}]
    }, {
      id: 'abbff0ee-c671-4e2b-84b9-7ae16de3088e',
      firstName: 'Bugsy',
      lastName: 'McGillicutty',
      score: 0,
      away: true,
      acting: false,
      bankRoll: null,
      cards: [{suit: 'Diamonds', value: 'Nine'}]
    }, {
      id: '15c39a26-6abd-4690-a1e2-f96094a4bf44',
      firstName: 'Bucephalus',
      lastName: 'Johnson',
      score: 0,
      away: true,
      acting: false,
      bankRoll: null,
      cards: [{suit: 'Hearts', value: 'Jack'}]
    }, {
      id: '24800f75-0353-4c83-b13c-af388bd9a9ac',
      firstName: 'Jackson',
      lastName: 'McGee',
      score: 0,
      away: false,
      acting: false,
      bankRoll: null,
      cards: []
    }, {
      id: '1c20765d-4245-4943-a55b-78ce098349ad',
      firstName: 'Edith',
      lastName: 'Winslow',
      score: 0,
      away: false,
      acting: false,
      bankRoll: null,
      cards: []
    }, {
      id: '3eb401ed-f9c4-4dfe-9920-3dffdc52d9ba',
      firstName: 'Testy',
      lastName: 'McGoo',
      score: 0,
      away: false,
      acting: false,
      bankRoll: null,
      cards: []
    }, {
      id: '00000000-0000-0000-0000-000000000000',
      firstName: 'admin',
      lastName: 'admin',
      score: 0,
      away: false,
      acting: false,
      bankRoll: null,
      cards: []
    }, {
      id: '49c7cdb3-8139-4d23-aba1-e4492846d2a0',
      firstName: 'Cooter',
      lastName: 'McGraw',
      score: 0,
      away: false,
      acting: false,
      bankRoll: null,
      cards: []
    }, {
      id: 'f9ee2f0a-fc87-4a57-b436-aeff792a5dd6',
      firstName: 'Testy',
      lastName: 'McGee',
      score: 0,
      away: false,
      acting: false,
      bankRoll: null,
      cards: []
    }
  ],
  actingPlayer: 1,
  dealer: 0,
  displayHandSummary: false,
  summary: null,
  eventTracker: 0
};
