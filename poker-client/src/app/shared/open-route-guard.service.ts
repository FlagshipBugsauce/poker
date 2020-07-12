import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree} from '@angular/router';
import {Observable} from 'rxjs';
import {CookieService} from 'ngx-cookie-service';
import {AppStateContainer} from './models/app-state.model';
import {Store} from '@ngrx/store';
import {selectAuthenticated} from '../state/app.selector';
import {signInWithJwt} from '../state/app.actions';

@Injectable({
  providedIn: 'root'
})
export class OpenRouteGuardService implements CanActivate {
  private authenticated: boolean;

  constructor(
    private appStore: Store<AppStateContainer>,
    private cookieService: CookieService) {
    this.appStore.select(selectAuthenticated)
    .subscribe(authenticated => this.authenticated = authenticated);
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot):
    Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    // If user has not "authenticated", then dispatch special authentication using JWT from cookie.
    if (!this.authenticated && this.cookieService.check('jwt')) {
      this.appStore.dispatch(signInWithJwt({
        jwt: this.cookieService.get('jwt'),
        url: state.url
      }));
    }
    return true;
  }
}
