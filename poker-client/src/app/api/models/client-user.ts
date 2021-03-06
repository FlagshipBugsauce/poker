/* tslint:disable */

/**
 * The user's information.
 */
export interface ClientUser {

  /**
   * User's email address.
   */
  email?: string;

  /**
   * User's first name.
   */
  firstName?: string;

  /**
   * User's user group.
   */
  group?: 'Administrator' | 'Client' | 'Guest';

  /**
   * User's ID.
   */
  id?: string;

  /**
   * User's last name.
   */
  lastName?: string;
}
