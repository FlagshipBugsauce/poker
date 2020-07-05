package com.poker.poker.models.game.hand;

import com.poker.poker.models.enums.HandAction;
import com.poker.poker.models.game.CardModel;
import com.poker.poker.models.game.GamePlayerModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandActionModel {

  @Schema(
      description = "Hand Action ID.",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = UUID.class)
  @Id
  private UUID id;

  @Schema(description = "Type of action performed.", implementation = HandAction.class)
  private HandAction type;

  @Schema(
      description = "Message related to action which was performed.",
      example = "Player X rolled 27.",
      implementation = String.class)
  private String message;

  @Schema(description = "Player that performed the roll.", implementation = GamePlayerModel.class)
  private GamePlayerModel player;

  /** For roll events only. */
  @Schema(description = "Value that was rolled.", example = "27")
  private int value;

  /** For draw events only. */
  @Schema(description = "Card that was drawn.", implementation = CardModel.class)
  private CardModel drawnCard;
}
