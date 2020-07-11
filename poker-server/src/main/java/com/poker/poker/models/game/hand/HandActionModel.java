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
@Schema(description = "Action performed in a hand, such as draw, bet, etc...")
public class HandActionModel {

  /** Type of action, i.e. Draw, Bet, Check, Fold, etc... */
  @Schema(description = "Type of action performed.", implementation = HandAction.class)
  private HandAction type;

  /** Player who performed the action. */
  @Schema(description = "Player that performed the action.", implementation = GamePlayerModel.class)
  private GamePlayerModel player;

  // TODO: Should make this generic and call the field data or something.
  /** For draw events only. */
  @Schema(description = "Card that was drawn.", implementation = CardModel.class)
  private CardModel drawnCard;
}
