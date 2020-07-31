package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Hand summary.")
public class HandSummaryModel {

  /**
   * The winning card.
   */
  private CardModel card;

  /**
   * Index of the winning player in the player list in the game model and poker table model.
   */
  private int winner;
}
