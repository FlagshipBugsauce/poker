import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthRequestModel} from 'src/app/api/models';
import {AppStateContainer} from '../models/app-state.model';
import {Store} from '@ngrx/store';
import {hideFailedSignInWarning, signIn} from '../../state/app.actions';
import {selectSignInFail} from '../../state/app.selector';
import {Observable, Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';

@Component({
  selector: 'pkr-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {

  public loginForm: FormGroup;
  public ngDestroyed$ = new Subject();

  // TODO: Only for development! Remove later! These are local accounts that will not exist in production.
  // private quickCredentials: AuthRequestModel = <AuthRequestModel> {
  //   email: "admin@domain.com",
  //   password: "admin!@#"
  // }
  // private quickCredentials: AuthRequestModel = <AuthRequestModel> {
  //   email: "test.account@domain.com",
  //   password: "admin!@#"
  // }
  // private quickCredentials: AuthRequestModel = <AuthRequestModel> {
  //   email: "test.account2@domain.com",
  //   password: "admin!@#"
  // }
  // private quickCredentials: AuthRequestModel = {
  //   email: 'jon@domain.com',
  //   password: 'jonathan'
  // } as AuthRequestModel;
  public showFailedSignIn$: Observable<boolean>;
  private quickCredentials: AuthRequestModel = {
    email: '',
    password: ''
  } as AuthRequestModel;

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private appStore: Store<AppStateContainer>) {
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
  }

  public ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      email: [this.quickCredentials.email, [Validators.required, Validators.email]],
      password: [this.quickCredentials.password, [Validators.required]]
    });

    // DEV HELPER: AUTOMATICALLY AUTHORIZES AND NAVIGATES TO PAGE BEING WORKED ON
    // this.authService.authorize(this.quickCredentials.email, this.quickCredentials.password).then((success: boolean) => {
    //   // if (success) this.router.navigate(['/game/0a7d95ef-94ba-47bc-b591-febb365bc543']);
    //   if (success) {
    //     this.router.navigate(['/home']).then();
    //   }
    // });

    this.showFailedSignIn$ = this.appStore.select(selectSignInFail).pipe(takeUntil(this.ngDestroyed$));
  }

  /**
   * Calls the auth service to attempt authentication.
   * @param formValues The values of the login form.
   */
  public async authorize(formValues: any): Promise<void> {
    this.appStore.dispatch(signIn({email: formValues.email, password: formValues.password}));
  }

  /**
   * Hides the alert that is produced when a login attempt fails.
   */
  public hideFailAlert(): void {
    this.appStore.dispatch(hideFailedSignInWarning());
  }
}
