package com.poker.poker.models.game;

import com.poker.poker.models.enums.CardSuit;
import com.poker.poker.models.enums.CardValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardModel {
  @Schema(
      description = "The suit of the card.",
      example = "Spades",
      implementation = CardSuit.class)
  private CardSuit suit;

  @Schema(
      description = "The value of the card.",
      example = "Ace",
      implementation = CardValue.class)
  private CardValue value;
}
