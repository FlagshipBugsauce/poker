package com.poker.poker.models.game;

import com.poker.poker.models.enums.GameAction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameActionModel {
  @Schema(
      description = "The unique id of the client",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543")
  private UUID userID;

  @Schema(
      description = "An action that pertains to a poker game",
      example = "Fold")
  private GameAction gameAction;
}
