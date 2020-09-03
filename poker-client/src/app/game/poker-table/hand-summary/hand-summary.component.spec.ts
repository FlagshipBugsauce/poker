import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {HandSummaryComponent} from './hand-summary.component';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {SharedModule} from '../../../shared/shared.module';
import {MemoizedSelector} from '@ngrx/store';
import {PokerTableStateContainer} from '../../../shared/models/app-state.model';
import {GamePlayer, Winner} from '../../../api/models';
import {
  selectDisplayHandSummary,
  selectHandWinners,
  selectPlayers
} from '../../../state/app.selector';
import {table2} from '../../../testing/sample-table';
import {By} from '@angular/platform-browser';

describe('HandSummaryComponent', () => {
  let component: HandSummaryComponent;
  let fixture: ComponentFixture<HandSummaryComponent>;
  let mockStore: MockStore;
  let mockDisplayHandSummarySelector: MemoizedSelector<PokerTableStateContainer, boolean>;
  let mockHandWinnersSelector: MemoizedSelector<PokerTableStateContainer, Winner[]>;
  let mockPlayersSelector: MemoizedSelector<PokerTableStateContainer, GamePlayer[]>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [HandSummaryComponent],
      providers: [provideMockStore()],
      imports: [SharedModule]
    }).compileComponents();
    mockStore = TestBed.inject(MockStore);
    // Mock selectors here:
    mockDisplayHandSummarySelector = mockStore.overrideSelector(selectDisplayHandSummary, true);
    mockHandWinnersSelector = mockStore.overrideSelector(selectHandWinners, table2.winners);
    mockPlayersSelector = mockStore.overrideSelector(selectPlayers, table2.players);

    fixture = TestBed.createComponent(HandSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display hand summary', () => {
    expect(fixture.debugElement.query(By.css('#handSummaryContainer'))).toBeTruthy();
  });

  it('should have 2 winners', () => {
    const winners = fixture.debugElement.queryAll(By.css('#winner'));
    expect(winners).toBeTruthy();
    expect(winners.length).toBe(2);
  });

  it('should have 10 "winning cards"', () => {
    const winningCards = fixture.debugElement.queryAll(By.css('#winningCard'));
    expect(winningCards).toBeTruthy();
    expect(winningCards.length).toBe(10);
  });

  it('should display correct winning message', () => {
    const winningText = fixture.debugElement.queryAll(By.css('#winningText'));
    expect(winningText).toBeTruthy();
    expect(winningText[0].nativeElement.textContent).toBe(component.getWinnerMessage(table2.winners[0]));
    expect(winningText[1].nativeElement.textContent).toBe(component.getWinnerMessage(table2.winners[1]));
  });

  it('should display the correct cards', () => {
    const winningCards = fixture.debugElement.queryAll(By.css('#winningCard'));
    expect(winningCards).toBeTruthy();
    const cards = table2.winners[0].cards.map(c => ({...c}))
    .concat(table2.winners[1].cards.map(c => ({...c})));
    for (let i = 0; i < 10; i++) {
      expect(winningCards[i].componentInstance.card).toEqual(cards[i]);
    }
  });

  it('should not display summary when displayHandSummary is false', () => {
    mockDisplayHandSummarySelector.setResult(false);
    mockStore.refreshState();
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('#handSummaryContainer'))).toBeFalsy();
  });
});
