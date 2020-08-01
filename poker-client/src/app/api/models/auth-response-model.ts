/* tslint:disable */
import {ClientUserModel} from './client-user-model';

export interface AuthResponseModel {

  /**
   * JSON Web Token that can be used to access secured endpoints.
   */
  jwt?: string;
  userDetails?: ClientUserModel;
}
