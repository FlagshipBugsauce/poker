import {Injectable} from '@angular/core';
import {ApiInterceptor} from '../api-interceptor.service';
import {UsersService} from '../api/services';
import {AuthRequestModel, AuthResponseModel, UserModel} from '../api/models';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private loggedIn: boolean = false;
  private userModelInternal: UserModel = {} as UserModel;

  constructor(private apiInterceptor: ApiInterceptor, private usersService: UsersService, private router: Router) {
  }

  /**
   * Authorizes the user and stores the authorization token in the apiInterceptor, which will
   * add the token to all future requests.
   * @param email The email being used to authenticate.
   * @param password The password being used to authenticate.
   */
  public async authorize(email: string, password: string): Promise<boolean> {
    await this.usersService.authorize({body: {email, password} as AuthRequestModel})
    .toPromise().then((authResponseModel: AuthResponseModel) => {
      this.apiInterceptor.jwt = authResponseModel.jwt;
      this.loggedIn = true;
      this.userModelInternal = authResponseModel.userDetails;
    }, (error: any) => console.log(error.error));
    return this.loggedIn;
  }

  /**
   * Used to determine if a user has authenticated. Returns true when user has authenticated, false otherwise.
   */
  public get authenticated(): boolean {
    return this.loggedIn;
  }

  public get userModel(): UserModel {
    return this.userModelInternal;
  }

  /**
   * Logs a user out of the site, destroying their token.
   */
  public logout() {
    this.apiInterceptor.jwt = '';   // Delete the JWT stored in memory.
    this.loggedIn = false;          // Set loggedIn to false.
  }
}
