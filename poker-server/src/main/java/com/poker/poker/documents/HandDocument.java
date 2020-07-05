package com.poker.poker.documents;

import com.poker.poker.models.game.CardModel;
import com.poker.poker.models.game.GamePlayerModel;
import com.poker.poker.models.game.hand.HandActionModel;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "hands")
public class HandDocument {

  @Schema(
      description = "Hand ID.",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = UUID.class)
  @Id
  private UUID id;

  @Schema(
      description = "Game ID.",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = UUID.class)
  private UUID gameId;

  // Temporary
  @Schema(
      description = "Temporary message.",
      example = "Player X won.",
      implementation = String.class)
  private String message;

  @ArraySchema(schema = @Schema(implementation = HandActionModel.class))
  private List<HandActionModel> actions;

  @Schema(implementation = GamePlayerModel.class)
  private GamePlayerModel playerToAct;

  // TODO: Remove once we evolve past the card drawing game.
  @ArraySchema(schema = @Schema(implementation = CardModel.class))
  private List<CardModel> drawnCards;
}
