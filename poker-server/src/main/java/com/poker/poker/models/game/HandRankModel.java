package com.poker.poker.models.game;

import java.util.List;
import lombok.Data;

@Data
public class HandRankModel implements Comparable<HandRankModel> {

  /**
   * Numerical rank of the hand (higher means better hand).
   */
  private final int rank;

  /**
   * Hand associated with the rank.
   */
  private final List<CardModel> hand;

  @Override
  public int compareTo(final HandRankModel o) {
    return Integer.compare(rank, o.getRank());
  }
}
