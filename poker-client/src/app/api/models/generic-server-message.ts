/* tslint:disable */
export interface GenericServerMessage {
  data?: {};
  type?: 'Debug' | 'GameList' | 'Lobby' | 'Game' | 'Hand' | 'GameData' | 'PlayerData' | 'Toast' | 'ReadyToggled' | 'PlayerJoinedLobby' | 'PlayerLeftLobby' | 'GamePhaseChanged' | 'HandStarted' | 'PlayerAwayToggled' | 'HandActionPerformed' | 'ActingPlayerChanged' | 'GamePlayer' | 'CardDrawnByPlayer' | 'StartTurnTimer' | 'PokerTable' | 'Deal' | 'Timer' | 'HideCards' | 'Cards';
}
