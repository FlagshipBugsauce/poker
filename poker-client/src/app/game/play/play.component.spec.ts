import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {PlayComponent} from './play.component';
import {RouterTestingModule} from '@angular/router/testing';
import {SharedModule} from '../../shared/shared.module';
import {SseService} from '../../shared/sse.service';
import {EmitterType} from '../../shared/models/emitter-type.model';
import {DrawGameDataModel} from '../../api/models/draw-game-data-model';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {MemoizedSelector} from '@ngrx/store';
import {
  AppStateContainer,
  GameDataStateContainer,
  GameStateContainer,
  HandStateContainer
} from '../../shared/models/app-state.model';
import * as selectors from '../../state/app.selector';
import {GameDocument, HandDocument, UserModel} from '../../api/models';
import {
  mockGameData,
  mockGameDocument,
  mockHandDocument,
  mockUser
} from '../../testing/mock-models';

class MockSseService {
  public closeEvent(type: EmitterType): void {
  }

  public openEvent(type: EmitterType, callback: () => void = null): void {
  }
}

describe('PlayComponent', () => {
  let mockStore: MockStore;
  let mockHandSelector: MemoizedSelector<HandStateContainer, HandDocument>;
  let mockUserSelector: MemoizedSelector<AppStateContainer, UserModel>;
  let mockGameDataSelector: MemoizedSelector<GameDataStateContainer, DrawGameDataModel[]>;
  let mockGameSelector: MemoizedSelector<GameStateContainer, GameDocument>;
  let component: PlayComponent;
  let fixture: ComponentFixture<PlayComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PlayComponent],
      providers: [
        {
          provide: SseService,
          useClass: MockSseService
        },
        provideMockStore()
      ],
      imports: [RouterTestingModule, SharedModule]
    }).compileComponents();

    fixture = TestBed.createComponent(PlayComponent);
    mockStore = TestBed.inject(MockStore);
    mockHandSelector = mockStore.overrideSelector(selectors.selectHandDocument, mockHandDocument);
    mockUserSelector = mockStore.overrideSelector(selectors.selectLoggedInUser, mockUser);
    mockGameDataSelector = mockStore.overrideSelector(selectors.selectGameData, mockGameData);
    mockGameSelector = mockStore.overrideSelector(selectors.selectGameDocument, mockGameDocument);
    fixture.detectChanges();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
