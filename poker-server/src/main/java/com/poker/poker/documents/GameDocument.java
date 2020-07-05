package com.poker.poker.documents;

import com.poker.poker.models.GameSummaryModel;
import com.poker.poker.models.enums.GameState;
import com.poker.poker.models.game.GamePlayerModel;
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
@Document(collection = "game")
public class GameDocument {

  /**
   * The game ID is the same as the lobby ID.
   */
  @Schema(
      description = "Game ID (same as game lobby ID).",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = UUID.class)
  @Id
  private UUID id;

  @Schema(description = "Game state.", example = "Lobby", implementation = GameState.class)
  private GameState state;

  /**
   * This list of player ID's will only be updated after the game begins.
   */
  @ArraySchema(schema = @Schema(implementation = GamePlayerModel.class))
  private List<GamePlayerModel> players;

  @ArraySchema(schema = @Schema(implementation = UUID.class))
  private List<UUID> hands;

  @Schema(implementation = GameSummaryModel.class)
  private GameSummaryModel summary;

  @Schema(description = "Total number of hands in the game.", example = "5")
  private int totalHands;

  @Schema(description = "Amount of time each player has to act.", example = "17")
  private int timeToAct;
}
