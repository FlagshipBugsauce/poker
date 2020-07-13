import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {UsersService} from '../api/services/users.service';
import {
  requestCurrentGameUpdate,
  signIn,
  signInFail,
  signInSuccess,
  signInWithJwt,
  signOut
} from './app.actions';
import {catchError, exhaustMap, switchMap, tap} from 'rxjs/operators';
import {AuthRequestModel} from '../api/models/auth-request-model';
import {of} from 'rxjs';
import {AuthResponseModel} from '../api/models/auth-response-model';
import {ToastService} from '../shared/toast.service';
import {Router} from '@angular/router';
import {APP_ROUTES} from '../app-routes';
import {WebSocketService} from '../shared/web-socket/web-socket.service';
import {ActionModel} from '../api/models/action-model';
import {CookieService} from 'ngx-cookie-service';

@Injectable()
export class AppEffects {

  /**
   * Uses JWT from a cookie to perform a pseudo-authentication, which retrieves the same data as a
   * normal sign in.
   */
  signInWithJwt$ = createEffect(() => this.actions$.pipe(
    ofType(signInWithJwt),
    exhaustMap((action: { jwt: string }) =>
      this.usersService.authorizeWithJwt({body: {jwt: action.jwt}})
      .pipe(
        switchMap((response: AuthResponseModel) => {
          this.webSocketService.subscribeToCurrentGameTopic(response.userDetails.id);
          this.router.navigate([`/${APP_ROUTES.HOME.path}`]).then();
          return [signInSuccess(response), requestCurrentGameUpdate({userId: response.userDetails.id})];
        }), catchError(() => of({type: signInFail().type}))
      )
    )
  ));
  /**
   * Effect that will destroy the JWT cookie value.
   */
  signOut$ = createEffect(() => this.actions$.pipe(
    ofType(signOut),
    tap(() => this.cookieService.deleteAll())
  ), {dispatch: false});
  /**
   * Effect that will request an update in the current game topic.
   */
  requestCurrentGameUpdate$ = createEffect(() => this.actions$.pipe(
    ofType(requestCurrentGameUpdate),
    tap((action: ActionModel) => this.webSocketService.send(
      '/topic/game/current/update', {userId: action.userId}))
  ), {dispatch: false});
  /**
   * 2 weeks.
   */
  private twoWeeks: number = 12096e5;
  /**
   * Authenticates, retrieves a JWT and a user model of the user that logged in.
   */
  signIn$ = createEffect(() => this.actions$.pipe(
    ofType(signIn),
    exhaustMap((action: AuthRequestModel) =>
      this.usersService.authorize({body: action})
      .pipe(
        switchMap((response: AuthResponseModel) => {
          this.toastService.show(
            'Login Successful!',
            {classname: 'bg-light toast-md', delay: 5000}
          );
          // Subscribe to current game topic
          this.webSocketService.subscribeToCurrentGameTopic(response.userDetails.id);
          this.router.navigate([`/${APP_ROUTES.HOME}`]).then();
          this.cookieService.deleteAll();
          this.cookieService.set('jwt', response.jwt, new Date(Date.now() + this.twoWeeks));
          return [signInSuccess(response), requestCurrentGameUpdate({userId: response.userDetails.id})];
        }), catchError(() => of({type: signInFail().type}))
      )
    ))
  );

  constructor(
    private actions$: Actions,
    private webSocketService: WebSocketService,
    private usersService: UsersService,
    private toastService: ToastService,
    private router: Router,
    private cookieService: CookieService
  ) {
  }
}
