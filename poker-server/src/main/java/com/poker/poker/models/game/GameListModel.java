package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameListModel {

  @Schema(
      description = "The ID of the game.",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = UUID.class)
  private UUID id;

  @Schema(implementation = GameParameterModel.class)
  private GameParameterModel parameters;

  @Schema(
      description = "The ID of the host.",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = LobbyPlayerModel.class)
  private LobbyPlayerModel host;

  @Schema(
      description = "The current number of players in the game",
      example = "4",
      implementation = BigDecimal.class)
  private int currentPlayers;

}
