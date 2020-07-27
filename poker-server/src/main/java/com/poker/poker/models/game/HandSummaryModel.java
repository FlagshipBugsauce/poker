package com.poker.poker.models.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandSummaryModel {

  private CardModel card;
  private int winner;
}
