package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
@Schema(description = "Contains a hand with it's corresponding rank.")
public class HandRankModel implements Comparable<HandRankModel> {

  /** Numerical rank of the hand (higher means better hand). */
  @Schema(description = "Hand rank.", example = "42069")
  private final int rank;
  /** Hand associated with the rank. */
  @ArraySchema(schema = @Schema(implementation = Card.class))
  private final List<Card> hand;
  /** Player's ID. */
  @Schema(description = "Player's ID.")
  private UUID id;

  @Override
  public int compareTo(final HandRankModel o) {
    return Integer.compare(rank, o.getRank());
  }
}
