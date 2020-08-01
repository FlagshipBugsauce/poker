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
  PlayerDataStateContainer,
  PokerTableStateContainer,
  TimerStateContainer
} from '../../shared/models/app-state.model';
import * as selectors from '../../state/app.selector';
import {DrawGameDataModel} from '../../api/models/draw-game-data-model';
import {
  mockChatMessage,
  mockGameData,
  mockGameModel,
  mockHandSummaryModel,
  mockPlayerModel,
  mockUser
} from '../../testing/mock-models';
import {PopupAfkComponent} from '../popup-afk/popup-afk.component';
import {WebSocketService} from '../../shared/web-socket/web-socket.service';
import {
  ChatMessageModel,
  ClientUserModel,
  GameModel,
  GamePlayerModel,
  HandSummaryModel,
  TimerModel
} from '../../api/models';
import {MockChatService, MockWebSocketService} from '../../testing/mock-services';
import {ChatService} from '../../shared/web-socket/chat.service';
import {PokerTableComponent} from '../poker-table/poker-table.component';
import {PlayerBoxComponent} from '../poker-table/player-box/player-box.component';
import {HandSummaryComponent} from '../poker-table/hand-summary/hand-summary.component';
import {DeckComponent} from '../poker-table/deck/deck.component';
import {GamePhase} from '../../shared/models/game-phase.enum';

describe('GameComponent', () => {
  let mockStore: MockStore;
  let mockGameDataSelector: MemoizedSelector<GameDataStateContainer, DrawGameDataModel[]>;
  let mockGameStateSelector: MemoizedSelector<GameStateContainer, string>;
  let mockLoggedInUserSelector: MemoizedSelector<AppStateContainer, ClientUserModel>;
  let mockJwtSelector: MemoizedSelector<AppStateContainer, string>;
  let mockGeneralChatSelector: MemoizedSelector<ChatStateContainer, ChatMessageModel>;
  let mockAuthenticatedSelector: MemoizedSelector<AppStateContainer, boolean>;
  let mockGameSelector: MemoizedSelector<GameStateContainer, GameModel>;
  let mockPlayersSelector: MemoizedSelector<PokerTableStateContainer, GamePlayerModel[]>;
  let mockActingPlayerSelector: MemoizedSelector<PokerTableStateContainer, number>;
  let mockDisplayHandSummarySelector: MemoizedSelector<PokerTableStateContainer, boolean>;
  let mockGamePhaseSelector: MemoizedSelector<GameStateContainer, string>;
  let mockPlayerThatActedSelector: MemoizedSelector<PokerTableStateContainer, number>;
  let mockHandSummarySelector: MemoizedSelector<PokerTableStateContainer, HandSummaryModel>;
  let mockAwayStatusSelector: MemoizedSelector<PlayerDataStateContainer, boolean>;
  let mockSelectStartTimer: MemoizedSelector<TimerStateContainer, TimerModel>;
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
        DeckComponent
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
    mockPlayersSelector = mockStore.overrideSelector(selectors.selectPlayers, [mockPlayerModel]);
    mockActingPlayerSelector = mockStore.overrideSelector(selectors.selectActingPlayer, 0);
    mockDisplayHandSummarySelector = mockStore.overrideSelector(selectors.selectDisplayHandSummary, false);
    mockGamePhaseSelector = mockStore.overrideSelector(selectors.selectGamePhase, GamePhase.Play);
    mockPlayerThatActedSelector = mockStore.overrideSelector(selectors.selectPlayerThatActed, 0);
    mockHandSummarySelector = mockStore.overrideSelector(selectors.selectHandSummary, mockHandSummaryModel);
    mockAwayStatusSelector = mockStore.overrideSelector(selectors.selectAwayStatus, false);
    mockSelectStartTimer = mockStore.overrideSelector(selectors.selectTimer, {});
    fixture = TestBed.createComponent(GameComponent);
    fixture.detectChanges();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
