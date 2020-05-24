import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UsersService} from 'src/app/api/services';
import {ApiSuccessModel, NewAccountModel} from 'src/app/api/models';
import {ToastService} from '../toast.service';

@Component({
  selector: 'pkr-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  /** Registration form. */
  public registrationForm: FormGroup;
  public showFailAlert: boolean = false;

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private usersService: UsersService,
    private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.registrationForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
      confirmPassword: ['', [Validators.required]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]]
    }, {validator: this.checkPasswords});
  }

  /**
   * Custom validator to ensure that the password and confirm password fields match.
   * @param group FormGroup that is being validated.
   */
  public checkPasswords(group: FormGroup) {
    const password = group.controls.password.value;
    const confirmPassword = group.controls.confirmPassword.value;
    return password === confirmPassword ? null : {notSame: true};
  }

  /**
   * Registers a new account. This should only be callable if the form is valid.
   * @param values Values from the registration form.
   */
  public register(values: any): void {
    this.usersService.register({
      body: {
        email: values.email,
        password: values.password,
        firstName: values.firstName,
        lastName: values.lastName
      } as NewAccountModel
    }).subscribe((response: ApiSuccessModel) => {
      this.toastService.show('Registration Successful!', {classname: 'bg-light toast-md', delay: 5000});
      this.router.navigate(['/login']);
    }, (error: any) => {
      console.log(error);
      this.showFailAlert = true;
    });
  }

  /**
   * Hides the alert that is produced when a login attempt fails.
   */
  public hideFailAlert(): void {
    this.showFailAlert = false;
  }
}
