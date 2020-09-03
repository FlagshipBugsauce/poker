import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {GameComponent} from './game.component';
import {SharedModule} from '../../shared/shared.module';
import {RouterTestingModule} from '@angular/router/testing';
import {LobbyComponent} from '../lobby/lobby.component';
import {EndComponent} from '../end/end.component';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {MemoizedSelector} from '@ngrx/store';
import {
  AppStateContainer,
  ChatStateContainer,
  GameDataStateContainer,
  GameStateContainer,
  MiscEventsStateContainer,
  PlayerDataStateContainer,
  PokerTableStateContainer,
  PrivatePlayerDataStateContainer
} from '../../shared/models/app-state.model';
import * as selectors from '../../state/app.selector';
import {selectHiddenCards, selectPrivateCards} from '../../state/app.selector';
import {DrawGameData} from '../../api/models/draw-game-data';
import {
  mockChatMessage,
  mockGameData,
  mockGameModel,
  mockHandSummaryModel,
  mockNoHiddenCards,
  mockUser
} from '../../testing/mock-models';
import {PopupAfkComponent} from '../popup-afk/popup-afk.component';
import {WebSocketService} from '../../shared/web-socket/web-socket.service';
import {
  Card,
  ChatMessage,
  ClientUser,
  Deal,
  Game,
  GamePlayer,
  HandSummary,
  Timer,
  Winner
} from '../../api/models';
import {MockChatService, MockWebSocketService} from '../../testing/mock-services';
import {ChatService} from '../../shared/web-socket/chat.service';
import {PokerTableComponent} from '../poker-table/poker-table.component';
import {PlayerBoxComponent} from '../poker-table/player-box/player-box.component';
import {HandSummaryComponent} from '../poker-table/hand-summary/hand-summary.component';
import {DeckComponent} from '../poker-table/deck/deck.component';
import {GamePhase} from '../../shared/models/game-phase.enum';
import {TableControlsComponent} from '../poker-table/table-controls/table-controls.component';
import {CommunityCardsComponent} from '../poker-table/community-cards/community-cards.component';
import {samplePlayer, table2} from '../../testing/sample-table';

describe('GameComponent', () => {
  let mockStore: MockStore;
  let mockGameDataSelector: MemoizedSelector<GameDataStateContainer, DrawGameData[]>;
  let mockGameStateSelector: MemoizedSelector<GameStateContainer, string>;
  let mockLoggedInUserSelector: MemoizedSelector<AppStateContainer, ClientUser>;
  let mockJwtSelector: MemoizedSelector<AppStateContainer, string>;
  let mockGeneralChatSelector: MemoizedSelector<ChatStateContainer, ChatMessage>;
  let mockAuthenticatedSelector: MemoizedSelector<AppStateContainer, boolean>;
  let mockGameSelector: MemoizedSelector<GameStateContainer, Game>;
  let mockPlayersSelector: MemoizedSelector<PokerTableStateContainer, GamePlayer[]>;
  let mockActingPlayerSelector: MemoizedSelector<PokerTableStateContainer, number>;
  let mockDisplayHandSummarySelector: MemoizedSelector<PokerTableStateContainer, boolean>;
  let mockGamePhaseSelector: MemoizedSelector<GameStateContainer, string>;
  let mockPlayerThatActedSelector: MemoizedSelector<PokerTableStateContainer, number>;
  let mockHandSummarySelector: MemoizedSelector<PokerTableStateContainer, HandSummary>;
  let mockAwayStatusSelector: MemoizedSelector<PlayerDataStateContainer, boolean>;
  let mockStartTimerSelector: MemoizedSelector<MiscEventsStateContainer, Timer>;
  let mockDealSelector: MemoizedSelector<MiscEventsStateContainer, Deal>;
  let mockWinnersSelector: MemoizedSelector<PokerTableStateContainer, Winner[]>;
  let mockDealerSelector: MemoizedSelector<PokerTableStateContainer, number>;
  let mockSharedCardsSelector: MemoizedSelector<PokerTableStateContainer, Card[]>;
  let mockPrivateCardsSelector: MemoizedSelector<PrivatePlayerDataStateContainer, Card[]>;
  let mockHiddenCardsSelector: MemoizedSelector<MiscEventsStateContainer, boolean[][]>;
  let component: GameComponent;
  let fixture: ComponentFixture<GameComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        GameComponent,
        LobbyComponent,
        EndComponent,
        PopupAfkComponent,
        PokerTableComponent,
        PlayerBoxComponent,
        HandSummaryComponent,
        DeckComponent,
        TableControlsComponent,
        CommunityCardsComponent
      ],
      imports: [SharedModule, RouterTestingModule],
      providers: [
        provideMockStore(),
        {
          provide: WebSocketService,
          useClass: MockWebSocketService
        },
        {
          provide: ChatService,
          useClass: MockChatService
        }
      ]
    }).compileComponents();
    mockStore = TestBed.inject(MockStore);
    mockGameDataSelector = mockStore.overrideSelector(selectors.selectGameData, mockGameData);
    mockGameStateSelector = mockStore.overrideSelector(selectors.selectGamePhase, 'Lobby');
    mockLoggedInUserSelector = mockStore.overrideSelector(selectors.selectLoggedInUser, mockUser);
    mockJwtSelector = mockStore.overrideSelector(selectors.selectJwt, 'jwt');
    mockGeneralChatSelector = mockStore.overrideSelector(selectors.selectGeneralChat, mockChatMessage);
    mockAuthenticatedSelector = mockStore.overrideSelector(selectors.selectAuthenticated, true);
    mockGameSelector = mockStore.overrideSelector(selectors.selectGameModel, mockGameModel);
    mockPlayersSelector = mockStore.overrideSelector(selectors.selectPlayers, table2.players);
    mockActingPlayerSelector = mockStore.overrideSelector(selectors.selectActingPlayer, 0);
    mockDisplayHandSummarySelector = mockStore.overrideSelector(selectors.selectDisplayHandSummary, false);
    mockGamePhaseSelector = mockStore.overrideSelector(selectors.selectGamePhase, GamePhase.Play);
    mockPlayerThatActedSelector = mockStore.overrideSelector(selectors.selectPlayerThatActed, 0);
    mockHandSummarySelector = mockStore.overrideSelector(selectors.selectHandSummary, mockHandSummaryModel);
    mockAwayStatusSelector = mockStore.overrideSelector(selectors.selectAwayStatus, false);
    mockStartTimerSelector = mockStore.overrideSelector(selectors.selectTimer, {});
    mockDealSelector = mockStore.overrideSelector(selectors.selectDeal, {});
    mockWinnersSelector = mockStore.overrideSelector(selectors.selectHandWinners, []);
    mockDealerSelector = mockStore.overrideSelector(selectors.selectDealer, 2);
    mockSharedCardsSelector = mockStore.overrideSelector(selectors.selectCommunityCards, []);
    mockPrivateCardsSelector = mockStore.overrideSelector(selectPrivateCards, samplePlayer.cards);
    mockHiddenCardsSelector = mockStore.overrideSelector(selectHiddenCards, mockNoHiddenCards);
    fixture = TestBed.createComponent(GameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
