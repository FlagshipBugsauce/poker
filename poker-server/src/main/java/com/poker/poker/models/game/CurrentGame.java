package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Contains information regarding the game a player is currently in.")
public class CurrentGame {

  /** Flag that represents whether a player is currently in a game. */
  @Schema(
      description = "Flag that represents whether a player is currently in a game.",
      example = "true")
  private boolean inGame;

  /** ID of the game a player is in, if the player is in a game. */
  @Schema(
      description = "ID of the game a player is in, if the player is in a game.",
      implementation = UUID.class)
  private UUID id;
}
