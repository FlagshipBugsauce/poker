package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetGameModel {
  @Schema(description = "The ID of the game.", example = "0a7d95ef-94ba-47bc-b591-febb365bc543")
  private UUID id;

  @Schema(description = "The name of the game.", example = "Friends Night Out Poker")
  private String name;

  @Schema(
      description = "The ID of the host.",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = PlayerModel.class
  )
  private PlayerModel host;

  @Schema(description = "The current number of players in the game", example = "4")
  private int currentPlayers;

  @Schema(description = "The maximum number of players allowed in the game", example = "8")
  private int maxPlayers;

  @Schema(description = "The buy-in required to play.", example = "400.00")
  private BigDecimal buyIn;
}
