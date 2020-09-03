import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {PlayerBoxComponent} from './player-box.component';
import {SharedModule} from '../../../shared/shared.module';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {MemoizedSelector} from '@ngrx/store';
import {
  AppStateContainer,
  MiscEventsStateContainer,
  PokerTableStateContainer,
  PrivatePlayerDataStateContainer
} from '../../../shared/models/app-state.model';
import {Card, ClientUser, GamePlayer, PokerTable} from '../../../api/models';
import {
  selectHiddenCards,
  selectLoggedInUser,
  selectPlayers,
  selectPokerTable,
  selectPrivateCards
} from '../../../state/app.selector';
import {samplePlayer, table2} from '../../../testing/sample-table';
import {mockAllHiddenCards, mockClientUser, mockNoHiddenCards} from '../../../testing/mock-models';
import {By} from '@angular/platform-browser';
import {CardSuit, CardValue} from '../../../shared/models/card.enum';

describe('PlayerBoxComponent', () => {
  let component: PlayerBoxComponent;
  let fixture: ComponentFixture<PlayerBoxComponent>;
  let mockStore: MockStore;
  let mockPokerTableSelector: MemoizedSelector<PokerTableStateContainer, PokerTable>;
  let mockPlayersSelector: MemoizedSelector<PokerTableStateContainer, GamePlayer[]>;
  let mockLoggedInUserSelector: MemoizedSelector<AppStateContainer, ClientUser>;
  let mockPrivateCardsSelector: MemoizedSelector<PrivatePlayerDataStateContainer, Card[]>;
  let mockHiddenCardsSelector: MemoizedSelector<MiscEventsStateContainer, boolean[][]>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PlayerBoxComponent],
      providers: [
        provideMockStore()
      ],
      imports: [
        SharedModule
      ]
    }).compileComponents();
    mockStore = TestBed.inject(MockStore);
    // Mock selectors here:
    mockPokerTableSelector = mockStore.overrideSelector(selectPokerTable, table2);
    mockPlayersSelector = mockStore.overrideSelector(selectPlayers, table2.players);
    mockLoggedInUserSelector = mockStore.overrideSelector(selectLoggedInUser, mockClientUser);
    mockPrivateCardsSelector = mockStore.overrideSelector(selectPrivateCards, samplePlayer.cards);
    mockHiddenCardsSelector = mockStore.overrideSelector(selectHiddenCards, mockNoHiddenCards);

    fixture = TestBed.createComponent(PlayerBoxComponent);
    component = fixture.componentInstance;
    component.player = 0;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // TODO: This will need to be updated if/when players can have actual avatars.
  it('should have player initials in avatar box', () => {
    const avatarBox = fixture.debugElement.query(By.css('#playerBoxAvatar'));
    expect(avatarBox.nativeElement.textContent).toBe('JM');
  });

  it('should have cards and card containers', () => {
    expect(fixture.debugElement.query(By.css('#cardContainerLeft'))).toBeTruthy();
    expect(fixture.debugElement.query(By.css('#cardContainerRight'))).toBeTruthy();
    expect(fixture.debugElement.query(By.css('#firstCard'))).toBeTruthy();
    expect(fixture.debugElement.query(By.css('#secondCard'))).toBeTruthy();
  });

  it('should have cards that are not face down', () => {
    expect(component.cards).toBeTruthy();
    expect(component.cards.length).toBe(2);
    expect(component.cards[0].suit === CardSuit.Back).toBeFalsy();
    expect(component.cards[0].value === CardValue.Back).toBeFalsy();
    expect(component.cards[1].suit === CardSuit.Back).toBeFalsy();
    expect(component.cards[1].value === CardValue.Back).toBeFalsy();
  });

  it('should have the proper name', () => {
    const nameBox = fixture.debugElement.query(By.css('#nameBox'));
    expect(nameBox).toBeTruthy();
    expect(nameBox.childNodes.length).toBe(2);
    const name = fixture.debugElement.query(By.css('#name'));
    expect(name.nativeElement.textContent).toBe(`${samplePlayer.firstName} ${samplePlayer.lastName}`);
    expect(component.name).toBe(`${samplePlayer.firstName} ${samplePlayer.lastName}`);
  });

  it('should have the correct chips/bankroll', () => {
    const chips = fixture.debugElement.query(By.css('#chips'));
    expect(chips).toBeTruthy();
    expect(chips.nativeElement.textContent).toBe(`$${samplePlayer.chips}.00`);
  });

  it('should not have dealer button visible', () => {
    expect(fixture.debugElement.query(By.css('#dealerButton'))).toBeFalsy();
    expect(component.dealer).toBeFalsy();
  });

  it('should not have away icon visible', () => {
    expect(fixture.debugElement.query(By.css('#awayIcon'))).toBeFalsy();
    expect(component.away).toBeFalsy();
  });

  it('should have correct value in wager box', () => {
    const wagerBox = fixture.debugElement.query(By.css('#wagerBox'));
    expect(wagerBox.nativeElement.textContent).toBe(`$${samplePlayer.bet}.00`);
  });

  it('should hide/show cards when action is dispatched', () => {
    mockHiddenCardsSelector.setResult(mockAllHiddenCards);
    mockStore.refreshState();
    fixture.detectChanges();
    // Cards should still be there, just hidden in the UI.
    expect(component.cards).toBeTruthy();
    // Make sure cards are hidden in the UI.
    expect(fixture.debugElement.query(By.css('#cardContainerLeft'))).toBeFalsy();
    expect(fixture.debugElement.query(By.css('#cardContainerRight'))).toBeFalsy();
    expect(fixture.debugElement.query(By.css('#firstCard'))).toBeFalsy();
    expect(fixture.debugElement.query(By.css('#secondCard'))).toBeFalsy();

    mockHiddenCardsSelector.setResult([[false, true]]);
    mockStore.refreshState();
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('#cardContainerLeft'))).toBeTruthy();
    expect(fixture.debugElement.query(By.css('#cardContainerRight'))).toBeFalsy();
    expect(fixture.debugElement.query(By.css('#firstCard'))).toBeTruthy();
    expect(fixture.debugElement.query(By.css('#secondCard'))).toBeFalsy();
  });

  it('should show dealer button when player == dealer', () => {
    mockPokerTableSelector.setResult(({...table2, dealer: 0}));
    mockStore.refreshState();
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('#dealerButton'))).toBeTruthy();
    expect(component.dealer).toBeTruthy();
  });

  it('should show away icon when player is away', () => {
    const newPlayers = table2.players.map(p => ({...p}));
    newPlayers[0].away = true;
    mockPlayersSelector.setResult(newPlayers);
    mockStore.refreshState();
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('#awayIcon'))).toBeTruthy();
    expect(component.away).toBeTruthy();
  });

  it('should not show a wager if player has not wagered anything', () => {
    const newPlayers = table2.players.map(p => ({...p}));
    newPlayers[0].controls.currentBet = 0;
    mockPlayersSelector.setResult(newPlayers);
    mockStore.refreshState();
    fixture.detectChanges();
    const wagerBox = fixture.debugElement.query(By.css('#wagerBox'));
    expect(wagerBox.nativeElement.textContent).toBe('');
  });
});
