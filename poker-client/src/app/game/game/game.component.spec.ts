import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {GameComponent} from './game.component';
import {SharedModule} from '../../shared/shared.module';
import {RouterTestingModule} from '@angular/router/testing';
import {LobbyComponent} from '../lobby/lobby.component';
import {PlayComponent} from '../play/play.component';
import {EndComponent} from '../end/end.component';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {MemoizedSelector} from '@ngrx/store';
import {
  AppStateContainer,
  GameDataStateContainer,
  GameStateContainer
} from '../../shared/models/app-state.model';
import * as selectors from '../../state/app.selector';
import {DrawGameDataModel} from '../../api/models/draw-game-data-model';
import {mockGameData, mockUser} from '../../testing/mock-models';
import {PopupAfkComponent} from '../popup-afk/popup-afk.component';
import {WebSocketService} from '../../shared/web-socket.service';
import {UserModel} from '../../api/models/user-model';

describe('GameComponent', () => {
  let mockStore: MockStore;
  let mockGameDataSelector: MemoizedSelector<GameDataStateContainer, DrawGameDataModel[]>;
  let mockGameStateSelector: MemoizedSelector<GameStateContainer, string>;
  let mockLoggedInUserSelector: MemoizedSelector<AppStateContainer, UserModel>;
  let component: GameComponent;
  let fixture: ComponentFixture<GameComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        GameComponent,
        LobbyComponent,
        PlayComponent,
        EndComponent,
        PopupAfkComponent
      ],
      imports: [SharedModule, RouterTestingModule],
      providers: [
        provideMockStore(),
        {
          provide: WebSocketService,
          useClass: jest.fn()
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(GameComponent);
    mockStore = TestBed.inject(MockStore);
    mockGameDataSelector = mockStore.overrideSelector(selectors.selectGameData, mockGameData);
    mockGameStateSelector = mockStore.overrideSelector(selectors.selectGameState, 'Lobby');
    mockLoggedInUserSelector = mockStore.overrideSelector(selectors.selectLoggedInUser, mockUser);
    fixture.detectChanges();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
