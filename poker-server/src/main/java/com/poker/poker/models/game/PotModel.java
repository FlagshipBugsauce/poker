package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Side-pot of a poker game. A side-pot can be created when a player goes all-in followed by some
 * other player raising. It's possible to have
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Side-pot of a poker game.")
public class PotModel {

  /** The maximum wager in this side-pot. */
  @Schema(description = "The maximum wager in this side-pot.", implementation = BigDecimal.class)
  private BigDecimal wager = BigDecimal.ZERO;

  /** The total amount in this side-pot. */
  @Schema(description = "The total amount in this side-pot.", implementation = BigDecimal.class)
  private BigDecimal total = BigDecimal.ZERO;

  /**
   * Increases the total in the pot by the specified amount.
   *
   * @param amount The amount the total will by increased by.
   */
  public void increaseTotal(final BigDecimal amount) {
    total = total.add(amount);
  }
}
