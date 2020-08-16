package com.poker.poker.models.game;

import java.util.List;
import lombok.Data;

@Data
public class HandRankModel implements Comparable<HandRankModel> {

  private final int rank;
  private final List<CardModel> hand;

  @Override
  public int compareTo(final HandRankModel o) {
    return Integer.compare(rank, o.getRank());
  }
}
