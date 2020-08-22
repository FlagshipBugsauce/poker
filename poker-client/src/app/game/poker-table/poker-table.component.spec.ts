import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PokerTableComponent} from './poker-table.component';
import {provideMockStore} from '@ngrx/store/testing';
import {DeckComponent} from './deck/deck.component';
import {PlayerBoxComponent} from './player-box/player-box.component';
import {HandSummaryComponent} from './hand-summary/hand-summary.component';
import {SharedModule} from '../../shared/shared.module';
import {PopupAfkComponent} from '../popup-afk/popup-afk.component';
import {TableControlsComponent} from './table-controls/table-controls.component';
import {CommunityCardsComponent} from './community-cards/community-cards.component';

describe('PokerTableComponent', () => {
  let component: PokerTableComponent;
  let fixture: ComponentFixture<PokerTableComponent>;

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
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PokerTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
