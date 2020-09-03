import {Injectable} from '@angular/core';
import {DropDownMenuItem} from '../models/menu-item.model';
import {Store} from '@ngrx/store';
import {AppStateContainer} from '../models/app-state.model';
import {initialState} from '../../state/app.reducer';
import {APP_ROUTES} from '../../app-routes';
import {selectAuthenticated} from '../../state/app.selector';

@Injectable({
  providedIn: 'root'
})
export class TopBarService {

  constructor(private store: Store<AppStateContainer>) {
    this.updateMenuItems();
    store.select(selectAuthenticated).subscribe((authenticated: boolean) => {
      this.authenticated = authenticated;
      this.updateMenuItems();
    });
  }

  /**
   * Getter for the path to the icon on the top bar.
   */
  public get topBarIcon(): string {
    return 'assets/icons/aces.svg';
  }

  public static HOME_ITEM_TEXT: string = APP_ROUTES.HOME.label;
  public static ACCOUNT_ITEM_TEXT: string = 'Account';
  public static REGISTER_ITEM_TEXT: string = APP_ROUTES.REGISTER.label;
  public static LOGIN_ITEM_TEXT: string = APP_ROUTES.LOGIN.label;
  public static EDIT_PROFILE_ITEM_TEXT: string = 'Edit Profile';
  public static VIEW_STATS_ITEM_TEXT: string = 'View Statistics';
  public static LOGOUT_ITEM_TEXT: string = 'APP_ROUTES.LOGOUT.label';
  public static GAME_ITEM_TEXT: string = APP_ROUTES.GAME_PREFIX.label;
  public static CREATE_GAME_ITEM_TEXT: string = APP_ROUTES.CREATE_GAME.label;
  public static JOIN_GAME_ITEM_TEXT: string = APP_ROUTES.JOIN_GAME.label;
  public static ABOUT_ITEM_TEXT: string = APP_ROUTES.ABOUT.label;
  /**
   * Top bar menu items that is generated anew each time the application state changes.
   */
  public topBarMenuItems: DropDownMenuItem[] = [];
  /**
   * Flag selected from the application state which indicates whether a user is logged in.
   */
  private authenticated = initialState.authenticated;

  /**
   * Getter for the home menu item.
   */
  public get homeMenuItem(): DropDownMenuItem {
    return {
      text: TopBarService.HOME_ITEM_TEXT,
      anchor: APP_ROUTES.HOME.path
    };
  }

  /**
   * Getter for the game menu item dropdown.
   */
  public get gameMenuItems(): DropDownMenuItem {
    return {
      text: TopBarService.GAME_ITEM_TEXT,
      anchor: null, // TODO: Should have a generic "game" page which links to join/create/etc...
      dropDown: [
        {
          text: TopBarService.CREATE_GAME_ITEM_TEXT,
          anchor: APP_ROUTES.CREATE_GAME.path
        },
        {
          text: TopBarService.JOIN_GAME_ITEM_TEXT,
          anchor: APP_ROUTES.JOIN_GAME.path
        }
      ]
    };
  }

  /**
   * Getter for the account menu item dropdown that should be presented when a user is not
   * authenticated.
   */
  public get unauthenticatedAccountMenuItems(): DropDownMenuItem {
    return {
      text: TopBarService.ACCOUNT_ITEM_TEXT,
      anchor: null,
      dropDown: [
        {
          text: TopBarService.REGISTER_ITEM_TEXT,
          anchor: APP_ROUTES.REGISTER.path
        },
        {
          text: TopBarService.LOGIN_ITEM_TEXT,
          anchor: APP_ROUTES.LOGIN.path
        }
      ]
    };
  }

  /**
   * Getter for the account menu item dropdown that should be presented when a user is
   * authenticated.
   */
  public get authenticatedAccountMenuItems(): DropDownMenuItem {
    return {
      text: TopBarService.ACCOUNT_ITEM_TEXT,
      anchor: null,
      dropDown: [
        {
          text: TopBarService.EDIT_PROFILE_ITEM_TEXT,
          anchor: 'edit-profile'
        },
        {
          text: TopBarService.VIEW_STATS_ITEM_TEXT,
          anchor: 'view-statistics',
        },
        {
          text: TopBarService.LOGOUT_ITEM_TEXT,
          anchor: APP_ROUTES.LOGOUT.path
        }
      ]
    };
  }

  public get aboutMenuItems(): DropDownMenuItem {
    return {
      text: TopBarService.ABOUT_ITEM_TEXT,
      anchor: APP_ROUTES.ABOUT.path
    };
  }

  /**
   * Helper to update menu items whenever a state change is detected.
   */
  private updateMenuItems(): void {
    this.topBarMenuItems = [];
    this.topBarMenuItems.push(this.homeMenuItem);
    if (this.authenticated) {
      this.topBarMenuItems.push(this.gameMenuItems);
      this.topBarMenuItems.push(this.authenticatedAccountMenuItems);
    } else {
      this.topBarMenuItems.push(this.unauthenticatedAccountMenuItems);
    }
    this.topBarMenuItems.push(this.aboutMenuItems);
  }
}
