import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {PlayComponent} from './play.component';
import {RouterTestingModule} from '@angular/router/testing';
import {SharedModule} from '../../shared/shared.module';
import {DrawGameDataModel} from '../../api/models/draw-game-data-model';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {MemoizedSelector} from '@ngrx/store';
import {
  AppStateContainer,
  GameDataStateContainer,
  GameStateContainer,
  HandStateContainer,
  PlayerDataStateContainer
} from '../../shared/models/app-state.model';
import * as selectors from '../../state/app.selector';
import {GameModel, HandDocument, UserModel} from '../../api/models';
import {
  mockGameData,
  mockGameModel,
  mockHandDocument,
  mockUser
} from '../../testing/mock-models';
import {PopupAfkComponent} from '../popup-afk/popup-afk.component';
import {WebSocketService} from '../../shared/web-socket/web-socket.service';

describe('PlayComponent', () => {
  let mockStore: MockStore;
  let mockHandSelector: MemoizedSelector<HandStateContainer, HandDocument>;
  let mockUserSelector: MemoizedSelector<AppStateContainer, UserModel>;
  let mockGameDataSelector: MemoizedSelector<GameDataStateContainer, DrawGameDataModel[]>;
  let mockGameSelector: MemoizedSelector<GameStateContainer, GameModel>;
  let mockAwayStatusSelector: MemoizedSelector<PlayerDataStateContainer, boolean>;
  let mockActingStatusSelector: MemoizedSelector<PlayerDataStateContainer, boolean>;
  let mockJwtSelector: MemoizedSelector<AppStateContainer, string>;
  let component: PlayComponent;
  let fixture: ComponentFixture<PlayComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PlayComponent, PopupAfkComponent],
      providers: [
        provideMockStore(),
        {
          provide: WebSocketService,
          useClass: jest.fn()
        }
      ],
      imports: [RouterTestingModule, SharedModule]
    }).compileComponents();

    mockStore = TestBed.inject(MockStore);
    mockHandSelector = mockStore.overrideSelector(selectors.selectHandModel, mockHandDocument);
    mockUserSelector = mockStore.overrideSelector(selectors.selectLoggedInUser, mockUser);
    mockGameDataSelector = mockStore.overrideSelector(selectors.selectGameData, mockGameData);
    mockGameSelector = mockStore.overrideSelector(selectors.selectGameModel, mockGameModel);
    mockAwayStatusSelector = mockStore.overrideSelector(selectors.selectAwayStatus, false);
    mockActingStatusSelector = mockStore.overrideSelector(selectors.selectActingStatus, false);
    mockJwtSelector = mockStore.overrideSelector(selectors.selectJwt, 'jwt');
    fixture = TestBed.createComponent(PlayComponent);
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
