package com.poker.poker.models.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "The parameters of a game.")
public class GameParameter {

  /**
   * The name of the game.
   */
  @NotNull(message = "Game name cannot be null.")
  @NotEmpty(message = "Game name cannot be empty.")
  @Schema(description = "The name of the game to be created", example = "Friends Night Out Poker")
  @JsonProperty(required = true)
  private String name;

  /**
   * The maximum number of players that can join the game.
   */
  @NotNull(message = "Max players must be specified.")
  @Min(value = 2, message = "Must have at least 2 players in a game.")
  @Max(value = 10, message = "Cannot have more than 10 players in a game.")
  @Schema(description = "The maximum number of players allowed in the game", example = "8")
  @JsonProperty(required = true)
  private int maxPlayers;

  /**
   * The amount of money required to play the game.
   */
  @NotNull(message = "Buy-in must be specified.")
  @Min(value = 0, message = "Buy-in must be a positive value.")
  @Schema(description = "Buy-in required to play in the game.", example = "25")
  @JsonProperty(required = true)
  private BigDecimal buyIn;

  /**
   * The value of the small blind when the game starts.
   */
  @Min(value = 0)
  @Schema(description = "The value of the small blind when the game starts.", example = "50")
  private BigDecimal startingBlinds;

  /**
   * Amount of time (in seconds) players have to act when it is their turn.
   */
  @Min(value = 0)
  @Schema(
      description = "Amount of time (in seconds) players have to act when it is their turn.",
      example = "25")
  private int turnDuration;
}
