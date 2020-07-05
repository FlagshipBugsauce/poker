/* tslint:disable */
import {ToastClassModel} from './toast-class-model';

/**
 * Contains the message to display and the duration of the toast.
 */
export interface ToastModel {

  /**
   * The message to display.
   */
  message?: string;
  options?: ToastClassModel;
}
