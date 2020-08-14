package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Deal model that is sent to the client to trigger a deal event.")
public class DealModel {

  /** ID of the deal event. */
  @Schema(description = "ID of the deal event.", implementation = UUID.class)
  private UUID id;

  /** Number of cards to deal. */
  @Schema(description = "Number of cards to deal.", example = "2")
  private int numCards;

  public DealModel() {
    id = UUID.randomUUID();
  }
}
