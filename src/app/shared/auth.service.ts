import { Injectable } from '@angular/core';
import { ApiInterceptor } from '../api-interceptor.service';
import { UsersService } from '../api/services';
import { AuthRequestModel, AuthResponseModel } from '../api/models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private loggedIn: boolean = false;

  constructor(private apiInterceptor: ApiInterceptor, private usersService: UsersService) { }

  /**
   * Authorizes the user and stores the authorization token in the apiInterceptor, which will
   * add the token to all future requests.
   * @param email The email being used to authenticate.
   * @param password The password being used to authenticate.
   */
  public authorize(email: string, password: string): void {
    this.usersService.authorize({ body: <AuthRequestModel> { email: email, password: password }})
      .subscribe(
        (data: AuthResponseModel) => {
          this.apiInterceptor.jwt = data.jwt;
          this.loggedIn = true;
        },
        (error: any) => console.log(error)
      );
  }

  /**
   * Used to determine if a user has authenticated. Returns true when user has authenticated, false otherwise.
   */
  public get authenticated(): boolean {
    return this.loggedIn;
  }

  /**
   * Logs a user out of the site, destroying their token.
   */
  public logout() {
    this.apiInterceptor.jwt = "";   // Delete the JWT stored in memory.
    this.loggedIn = false;          // Set loggedIn to false.
  }
}
