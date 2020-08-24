/* tslint:disable */
import {ToastClass} from './toast-class';

/**
 * Contains the message to display and the duration of the toast.
 */
export interface Toast {

  /**
   * The message to display.
   */
  message?: string;
  options?: ToastClass;
}
