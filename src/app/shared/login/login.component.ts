import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthRequestModel } from 'src/app/api/models';

@Component({
  selector: 'pkr-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  public loginForm: FormGroup;

  // TODO: Only for development! Remove later!
  private quickCredentials: AuthRequestModel = <AuthRequestModel> {
    email: "admin@domain.com",
    password: "admin!@#"
  }

  constructor(private authService: AuthService, private router: Router, private formBuilder: FormBuilder) { }

  public ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      email: [this.quickCredentials.email, [Validators.required, Validators.email]],
      password: [this.quickCredentials.password, [Validators.required]]
    });
  }

  public authorize(formValues: any): void {
    this.authService.authorize(formValues.email, formValues.password);
  }
}
