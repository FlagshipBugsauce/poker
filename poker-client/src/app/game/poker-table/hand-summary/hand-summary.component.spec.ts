import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {HandSummaryComponent} from './hand-summary.component';

describe('HandSummaryComponent', () => {
  let component: HandSummaryComponent;
  let fixture: ComponentFixture<HandSummaryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [HandSummaryComponent]
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
