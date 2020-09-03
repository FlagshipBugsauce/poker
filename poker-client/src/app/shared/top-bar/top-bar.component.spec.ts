import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {TopBarComponent} from './top-bar.component';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {TopBarItemComponent} from './top-bar-item/top-bar-item.component';
import {RouterTestingModule} from '@angular/router/testing';
import {TopBarInfoComponent} from './top-bar-info/top-bar-info.component';
import {MemoizedSelector} from '@ngrx/store';
import {AppStateContainer} from '../models/app-state.model';
import {selectAuthenticated, selectCurrentGame} from '../../state/app.selector';
import {initialState} from '../../state/app.reducer';
import {CurrentGame} from '../../api/models';
import {mockCurrentGame} from '../../testing/mock-models';
import {APP_ROUTES} from '../../app-routes';
import {TopBarService} from './top-bar.service';
import {DropDownMenuItem} from '../models/menu-item.model';

describe('TopBarComponent', () => {
  let component: TopBarComponent;
  let fixture: ComponentFixture<TopBarComponent>;
  let mockStore: MockStore;
  let mockAuthenticatedSelector: MemoizedSelector<AppStateContainer, boolean>;
  let mockCurrentGameSelector: MemoizedSelector<AppStateContainer, CurrentGame>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        TopBarComponent,
        TopBarItemComponent,
        TopBarInfoComponent
      ],
      imports: [
        RouterTestingModule
      ],
      providers: [
        provideMockStore({initialState})
      ]
    }).compileComponents();
    mockStore = TestBed.inject(MockStore);
    mockAuthenticatedSelector = mockStore.overrideSelector(selectAuthenticated, false);
    mockCurrentGameSelector = mockStore.overrideSelector(selectCurrentGame, mockCurrentGame);
    fixture = TestBed.createComponent(TopBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have 3 menu items when not authenticated', () => {
    mockAuthenticatedSelector.setResult(false);
    mockStore.refreshState();
    expect(component.topBarService.topBarMenuItems.length).toBe(3);
  });

  it('should have 4 menu items when authenticated', () => {
    mockAuthenticatedSelector.setResult(true);
    mockStore.refreshState();
    expect(component.topBarService.topBarMenuItems.length).toBe(4);
  });

  it('should not have game dropdown when not authenticated', () => {
    mockAuthenticatedSelector.setResult(false);
    mockStore.refreshState();
    expect(component
    .topBarService
    .topBarMenuItems
    .filter(item => item.text === TopBarService.GAME_ITEM_TEXT))
    .toEqual([]);
  });

  it('should have game dropdown when authenticated', () => {
    mockAuthenticatedSelector.setResult(true);
    mockStore.refreshState();
    const gameDropDown: DropDownMenuItem[] = component.topBarService.topBarMenuItems
    .filter(item => item.text === TopBarService.GAME_ITEM_TEXT);
    expect(gameDropDown.length).toBe(1);
    expect(gameDropDown[0].dropDown.length).toBe(2);
  });

  it('should have correct account dropdown items when not authenticated', () => {
    mockAuthenticatedSelector.setResult(false);
    mockStore.refreshState();
    const accountDropDown: DropDownMenuItem = component.topBarService.topBarMenuItems
    .filter(item => item.text === TopBarService.ACCOUNT_ITEM_TEXT)[0];
    expect(accountDropDown.dropDown.length).toBe(2);
    expect(accountDropDown.dropDown[0].text).toBe(TopBarService.REGISTER_ITEM_TEXT);
    expect(accountDropDown.dropDown[1].text).toBe(TopBarService.LOGIN_ITEM_TEXT);
    expect(accountDropDown.dropDown[0].anchor).toBe(APP_ROUTES.REGISTER.path);
    expect(accountDropDown.dropDown[1].anchor).toBe(APP_ROUTES.LOGIN.path);
  });

  it('should have correct account dropdown items when authenticated', () => {
    mockAuthenticatedSelector.setResult(true);
    mockStore.refreshState();
    const accountDropDown: DropDownMenuItem = component.topBarService.topBarMenuItems
    .filter(item => item.text === TopBarService.ACCOUNT_ITEM_TEXT)[0];
    expect(accountDropDown.dropDown.length).toBe(3);
    expect(accountDropDown.dropDown[0].text).toBe(TopBarService.EDIT_PROFILE_ITEM_TEXT);
    expect(accountDropDown.dropDown[1].text).toBe(TopBarService.VIEW_STATS_ITEM_TEXT);
    expect(accountDropDown.dropDown[2].text).toBe(TopBarService.LOGOUT_ITEM_TEXT);
    // edit-profile and view-statistics pages have not been implemented yet.
    expect(accountDropDown.dropDown[2].anchor).toBe(APP_ROUTES.LOGOUT.path);
  });
});
