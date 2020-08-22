import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CommunityCardsComponent} from './community-cards.component';
import {provideMockStore} from '@ngrx/store/testing';
import {SharedModule} from '../../../shared/shared.module';

describe('CommunityCardsComponent', () => {
  let component: CommunityCardsComponent;
  let fixture: ComponentFixture<CommunityCardsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
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
    fixture = TestBed.createComponent(CommunityCardsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
