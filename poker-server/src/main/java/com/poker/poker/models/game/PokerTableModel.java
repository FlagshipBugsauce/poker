package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PokerTableModel {

  /**
   * List of players at the table. Contains player names, bank roll, score, etc...
   */
  @ArraySchema(schema = @Schema(implementation = GamePlayerModel.class))
  private List<GamePlayerModel> players;

  /**
   * Position of the player who is acting.
   */
  @Schema(description = "Position of the player who is acting.", example = "3")
  private int actingPlayer = 0;

  @Schema(description = "Position of the player that acted.", example = "3")
  private int playerThatActed = -1;

  /**
   * Position of the dealer.
   */
  @Schema(description = "Position of the dealer.", example = "3")
  private int dealer = 0;

  /**
   * Flag to determine whether the summary of winning hand should be displayed.
   */
  @Schema(
      description = "Flag to determine whether the summary of winning hand should be displayed.",
      example = "true")
  private boolean displayHandSummary = false;

  /**
   * Hand summary.
   */
  @Schema(implementation = HandSummaryModel.class)
  private HandSummaryModel summary = null;

  /**
   * This is incremented whenever some action is performed.
   */
  @Schema(description = "This is incremented whenever some action is performed.", example = "69")
  private int eventTracker = 0;

  /**
   * Minimum raise amount.
   */
  @Schema(description = "Minimum raise amount.", example = "69", implementation = BigDecimal.class)
  private BigDecimal minRaise;

  /**
   * Total amount in the pot.
   */
  @Schema(
      description = "Total amount in the pot.",
      example = "420.69",
      implementation = BigDecimal.class)
  private BigDecimal pot;

  /**
   * The round will end once this player has acted.
   */
  @Schema(description = "The round will end once this player has acted.", example = "2")
  private int lastToAct;

  public void playerActed() {
    incActingPlayer();
    incPlayerThatActed();
    actionPerformed();
  }

  public void actionPerformed() {
    eventTracker++;
  }

  public void incPlayerThatActed() {
    playerThatActed = (playerThatActed + 1) % players.size();
  }

  public void incActingPlayer() {
    actingPlayer = (actingPlayer + 1) % players.size();
  }
}
