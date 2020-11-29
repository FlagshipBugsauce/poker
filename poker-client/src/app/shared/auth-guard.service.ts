/* tslint:disable */
import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {APP_ROUTES} from '../app-routes';
import {AppStateContainer} from './models/app-state.model';
import {Store} from '@ngrx/store';
import {selectAuthenticated} from '../state/app.selector';
import {
  requestCurrentGameUpdate,
  requestPrivateTopic,
  signInFail,
  signInSuccess,
  signOut
} from '../state/app.actions';
import {CookieService} from 'ngx-cookie-service';
import {UsersService} from "../api/services/users.service";
import {AuthResponse} from "../api/models/auth-response";
import {WebSocketService} from "./web-socket/web-socket.service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

  private authenticated: boolean = false;

  constructor(
    private router: Router,
    private usersService: UsersService,
    private webSocketService: WebSocketService,
    private appStore: Store<AppStateContainer>,
    private cookieService: CookieService) {

    this.appStore.select(selectAuthenticated)
      .subscribe(authenticated => this.authenticated = authenticated);
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    // If we are linked to "/logout", then call logout() method and redirect to login.
    if (state.url === `/${APP_ROUTES.LOGOUT.path}`) {
      this.appStore.dispatch(signOut());
      return this.router.navigate([`/${APP_ROUTES.LOGIN.path}`]).then(() => false);
    }
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
          return true;
        }).catch(() => {
          // If authenticating with JWT cookie fails, redirect to login page.
          this.appStore.dispatch(signInFail());
          return this.router.createUrlTree([`/${APP_ROUTES.LOGIN.path}`]);
        });
    } else {
      // If there's no cookie, then redirect to login page.
      return this.router.createUrlTree([`/${APP_ROUTES.LOGIN.path}`]);
    }


    // return this.auth$.pipe(switchMap(auth => {
    //   if (!auth && this.cookieService.check('jwt')) {
    //     this.appStore.dispatch(signInWithJwt({jwt: this.cookieService.get('jwt')}));
    //     return this.appStore.select(selectAuthenticated).pipe(switchMap(auth => of(auth)));
    //   }
    //
    //   if (!auth) {
    //     return of(this.router.createUrlTree([`/${APP_ROUTES.LOGIN.path}`]));
    //   }
    //   return of(auth);
    // }));

    // If user has not "authenticated", then dispatch special authentication using JWT from cookie.
    // if (!this.authenticated && this.cookieService.check('jwt')) {
    //   this.appStore.dispatch(signInWithJwt({jwt: this.cookieService.get('jwt')}));
    //   return false;
    // }
    //
    // if (!this.authenticated) {
    //   this.router.navigate([`/${APP_ROUTES.LOGIN.path}`]).then();
    // } // Navigate to login page if user is not logged in
    // return this.authenticated;
  }
}
