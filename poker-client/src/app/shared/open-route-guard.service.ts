/* tslint:disable */
import {Injectable} from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  UrlTree
} from '@angular/router';
import {Observable} from 'rxjs';
import {CookieService} from 'ngx-cookie-service';
import {AppStateContainer} from './models/app-state.model';
import {Store} from '@ngrx/store';
import {selectAuthenticated} from '../state/app.selector';
import {
  requestCurrentGameUpdate,
  requestPrivateTopic,
  signInFail,
  signInSuccess
} from '../state/app.actions';
import {APP_ROUTES} from "../app-routes";
import {UsersService} from "../api/services/users.service";
import {WebSocketService} from "./web-socket/web-socket.service";
import {AuthResponse} from "../api/models/auth-response";

@Injectable({
  providedIn: 'root'
})
export class OpenRouteGuardService implements CanActivate {
  private authenticated: boolean;

  constructor(
    private appStore: Store<AppStateContainer>,
    private cookieService: CookieService,
    private router: Router,
    private usersService: UsersService,
    private webSocketService: WebSocketService) {
    this.appStore.select(selectAuthenticated)
      .subscribe(authenticated => this.authenticated = authenticated);
  }

  helper() {
    if (window.location.search) {
      // TODO: Improve code.
      const page = window.location.search.split('=')[1];
      // console.log('should redirect to ' + page);
      return this.router.createUrlTree([`${page}`]);
    }
    return true;
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    if (this.authenticated) {
      // If authenticated, allow access.
      return true;
    } else if (this.cookieService.check('jwt')) {
      // Else if, check if there is a JWT cookie.
      return this.usersService.authorizeWithJwt({body: {jwt: this.cookieService.get('jwt')}})
        .toPromise().then((response: AuthResponse) => {
          this.webSocketService.subscribeToCurrentGameTopic(response.userDetails.id);
          this.appStore.dispatch(signInSuccess(response));
          this.appStore.dispatch(requestCurrentGameUpdate({userId: response.userDetails.id}));
          this.appStore.dispatch(requestPrivateTopic());
          return this.helper();
        }).catch(() => {
          // If authenticating with JWT cookie fails, redirect to login page.
          this.appStore.dispatch(signInFail());
          return this.router.createUrlTree([`/${APP_ROUTES.LOGIN.path}`]);
        });
    } else {
      // If there's no cookie, then redirect to login page.
      return true;
    }

    // if (!this.authenticated && this.cookieService.check('jwt')) {
    //   this.appStore.dispatch(signInWithJwt({jwt: this.cookieService.get('jwt')}));
    // }
    // if (window.location.search) {
    //   const page = window.location.search.split('=')[1];
    //   console.log('should redirect to ' + page);
    //   return this.router.createUrlTree([`${page}`]);
    // }
    // return true;
    // return this.auth$.pipe(switchMap(auth => {
    //   if (!auth && this.cookieService.check('jwt')) {
    //     this.appStore.dispatch(signInWithJwt({jwt: this.cookieService.get('jwt')}));
    //   }
    //   if (window.location.search) {
    //     const page = window.location.search.split('=')[1];
    //     console.log('should redirect to ' + page);
    //     return of(this.router.createUrlTree([`${page}`]));
    //   }
    //   return of(true);
    // }));

    // If user has not "authenticated", then dispatch special authentication using JWT from cookie.
    // if (!this.authenticated && this.cookieService.check('jwt')) {
    //   this.appStore.dispatch(signInWithJwt({jwt: this.cookieService.get('jwt')}));
    // }
    // console.log(window.location.search);
    // if (window.location.search) {
    //   const page = window.location.search.split('=')[1];
    //   console.log('should redirect to ' + page);
    //   return this.router.createUrlTree([`${page}`]);
    // }
    // return true;
  }
}
