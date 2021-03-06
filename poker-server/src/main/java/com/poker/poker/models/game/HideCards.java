package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HideCards {

  /** ID of the hide cards event. */
  @Schema(description = "ID of the deal event.", implementation = UUID.class)
  private UUID id;

  public HideCards() {
    id = UUID.randomUUID();
  }
}
