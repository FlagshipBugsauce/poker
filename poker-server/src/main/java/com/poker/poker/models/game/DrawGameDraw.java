package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Model representing data in the draw game.")
public class DrawGameDraw {

  @Schema(description = "Card that was drawn.", implementation = Card.class)
  private Card card;

  @Schema(description = "Flag that is true if this draw won the hand.", example = "true")
  private boolean winner;
}
