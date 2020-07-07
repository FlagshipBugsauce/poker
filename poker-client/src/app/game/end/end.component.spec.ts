import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {EndComponent} from './end.component';
import {SharedModule} from '../../shared/shared.module';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {MemoizedSelector} from '@ngrx/store';
import {GameStateContainer} from '../../shared/models/app-state.model';
import * as selectors from '../../state/app.selector';
import {mockGameModel} from '../../testing/mock-models';
import {GameModel} from '../../api/models';

describe('EndComponent', () => {
  let mockStore: MockStore;
  let mockGameSelector: MemoizedSelector<GameStateContainer, GameModel>;
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
    mockGameSelector = mockStore.overrideSelector(selectors.selectGameModel, mockGameModel);
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
