package com.poker.poker.models.game;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PokerTableModel {

  /** List of players at the table. Contains player names, bank roll, score, etc... */
  private List<GamePlayerModel> players;

  /** Position of the player who is acting. */
  private int actingPlayer;

  private int playerThatActed;

  /** Position of the dealer. */
  private int dealer;

  /** Summary of winning hand is display */
  private boolean displayHandSummary;

  /**
   * This will be incremented whenever a player's turn begins so that the client knows when to begin
   * the turn timer.
   */
  private int startTurnTimer;

  public void startTurnTimer() {
    startTurnTimer++;
  }

  public void playedActed() {
    playerThatActed = (playerThatActed + 1) % players.size();
  }
}
