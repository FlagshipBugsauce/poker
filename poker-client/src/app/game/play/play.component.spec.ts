import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {PlayComponent} from './play.component';
import {RouterTestingModule} from '@angular/router/testing';
import {SharedModule} from '../../shared/shared.module';
import {DrawGameDataModel} from '../../api/models/draw-game-data-model';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {MemoizedSelector} from '@ngrx/store';
import {
  AppStateContainer,
  DrawnCardsContainer,
  GameDataStateContainer,
  GameStateContainer,
  HandStateContainer,
  PlayerDataStateContainer,
  PokerTableStateContainer
} from '../../shared/models/app-state.model';
import * as selectors from '../../state/app.selector';
import {
  CardModel,
  GameModel,
  GamePlayerModel,
  HandModel,
  HandSummaryModel,
  UserModel
} from '../../api/models';
import {
  mockGameData,
  mockGameModel,
  mockHandDocument,
  mockHandSummaryModel,
  mockPlayerModel,
  mockUser
} from '../../testing/mock-models';
import {PopupAfkComponent} from '../popup-afk/popup-afk.component';
import {WebSocketService} from '../../shared/web-socket/web-socket.service';
import {MockWebSocketService} from '../../testing/mock-services';
import {PokerTableComponent} from '../poker-table/poker-table.component';
import {PlayerBoxComponent} from '../poker-table/player-box/player-box.component';
import {HandSummaryComponent} from '../poker-table/hand-summary/hand-summary.component';
import {DeckComponent} from '../poker-table/deck/deck.component';
import {GamePhase} from '../../shared/models/game-phase.enum';

describe('PlayComponent', () => {
  let mockStore: MockStore;
  let mockHandSelector: MemoizedSelector<HandStateContainer, HandModel>;
  let mockUserSelector: MemoizedSelector<AppStateContainer, UserModel>;
  let mockGameDataSelector: MemoizedSelector<GameDataStateContainer, DrawGameDataModel[]>;
  let mockGameSelector: MemoizedSelector<GameStateContainer, GameModel>;
  let mockAwayStatusSelector: MemoizedSelector<PlayerDataStateContainer, boolean>;
  let mockActingStatusSelector: MemoizedSelector<PlayerDataStateContainer, boolean>;
  let mockJwtSelector: MemoizedSelector<AppStateContainer, string>;
  let mockDrawnCardsSelector: MemoizedSelector<DrawnCardsContainer, CardModel[]>;
  let mockPlayersSelector: MemoizedSelector<PokerTableStateContainer, GamePlayerModel[]>;
  let mockActingPlayerSelector: MemoizedSelector<PokerTableStateContainer, number>;
  let mockDisplayHandSummarySelector: MemoizedSelector<PokerTableStateContainer, boolean>;
  let mockGamePhaseSelector: MemoizedSelector<GameStateContainer, string>;
  let mockPlayerThatActedSelector: MemoizedSelector<PokerTableStateContainer, number>;
  let mockHandSummarySelector: MemoizedSelector<PokerTableStateContainer, HandSummaryModel>;
  let component: PlayComponent;
  let fixture: ComponentFixture<PlayComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        PlayComponent,
        PopupAfkComponent,
        PokerTableComponent,
        PlayerBoxComponent,
        HandSummaryComponent,
        DeckComponent
      ],
      providers: [
        provideMockStore(),
        {
          provide: WebSocketService,
          useClass: MockWebSocketService
        }
      ],
      imports: [RouterTestingModule, SharedModule]
    }).compileComponents();

    mockStore = TestBed.inject(MockStore);
    mockHandSelector = mockStore.overrideSelector(selectors.selectHandModel, mockHandDocument);
    mockUserSelector = mockStore.overrideSelector(selectors.selectLoggedInUser, mockUser);
    mockGameDataSelector = mockStore.overrideSelector(selectors.selectGameData, mockGameData);
    mockGameSelector = mockStore.overrideSelector(selectors.selectGameModel, mockGameModel);
    mockAwayStatusSelector = mockStore.overrideSelector(selectors.selectAwayStatus, false);
    mockActingStatusSelector = mockStore.overrideSelector(selectors.selectActingStatus, false);
    mockJwtSelector = mockStore.overrideSelector(selectors.selectJwt, 'jwt');
    mockDrawnCardsSelector = mockStore.overrideSelector(selectors.selectDrawnCards, []);
    mockPlayersSelector = mockStore.overrideSelector(selectors.selectPlayers, [mockPlayerModel]);
    mockActingPlayerSelector = mockStore.overrideSelector(selectors.selectActingPlayer, 0);
    mockDisplayHandSummarySelector = mockStore.overrideSelector(selectors.selectDisplayHandSummary, false);
    mockGamePhaseSelector = mockStore.overrideSelector(selectors.selectGamePhase, GamePhase.Play);
    mockPlayerThatActedSelector = mockStore.overrideSelector(selectors.selectCardPosition, 0);
    mockHandSummarySelector = mockStore.overrideSelector(selectors.selectHandSummary, mockHandSummaryModel);
    fixture = TestBed.createComponent(PlayComponent);
    fixture.detectChanges();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
  // TODO: Fix
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
