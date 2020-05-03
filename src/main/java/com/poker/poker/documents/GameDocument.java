package com.poker.poker.documents;

import com.poker.poker.models.enums.GameState;
import com.poker.poker.models.game.GameActionModel;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
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
@Document(collection = "games")
public class GameDocument {
  @Schema(description = "Game's ID.", example = "0a7d95ef-94ba-47bc-b591-febb365bc543")
  @Id
  private UUID id;

  @Schema(description = "Host's ID.", example = "0a7d95ef-94ba-47bc-b591-febb365bc543")
  private UUID host;

  @Schema(description = "Name of the game.", example = "All night poker with Jimmy")
  private String name;

  @Schema(description = "Maximum number of players allowed in the game.", example = "10")
  private int maxPlayers;

  @Schema(description = "Buy-in required to play in the game.", example = "$25")
  private BigDecimal buyIn;

  @ArraySchema(schema = @Schema(implementation = UUID.class))
  private List<UUID> playerIds;

  @ArraySchema(schema = @Schema(implementation = GameActionModel.class))
  private List<GameActionModel> gameActions;

  @Schema(description = "Current state of the game.", example = "PreGame")
  private GameState currentGameState;
}
