import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {LobbyComponent} from './lobby.component';
import {SharedModule} from '../../shared/shared.module';
import {RouterTestingModule} from '@angular/router/testing';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {MemoizedSelector} from '@ngrx/store';
import {AppStateContainer, LobbyStateContainer} from '../../shared/models/app-state.model';
import * as selectors from '../../state/app.selector';
import {mockLobbyModel, mockUser} from '../../testing/mock-models';
import {ClientUser} from '../../api/models/client-user';
import {By} from '@angular/platform-browser';
import {Lobby} from '../../api/models/lobby';

describe('LobbyComponent', () => {
  let mockStore: MockStore;
  let mockReadySelector: MemoizedSelector<AppStateContainer, boolean>;
  let mockUserSelector: MemoizedSelector<AppStateContainer, ClientUser>;
  let mockLobbySelector: MemoizedSelector<LobbyStateContainer, Lobby>;
  let component: LobbyComponent;
  let fixture: ComponentFixture<LobbyComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LobbyComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [
        provideMockStore()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LobbyComponent);
    mockStore = TestBed.inject(MockStore);
    mockReadySelector = mockStore.overrideSelector(selectors.selectReadyStatus, false);
    mockUserSelector = mockStore.overrideSelector(selectors.selectLoggedInUser, mockUser);
    mockLobbySelector = mockStore.overrideSelector(selectors.selectLobbyModel, mockLobbyModel);
    fixture.detectChanges();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LobbyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('ready button text changes', () => {
    expect(fixture.debugElement.query(By.css('.btn-success')).nativeElement.innerHTML)
    .toEqual('Ready');
    mockReadySelector.setResult(true);
    mockStore.refreshState();
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('.btn-success')).nativeElement.innerHTML)
    .toEqual('Un-Ready');
  });
});
