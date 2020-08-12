package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Side-pot of a poker game.")
public class PotModel {

  @Schema(description = "The maximum wager in this side-pot.", implementation = BigDecimal.class)
  private BigDecimal wager = BigDecimal.ZERO;

  @Schema(description = "The total amount in this side-pot.", implementation = BigDecimal.class)
  private BigDecimal total = BigDecimal.ZERO;

  public void increaseTotal(final BigDecimal value) {
    total = total.add(value);
  }

//  @Schema(description = "The number of wagers in this side-pot")
//  private int number = 0;
//
//  @ArraySchema(schema = @Schema(
//      description = "Collection of wagers.",
//      implementation = BigDecimal.class))
//  private List<BigDecimal> wagers = new ArrayList<>();
//
//  public void incrementNumber() {
//    number++;
//  }
//
//  public void decrementNumber() {
//    number--;
//  }
}
