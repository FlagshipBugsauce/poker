import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PokerTableComponent} from './poker-table.component';
import {provideMockStore} from '@ngrx/store/testing';
import {DeckComponent} from './deck/deck.component';
import {PlayerBoxComponent} from './player-box/player-box.component';
import {HandSummaryComponent} from './hand-summary/hand-summary.component';
import {SharedModule} from '../../shared/shared.module';

describe('PokerTableComponent', () => {
  let component: PokerTableComponent;
  let fixture: ComponentFixture<PokerTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        PokerTableComponent,
        DeckComponent,
        PlayerBoxComponent,
        HandSummaryComponent
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
