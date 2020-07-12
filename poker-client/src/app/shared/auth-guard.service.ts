import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {APP_ROUTES} from '../app-routes';
import {AppStateContainer} from './models/app-state.model';
import {Store} from '@ngrx/store';
import {selectAuthenticated} from '../state/app.selector';
import {signInWithJwt, signOut} from '../state/app.actions';
import {CookieService} from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

  private authenticated: boolean = false;

  constructor(
    private router: Router,
    private appStore: Store<AppStateContainer>,
    private cookieService: CookieService) {

    this.appStore.select(selectAuthenticated)
    .subscribe(authenticated => this.authenticated = authenticated);
  }

  canActivate(
    route: import('@angular/router').ActivatedRouteSnapshot,
    state: import('@angular/router').RouterStateSnapshot): boolean {
    // If we are linked to "/logout", then call logout() method and redirect to login.
    if (state.url === `/${APP_ROUTES.LOGOUT.path}`) {
      this.appStore.dispatch(signOut());
      this.router.navigate([`/${APP_ROUTES.LOGIN.path}`]).then();
      return true;
    }

    // If user has not "authenticated", then dispatch special authentication using JWT from cookie.
    if (!this.authenticated && this.cookieService.check('jwt')) {
      this.appStore.dispatch(signInWithJwt({jwt: this.cookieService.get('jwt')}));
      return false;
    }

    if (!this.authenticated) {
      this.router.navigate([`/${APP_ROUTES.LOGIN.path}`]).then();
    } // Navigate to login page if user is not logged in
    return this.authenticated;
  }
}
