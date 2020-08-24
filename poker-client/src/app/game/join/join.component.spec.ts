import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {JoinComponent} from './join.component';
import {RouterTestingModule} from '@angular/router/testing';
import {SharedModule} from '../../shared/shared.module';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {WebSocketService} from '../../shared/web-socket/web-socket.service';
import {MockWebSocketService} from '../../testing/mock-services';
import {MemoizedSelector} from '@ngrx/store';
import {GameListStateContainer} from '../../shared/models/app-state.model';
import {selectGameList} from '../../state/app.selector';
import {GameList} from '../../api/models/game-list';

describe('JoinComponent', () => {
  let mockStore: MockStore;
  let mockGameListSelector: MemoizedSelector<GameListStateContainer, GameList[]>;
  let component: JoinComponent;
  let fixture: ComponentFixture<JoinComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [JoinComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        provideMockStore(),
        {
          provide: WebSocketService,
          useClass: MockWebSocketService
        }
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(JoinComponent);
    mockStore = TestBed.inject(MockStore);
    mockGameListSelector = mockStore.overrideSelector(selectGameList, [] as GameList[]);
    fixture.detectChanges();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JoinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
