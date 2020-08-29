package com.poker.poker.models.game;

import com.poker.poker.models.enums.HandType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
@Schema(description = "Contains a hand with it's corresponding rank.")
public class HandRank implements Comparable<HandRank> {

  /** Numerical rank of the hand (higher means better hand). */
  @Schema(description = "Hand rank.", example = "42069")
  private final int rank;
  /** Hand associated with the rank. */
  @ArraySchema(schema = @Schema(implementation = Card.class))
  private final List<Card> hand;
  /** Player's ID. */
  @Schema(description = "Player's ID.")
  private UUID id;

  /** Type of hand. */
  @Schema(description = "Type of hand.", example = "Full House", implementation = HandType.class)
  private final HandType type;

  @Override
  public int compareTo(final HandRank o) {
    return Integer.compare(rank, o.getRank());
  }
}
