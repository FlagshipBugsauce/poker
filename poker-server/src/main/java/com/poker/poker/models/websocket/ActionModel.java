package com.poker.poker.models.websocket;

import com.poker.poker.models.enums.GameAction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Action model.")
public class ActionModel {

  @Schema(description = "ID of the user who performed an action.", implementation = UUID.class)
  private UUID userId;

  @Schema(
      description = "JWT of the user who performed an action (if security is required).",
      implementation = UUID.class)
  private String jwt;

  @Schema(description = "The type of action performed.", implementation = GameAction.class)
  private GameAction actionType;
}
