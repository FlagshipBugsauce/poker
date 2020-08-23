/* tslint:disable */

/**
 * Generic model used to send data to the server.
 */
export interface ClientMessage {

  /**
   * Data being sent by the client.
   */
  data?: {};

  /**
   * Optional field to identify the game a user is in.
   */
  gameId?: string;

  /**
   * Optional field when user's identity needs to be verified in a secure fashion.
   */
  jwt?: string;

  /**
   * Optional field to identify a user when security is not important.
   */
  userId?: string;
}
