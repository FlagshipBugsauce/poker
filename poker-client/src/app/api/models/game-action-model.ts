/* tslint:disable */
export interface GameActionModel {
  actionType?: 'Fold' | 'Check' | 'AllInCheck' | 'Call' | 'Raise';
  playerId?: string;
  raise?: number;
}
