package com.poker.poker.models.game;

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
public class HandModel {

  /**
   * The ID of the hand.
   */
  @Schema(
      description = "Hand ID.",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = UUID.class)
  @Id
  private UUID id;

  /**
   * The ID of the game the hand was played/is being played in.
   */
  @Schema(
      description = "Game ID.",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = UUID.class)
  private UUID gameId;

  /**
   * List of actions that occurred in the hand.
   */
  @ArraySchema(schema = @Schema(implementation = HandActionModel.class))
  private List<HandActionModel> actions;

  /** Model of the player whose turn it is to act. */
  @Schema(implementation = GamePlayerModel.class)
  private GamePlayerModel acting;
}
