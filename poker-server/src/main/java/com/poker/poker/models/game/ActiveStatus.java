package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class ActiveStatus {

  @Schema(
      description = "Flag to determine what to set the player's active status to.",
      example = "true")
  @NotNull
  private boolean away;
}
