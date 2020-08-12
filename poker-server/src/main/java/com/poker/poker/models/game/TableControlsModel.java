package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Model containing fields needed by the UI's game controls component.")
public class TableControlsModel {

  @Schema(description = "Size of the players bank roll.", implementation = BigDecimal.class)
  private BigDecimal bankRoll = BigDecimal.ZERO;

  @Schema(
      description = "Amount invested into current round of betting.",
      implementation = BigDecimal.class)
  private BigDecimal currentBet = BigDecimal.ZERO;

  @Schema(
      description = "Amount required for the player to call.",
      implementation = BigDecimal.class)
  private BigDecimal toCall = BigDecimal.ZERO;

  /**
   * Constructor that initializes the bankRoll field only. Used at the start of a new round, as this
   * will effectively reset the currentBet and toCall fields, while maintaining the bankRoll field.
   *
   * @param bankRoll Player's remaining chips/bank roll.
   */
  public TableControlsModel(final BigDecimal bankRoll) {
    this.bankRoll = bankRoll;
  }
}
