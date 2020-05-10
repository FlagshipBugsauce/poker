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
      description = "ID of the action performed which identifies it.",
      implementation = UUID.class,
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543")
  private UUID id;

  @Schema(
      description = "Player model representing the player that performed the action.",
      implementation = PlayerModel.class)
  private PlayerModel player;

  @Schema(
      description = "The action that occurred.",
      example = "Fold",
      implementation = GameAction.class)
  private GameAction gameAction;

  @Schema(
      description =
          "Message that will be displayed somewhere in the client when this action "
              + "occurs. Could be null.",
      example = "Player 'FudgeNuts' has left the game.",
      implementation = String.class)
  private String clientMessage;
}
