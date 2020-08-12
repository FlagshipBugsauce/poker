package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A winner of a hand, could be one of several.")
public class WinnerModel {

  /**
   * The ID of the winning player.
   */
  @Schema(description = "The ID of the winning player.")
  private UUID id;

  /**
   * The amount the player won.
   */
  @Schema(description = "The amount the player won.")
  private BigDecimal winnings;

  /**
   * The winning cards.
   */
  @ArraySchema(schema = @Schema(
      description = "The winning cards.", implementation = CardModel.class))
  private List<CardModel> cards;

  public void increaseWinnings(final BigDecimal amount) {
    winnings = winnings.add(amount);
  }
}