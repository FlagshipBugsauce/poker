import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {HandSummaryComponent} from './hand-summary.component';
import {provideMockStore} from '@ngrx/store/testing';
import {SharedModule} from '../../../shared/shared.module';

describe('HandSummaryComponent', () => {
  let component: HandSummaryComponent;
  let fixture: ComponentFixture<HandSummaryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [HandSummaryComponent],
      providers: [provideMockStore()],
      imports: [SharedModule]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HandSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
