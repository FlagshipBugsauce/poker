import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UsersService } from 'src/app/api/services';
import { NewAccountModel, ApiSuccessModel } from 'src/app/api/models';

@Component({
  selector: 'pkr-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  public registrationForm: FormGroup;

  constructor(private router: Router, private formBuilder: FormBuilder, private usersService: UsersService) { }

  ngOnInit(): void {
    this.registrationForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
      confirmPassword: ['', [Validators.required]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]]
    }, { validator: this.checkPasswords });
  }

  public checkPasswords(group: FormGroup) {
    let password = group.controls.password.value;
    let confirmPassword = group.controls.confirmPassword.value;
    return password === confirmPassword ? null : { notSame: true };
  }

  public register(values: any): void {
    this.usersService.register({ body: <NewAccountModel>{
      email: values.email,
      password: values.password,
      firstName: values.firstName,
      lastName: values.lastName
    }}).subscribe((response: ApiSuccessModel) => {
      console.log(response);
      this.router.navigate(['/login']);
    }, (error: any) => {
      console.log(error);
    })
  }
}
