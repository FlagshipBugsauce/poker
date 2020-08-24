/* tslint:disable */
export interface GameActionData {
  actionType?: 'Fold' | 'Check' | 'AllInCheck' | 'Call' | 'Raise';
  playerId?: string;
  raise?: number;
}
