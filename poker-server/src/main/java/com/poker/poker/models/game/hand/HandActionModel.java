package com.poker.poker.models.game.hand;

import com.poker.poker.models.enums.HandAction;
import com.poker.poker.models.game.CardModel;
import com.poker.poker.models.game.GamePlayerModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandActionModel {

  @Schema(description = "Type of action performed.", implementation = HandAction.class)
  private HandAction type;

  @Schema(description = "Player that performed the action.", implementation = GamePlayerModel.class)
  private GamePlayerModel player;

  /** For draw events only. */
  @Schema(description = "Card that was drawn.", implementation = CardModel.class)
  private CardModel drawnCard;
}
