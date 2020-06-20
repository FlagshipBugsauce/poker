/* tslint:disable */
import {Injectable} from '@angular/core';
import {DropDownMenuItem} from '../models/menu-item.model';
import {Store} from "@ngrx/store";
import {AppState, AppStateContainer} from "../models/app-state.model";
import {initialState} from "../../state/app.reducer";
import {AuthService} from "../auth.service";
import {APP_ROUTES} from "../../app-routes";

@Injectable({
  providedIn: 'root'
})
export class TopBarService {
  private state: AppState = initialState;

  public get topBarIcon(): string {
    return 'assets/icons/aces.svg';
  }

  public get homeMenuItem(): DropDownMenuItem {
    return {
      text: APP_ROUTES.HOME.label,
      anchor: APP_ROUTES.HOME.path
    };
  }

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

  public get authenticatedAccountMenuItems(): DropDownMenuItem {
    return {
      text: 'Account', // TODO: Change this to parameter and add general account page.
      anchor: null,
      dropDown: [
        {
          text: 'Edit Profile',
          anchor: ''
        },
        {
          text: 'View Statistics',
          anchor: '',
        },
        {
          text: APP_ROUTES.LOGOUT.label,
          anchor: APP_ROUTES.LOGOUT.path
        }
      ]
    }
  }

  public topBarMenuItems: DropDownMenuItem[] = [];

  constructor(private authService: AuthService, private store: Store<{appState: AppState}>) {
    this.updateMenuItems();
    store.subscribe((state: AppStateContainer) => {
      this.state = state.appState;
      this.updateMenuItems();
    });
  }

  private updateMenuItems(): void {
    this.topBarMenuItems = [];
    this.topBarMenuItems.push(this.homeMenuItem);
    if (this.state.authenticated) {
      this.topBarMenuItems.push(this.gameMenuItems);
      this.topBarMenuItems.push(this.authenticatedAccountMenuItems);
    } else {
      this.topBarMenuItems.push(this.unauthenticatedAccountMenuItems);
    }
  }
}
