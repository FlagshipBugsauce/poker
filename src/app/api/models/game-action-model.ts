/* tslint:disable */
import { PlayerModel } from './player-model';
export interface GameActionModel {

  /**
   * Message that will be displayed somewhere in the client when this action occurs. Could be null.
   */
  clientMessage?: string;

  /**
   * The action that occurred.
   */
  gameAction?: 'Fold' | 'Check' | 'Raise' | 'Call' | 'Bet' | 'ReRaise' | 'Ready' | 'Join' | 'Leave' | 'Start';

  /**
   * ID of the action performed which identifies it.
   */
  id?: string;
  player?: PlayerModel;
}
