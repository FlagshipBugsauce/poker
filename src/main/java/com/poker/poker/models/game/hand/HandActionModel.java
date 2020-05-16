package com.poker.poker.models.game.hand;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class HandActionModel {

  @Schema(
      description = "Hand Action ID.",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = UUID.class)
  @Id
  protected UUID id;

  @Schema(
      description = "Message related to action which was performed.",
      example = "Player X rolled 27.",
      implementation = String.class)
  protected String message;
}
