package com.poker.poker.models.websocket;

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
}
