/* tslint:disable */
import {ClientUser} from './client-user';

export interface AuthResponse {

  /**
   * JSON Web Token that can be used to access secured endpoints.
   */
  jwt?: string;
  userDetails?: ClientUser;
}
