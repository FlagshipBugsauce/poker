/* tslint:disable */

/**
 * Message sent to chat.
 */
export interface ChatMessage {

  /**
   * Author of the message. Null if the message was sent by the system.
   */
  author?: string;

  /**
   * Message
   */
  message?: string;

  /**
   * Time the message was sent.
   */
  timestamp?: string;
}
