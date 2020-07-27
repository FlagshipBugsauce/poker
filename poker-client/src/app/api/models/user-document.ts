/* tslint:disable */
export interface UserDocument {
  email?: string;
  firstName?: string;
  group?: 'Administrator' | 'Client' | 'Guest';
  id?: string;
  lastName?: string;
  password?: string;
}
