import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {GameComponent} from './game.component';
import {SharedModule} from '../../shared/shared.module';
import {RouterTestingModule} from '@angular/router/testing';
import {LobbyComponent} from '../lobby/lobby.component';
import {PlayComponent} from '../play/play.component';
import {EndComponent} from '../end/end.component';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {MemoizedSelector} from '@ngrx/store';
import {GameDataStateContainer} from '../../shared/models/app-state.model';
import * as selectors from '../../state/app.selector';
import {SseService} from '../../shared/sse.service';
import {MockSseService} from '../../testing/mock-services';
import {DrawGameDataModel} from '../../api/models/draw-game-data-model';
import {mockGameData} from '../../testing/mock-models';

describe('GameComponent', () => {
  let mockStore: MockStore;
  let mockGameDataSelector: MemoizedSelector<GameDataStateContainer, DrawGameDataModel[]>;
  let component: GameComponent;
  let fixture: ComponentFixture<GameComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        GameComponent,
        LobbyComponent,
        PlayComponent,
        EndComponent
      ],
      imports: [SharedModule, RouterTestingModule],
      providers: [
        {
          provide: SseService,
          useClass: MockSseService
        },
        provideMockStore()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(GameComponent);
    mockStore = TestBed.inject(MockStore);
    mockGameDataSelector = mockStore.overrideSelector(selectors.selectGameData, mockGameData);
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
