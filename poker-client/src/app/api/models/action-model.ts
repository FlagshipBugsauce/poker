/* tslint:disable */

/**
 * Action model.
 */
export interface ActionModel {

  /**
   * JWT of the user who performed an action (if security is required).
   */
  jwt?: string;

  /**
   * ID of the user who performed an action.
   */
  userId?: string;
}
