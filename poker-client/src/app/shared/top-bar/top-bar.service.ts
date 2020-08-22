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
  /**
   * Top bar menu items that is generated anew each time the application state changes.
   */
  public topBarMenuItems: DropDownMenuItem[] = [];
  /**
   * Flag selected from the application state which indicates whether a user is logged in.
   */
  private authenticated = initialState.authenticated;

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

  /**
   * Getter for the home menu item.
   */
  public get homeMenuItem(): DropDownMenuItem {
    return {
      text: APP_ROUTES.HOME.label,
      anchor: APP_ROUTES.HOME.path
    };
  }

  /**
   * Getter for the game menu item dropdown.
   */
  public get gameMenuItems(): DropDownMenuItem {
    return {
      text: APP_ROUTES.GAME_PREFIX.label,
      anchor: null, // TODO: Should have a generic "game" page which links to join/create/etc...
      dropDown: [
        {
          text: APP_ROUTES.CREATE_GAME.label,
          anchor: APP_ROUTES.CREATE_GAME.path
        },
        {
          text: APP_ROUTES.JOIN_GAME.label,
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
      text: 'Account',
      anchor: null,
      dropDown: [
        {
          text: APP_ROUTES.REGISTER.label,
          anchor: APP_ROUTES.REGISTER.path
        },
        {
          text: APP_ROUTES.LOGIN.label,
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
      text: 'Account', // TODO: Change this to parameter and add general account page.
      anchor: null,
      dropDown: [
        {
          text: 'Edit Profile',
          anchor: 'edit-profile'
        },
        {
          text: 'View Statistics',
          anchor: 'view-statistics',
        },
        {
          text: APP_ROUTES.LOGOUT.label,
          anchor: APP_ROUTES.LOGOUT.path
        }
      ]
    };
  }

  public get aboutMenuItems(): DropDownMenuItem {
    return {
      text: APP_ROUTES.ABOUT.label,
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
