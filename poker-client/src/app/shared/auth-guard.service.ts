import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {AuthService} from './auth.service';
import {APP_ROUTES} from '../app-routes';

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {
  }

  canActivate(
    route: import('@angular/router').ActivatedRouteSnapshot,
    state: import('@angular/router').RouterStateSnapshot): boolean {
    // If we are linked to "/logout", then call logout() method and redirect to login.
    if (state.url === `/${APP_ROUTES.LOGOUT.path}`) {
      this.authService.logout();
      this.router.navigate([`/${APP_ROUTES.LOGIN.path}`]).then();
      return true;
    }

    if (!this.authService.authenticated) {
      this.router.navigate([`/${APP_ROUTES.LOGIN.path}`]).then();
    } // Navigate to login page if user is not logged in
    return this.authService.authenticated;
  }
}
