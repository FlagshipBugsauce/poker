package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGameModel {
  @Schema(
      description = "The name of the game to be created",
      example = "Friends Night Out Poker")
  private String name;

  @Schema(
      description = "The maximum number of players allowed in the game",
      example = "8")
  private int maxPlayers;

  @Schema(
      description = "The minimum amount of money required to sit down in a particular poker game",
      example = "400.00")
  private BigDecimal buyIn;
}
