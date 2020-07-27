import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DeckComponent} from './deck.component';
import {provideMockStore} from '@ngrx/store/testing';

describe('DeckComponent', () => {
  let component: DeckComponent;
  let fixture: ComponentFixture<DeckComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DeckComponent],
      imports: [],
      providers: [provideMockStore()]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeckComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
