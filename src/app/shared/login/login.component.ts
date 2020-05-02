import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthRequestModel } from 'src/app/api/models';
import { ToastService } from '../toast.service';

@Component({
  selector: 'pkr-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  public loginForm: FormGroup;
  public showFailAlert: boolean = false;

  // TODO: Only for development! Remove later!
  private quickCredentials: AuthRequestModel = <AuthRequestModel> {
    email: "admin@domain.com",
    password: "admin!@#"
  }

  constructor(
    private authService: AuthService, 
    private router: Router, 
    private formBuilder: FormBuilder,
    public toastService: ToastService) { }

  public ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      email: [this.quickCredentials.email, [Validators.required, Validators.email]],
      password: [this.quickCredentials.password, [Validators.required]]
    });

    // DEV HELPER: AUTOMATICALLY AUTHORIZES AND NAVIGATES TO PAGE BEING WORKED ON
    this.authService.authorize(this.quickCredentials.email, this.quickCredentials.password).then((success: boolean) => {
      if (success) this.router.navigate(['/create']);
    });
  }

  /**
   * Calls the auth service to attempt authentication.
   * @param formValues The values of the login form.
   */
  public async authorize(formValues: any): Promise<void> {
    if (await this.authService.authorize(formValues.email, formValues.password)) {
      this.toastService.show("Login Successful!", { classname: 'bg-light toast-md', delay: 5000 });
      this.router.navigate(["/home"]);
    } else {
      this.showFailAlert = true;
    }
  }

  /**
   * Hides the alert that is produced when a login attempt fails.
   */
  public hideFailAlert(): void {
    this.showFailAlert = false;
  }
}
