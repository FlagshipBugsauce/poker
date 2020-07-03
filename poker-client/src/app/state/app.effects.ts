import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {UsersService} from '../api/services/users.service';
import {signIn, signInFail, signInSuccess} from './app.actions';
import {catchError, exhaustMap, map} from 'rxjs/operators';
import {AuthRequestModel} from '../api/models/auth-request-model';
import {EMPTY, of} from 'rxjs';
import {AuthResponseModel} from '../api/models/auth-response-model';
import {ToastService} from '../shared/toast.service';
import {Router} from '@angular/router';
import {APP_ROUTES} from '../app-routes';

@Injectable()
export class AppEffects {

  signIn$ = createEffect(() => this.actions$.pipe(
    ofType(signIn),
    exhaustMap((action: AuthRequestModel) =>
      this.usersService.authorize({body: action})
      .pipe(
        map((response: AuthResponseModel) => {
          this.toastService.show(
            'Login Successful!',
            {classname: 'bg-light toast-md', delay: 5000}
            );
          this.router.navigate([`/${APP_ROUTES.HOME}`]).then();
          return signInSuccess(response);
        }), catchError(() => of({type: signInFail().type}))
      )
    ))
  );

  constructor(
    private actions$: Actions,
    private usersService: UsersService,
    private toastService: ToastService,
    private router: Router
  ) {}
}
