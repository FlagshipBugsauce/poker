import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TopBarInfoComponent} from './top-bar-info.component';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {MemoizedSelector} from '@ngrx/store';
import {AppStateContainer} from '../../models/app-state.model';
import {CurrentGame} from '../../../api/models/current-game';
import {selectCurrentGame} from '../../../state/app.selector';
import {mockCurrentGame} from '../../../testing/mock-models';
import {By} from '@angular/platform-browser';
import {Router} from '@angular/router';

describe('TopBarInfoComponent', () => {
  let component: TopBarInfoComponent;
  let fixture: ComponentFixture<TopBarInfoComponent>;
  let router: Router;
  let mockStore: MockStore;
  let mockCurrentGameSelector: MemoizedSelector<AppStateContainer, CurrentGame>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TopBarInfoComponent],
      providers: [provideMockStore()],
      imports: [
        RouterTestingModule.withRoutes([
          {path: '', component: TopBarInfoComponent},
          {path: 'game', component: TopBarInfoComponent}
        ])
      ]
    }).compileComponents();
    router = TestBed.inject(Router);
    mockStore = TestBed.inject(MockStore);
    mockCurrentGameSelector = mockStore.overrideSelector(selectCurrentGame, mockCurrentGame);
    fixture = TestBed.createComponent(TopBarInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not display rejoin game button when player is not in game', () => {
    mockCurrentGameSelector.setResult({inGame: false, id: null});
    mockStore.refreshState();
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('.btn'))).toBeNull();
  });

  it('should display rejoin game button when player is in game', () => {
    mockCurrentGameSelector.setResult({inGame: true, id: 'test-id'});
    mockStore.refreshState();
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('.btn'))).toBeTruthy();
  });

  it('should not display rejoin game button when on game route', fakeAsync(() => {
    mockCurrentGameSelector.setResult({inGame: true, id: 'test-id'});
    mockStore.refreshState();
    router.navigate(['game']);
    tick();
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('.btn'))).toBeNull();
  }));
});
