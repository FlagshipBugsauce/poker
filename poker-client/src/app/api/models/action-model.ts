/* tslint:disable */

/**
 * Action model.
 */
export interface ActionModel {

  /**
   * The type of action performed.
   */
  actionType?: 'Fold' | 'Check' | 'Raise' | 'Call' | 'Bet' | 'ReRaise' | 'Ready' | 'Join' | 'LeaveLobby' | 'Start' | 'LeaveGame' | 'ReJoinGame';

  /**
   * JWT of the user who performed an action (if security is required).
   */
  jwt?: string;

  /**
   * ID of the user who performed an action.
   */
  userId?: string;
}
