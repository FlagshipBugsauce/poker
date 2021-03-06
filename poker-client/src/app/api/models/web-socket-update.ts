/* tslint:disable */

/**
 * Contains fields needed to determine what topic to broadcast to and what data to broadcast.
 */
export interface WebSocketUpdate {

  /**
   * ID component of the topic.
   */
  id?: string;

  /**
   * Topic to broadcast to.
   */
  topic?: string;

  /**
   * Type of message.
   */
  type?: 'Debug' | 'GameList' | 'Lobby' | 'Game' | 'Hand' | 'GameData' | 'PlayerData' | 'Toast' | 'ReadyToggled' | 'PlayerJoinedLobby' | 'PlayerLeftLobby' | 'GamePhaseChanged' | 'HandStarted' | 'PlayerAwayToggled' | 'HandActionPerformed' | 'ActingPlayerChanged' | 'GamePlayer' | 'CardDrawnByPlayer' | 'StartTurnTimer' | 'PokerTable' | 'Deal' | 'Timer' | 'HideCards' | 'Cards';
}
