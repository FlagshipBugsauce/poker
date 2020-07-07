package com.poker.poker.models.game;

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
public class GameParameterModel {

  @NotNull(message = "Game name cannot be null.")
  @NotEmpty(message = "Game name cannot be empty.")
  @Schema(description = "The name of the game to be created", example = "Friends Night Out Poker")
  private String name;

  @NotNull(message = "Max players must be specified.")
  @Min(value = 2, message = "Must have at least 2 players in a game.")
  @Max(value = 10, message = "Cannot have more than 10 players in a game.")
  @Schema(description = "The maximum number of players allowed in the game", example = "8")
  private int maxPlayers;

  @NotNull(message = "Buy-in must be specified.")
  @Min(value = 0, message = "Buy-in must be a positive value.")
  @Schema(description = "Buy-in required to play in the game.", example = "25")
  private BigDecimal buyIn;
}
