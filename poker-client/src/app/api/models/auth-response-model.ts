/* tslint:disable */
import {UserModel} from './user-model';

export interface AuthResponseModel {

  /**
   * JSON Web Token that can be used to access secured endpoints.
   */
  jwt?: string;
  userDetails?: UserModel;
}
