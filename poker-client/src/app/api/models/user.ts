/* tslint:disable */
export interface User {
  email?: string;
  firstName?: string;
  group?: 'Administrator' | 'Client' | 'Guest';
  id?: string;
  lastName?: string;
  password?: string;
}
