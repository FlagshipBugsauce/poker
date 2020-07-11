package com.poker.poker.models.game;

import com.poker.poker.models.enums.GamePhase;
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
@Schema(description = "Information that defines the game state, such as phase, players, etc...")
public class GameModel {

  /** The game ID is the same as the lobby ID. */
  @Schema(
      description = "Game ID (same as game lobby ID).",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = UUID.class)
  @Id
  private UUID id;

  @Schema(description = "Game phase.", example = "Lobby", implementation = GamePhase.class)
  private GamePhase phase;

  /**
   * This list of player ID's will only be updated after the game begins.
   */
  @ArraySchema(schema = @Schema(implementation = GamePlayerModel.class))
  private List<GamePlayerModel> players;

  /**
   * List of hand IDs, up to and including the current hand.
   */
  @ArraySchema(schema = @Schema(implementation = UUID.class))
  private List<UUID> hands;

  /**
   * The total number of hands that will be played in the game.
   */
  @Schema(description = "Total number of hands in the game.", example = "5")
  private int totalHands;

  /**
   * Amount of time each player has to act.
   */
  @Schema(description = "Amount of time each player has to act.", example = "17")
  private int timeToAct;
}
