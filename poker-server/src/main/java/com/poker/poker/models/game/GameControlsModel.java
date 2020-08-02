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
public class GameControlsModel {

  @Schema(description = "Size of the players bank roll.", implementation = BigDecimal.class)
  private BigDecimal bankRoll;

  @Schema(
      description = "Amount invested into current round of betting.",
      implementation = BigDecimal.class)
  private BigDecimal currentBet;

  @Schema(
      description = "Amount required for the player to call.",
      implementation = BigDecimal.class)
  private BigDecimal toCall;
}
