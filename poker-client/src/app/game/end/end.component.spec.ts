import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {EndComponent} from './end.component';
import {SharedModule} from '../../shared/shared.module';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {MemoizedSelector} from '@ngrx/store';
import {GameStateContainer} from '../../shared/models/app-state.model';
import {GameDocument} from '../../api/models/game-document';
import * as selectors from '../../state/app.selector';
import {mockGameDocument} from '../../testing/mock-models';

describe('EndComponent', () => {
  let mockStore: MockStore;
  let mockGameSelector: MemoizedSelector<GameStateContainer, GameDocument>;
  let component: EndComponent;
  let fixture: ComponentFixture<EndComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EndComponent],
      imports: [SharedModule],
      providers: [provideMockStore()]
    }).compileComponents();

    fixture = TestBed.createComponent(EndComponent);
    mockStore = TestBed.inject(MockStore);
    mockGameSelector = mockStore.overrideSelector(selectors.selectGameDocument, mockGameDocument);
    fixture.detectChanges();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EndComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
