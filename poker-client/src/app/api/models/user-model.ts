/* tslint:disable */
export interface UserModel {
  email?: string;
  firstName?: string;
  group?: 'Administrator' | 'Client' | 'Guest';
  id?: string;
  lastName?: string;
  password?: string;
}
