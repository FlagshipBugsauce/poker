import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {PokerTableComponent} from './poker-table.component';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {DeckComponent} from './deck/deck.component';
import {PlayerBoxComponent} from './player-box/player-box.component';
import {HandSummaryComponent} from './hand-summary/hand-summary.component';
import {SharedModule} from '../../shared/shared.module';
import {PopupAfkComponent} from '../popup-afk/popup-afk.component';
import {TableControlsComponent} from './table-controls/table-controls.component';
import {CommunityCardsComponent} from './community-cards/community-cards.component';
import {MemoizedSelector} from '@ngrx/store';
import {
  AppStateContainer,
  GameStateContainer,
  MiscEventsStateContainer,
  PlayerDataStateContainer,
  PokerTableStateContainer,
  PrivatePlayerDataStateContainer
} from '../../shared/models/app-state.model';
import {ClientUser} from '../../api/models/client-user';
import {Card, Deal, GamePlayer, PokerTable, Timer, Winner} from '../../api/models';
import {
  selectAwayStatus,
  selectCommunityCards,
  selectDeal,
  selectDealer,
  selectDisplayHandSummary,
  selectGamePhase,
  selectHandWinners,
  selectHiddenCards,
  selectLoggedInUser,
  selectPlayers,
  selectPokerTable,
  selectPrivateCards,
  selectTimer
} from '../../state/app.selector';
import {mockClientUser, mockDeal, mockNoHiddenCards, mockTimer} from '../../testing/mock-models';
import {samplePlayer, table2} from '../../testing/sample-table';
import {By} from '@angular/platform-browser';

describe('PokerTableComponent', () => {
  let component: PokerTableComponent;
  let fixture: ComponentFixture<PokerTableComponent>;
  let mockStore: MockStore;
  let mockUserSelector: MemoizedSelector<AppStateContainer, ClientUser>;
  let mockPlayersSelector: MemoizedSelector<PokerTableStateContainer, GamePlayer[]>;
  let mockTableSelector: MemoizedSelector<PokerTableStateContainer, PokerTable>;
  let mockAwaySelector: MemoizedSelector<PlayerDataStateContainer, boolean>;
  let mockTimerSelector: MemoizedSelector<MiscEventsStateContainer, Timer>;
  let mockGamePhaseSelector: MemoizedSelector<GameStateContainer, string>;
  let mockDealerSelector: MemoizedSelector<PokerTableStateContainer, number>;
  let mockDealSelector: MemoizedSelector<GameStateContainer, Deal>;
  let mockSharedCardsSelector: MemoizedSelector<PokerTableStateContainer, Card[]>;
  let mockWinnersSelector: MemoizedSelector<PokerTableStateContainer, Winner[]>;
  let mockDisplaySummary: MemoizedSelector<PokerTableStateContainer, boolean>;
  let mockPrivateCardsSelector: MemoizedSelector<PrivatePlayerDataStateContainer, Card[]>;
  let mockHiddenCardsSelector: MemoizedSelector<MiscEventsStateContainer, boolean[][]>;

  jest.setTimeout(10000);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        PokerTableComponent,
        DeckComponent,
        PlayerBoxComponent,
        HandSummaryComponent,
        PopupAfkComponent,
        TableControlsComponent,
        CommunityCardsComponent
      ],
      providers: [
        provideMockStore()
      ],
      imports: [
        SharedModule
      ]
    }).compileComponents();
    mockStore = TestBed.inject(MockStore);
    // Mock selectors here:
    mockUserSelector = mockStore.overrideSelector(selectLoggedInUser, mockClientUser);
    mockPlayersSelector = mockStore.overrideSelector(selectPlayers, table2.players);
    mockTableSelector = mockStore.overrideSelector(selectPokerTable, table2);
    mockAwaySelector = mockStore.overrideSelector(selectAwayStatus, false);
    mockTimerSelector = mockStore.overrideSelector(selectTimer, mockTimer);
    mockGamePhaseSelector = mockStore.overrideSelector(selectGamePhase, 'Play');
    mockDealerSelector = mockStore.overrideSelector(selectDealer, 2);
    mockDealSelector = mockStore.overrideSelector(selectDeal, mockDeal);
    mockSharedCardsSelector = mockStore.overrideSelector(selectCommunityCards, table2.sharedCards);
    mockWinnersSelector = mockStore.overrideSelector(selectHandWinners, table2.winners);
    mockDisplaySummary = mockStore.overrideSelector(selectDisplayHandSummary, true);
    mockPrivateCardsSelector = mockStore.overrideSelector(selectPrivateCards, samplePlayer.cards);
    mockHiddenCardsSelector = mockStore.overrideSelector(selectHiddenCards, mockNoHiddenCards);

    fixture = TestBed.createComponent(PokerTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have correct number of player boxes', () => {
    const playerBoxes = fixture.debugElement.queryAll(By.css('#playerBox'));
    expect(playerBoxes).toBeTruthy();
    expect(playerBoxes.length).toBe(4);
    expect(component.playerBoxes.length).toBe(4);
  });

  it('should assign correct position to player boxes', () => {
    const playerBoxes = fixture.debugElement.queryAll(By.css('#playerBox'))
      .map(b => b.componentInstance)
      .forEach((b, i) => expect(b.player).toBe(i));
  });

  it('should change width when window:resize event occurs', () => {
    // const resizeSpy = spyOn(component, 'onResize');
    const updatePositionsSpy = spyOn(component, 'updatePositions');
    window = Object.assign(window, {innerWidth: 2000});
    window.dispatchEvent(new Event('resize'));
    const scrollbar: boolean = document.body.scrollHeight > document.body.clientHeight;
    const margin: number = scrollbar ? 48 : 30;
    expect(updatePositionsSpy).toHaveBeenCalled();
    expect(component.width).toBe(2000 - margin);
  });

  it('should not change width below minWidth', () => {
    window = Object.assign(window, {innerWidth: component.minWidth});
    window.dispatchEvent(new Event('resize'));
    const scrollbar: boolean = document.body.scrollHeight > document.body.clientHeight;
    const margin: number = scrollbar ? 48 : 30;
    expect(component.width).toBe(component.minWidth - margin);
    window = Object.assign(window, {innerWidth: component.minWidth - 100});
    window.dispatchEvent(new Event('resize'));
    expect(component.width).toBe(component.minWidth - margin);
  });

  const validateTimer = (start, end) => {
    const timer = fixture.debugElement.query(By.css('#turnTimer'));
    mockTimerSelector.setResult({id: 'test-id', duration: start});
    mockStore.refreshState();
    fixture.detectChanges();
    for (let i = start; i > end; i--) {
      expect(timer.nativeElement.textContent).toBe(`${i}`);
      tick(1000);
      fixture.detectChanges();
    }
    expect(timer.nativeElement.textContent).toBe(`${end}`);
  };

  it('should have correct timer value', fakeAsync(() => {
    validateTimer(3, 0);
  }));

  it('should restart timer when another timer action is dispatched', fakeAsync(() => {
    validateTimer(4, 3);
    validateTimer(5, 0);
  }));
});
