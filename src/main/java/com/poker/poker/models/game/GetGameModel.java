package com.poker.poker.models.game;

import com.poker.poker.models.enums.GameState;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// subset of fields from Game Document
public class GetGameModel {
  @Schema(
      description = "The name of the current game",
      example = "Friends Night Out Poker")
  private String name;

  @Schema(
      description = "The unique id of the host of the game",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543")
  private UUID host;

  @Schema(
      description = "The current number of players in the game",
      example = "4")
  private int currentPlayers;

  @Schema(
      description = "The maximum number of players allowed in the game",
      example = "8")
  private int maxPlayers;

  @Schema(
      description = "The minimum amount of money required to sit down in a particular poker game",
      example = "400.00")
  private BigDecimal buyIn;
}
