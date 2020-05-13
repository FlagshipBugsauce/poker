package com.poker.poker.documents;

import com.poker.poker.models.game.GameActionModel;
import com.poker.poker.models.game.PlayerModel;
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
@Document(collection = "lobby")
public class LobbyDocument {

  @Schema(
      description = "Lobby's ID.",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = UUID.class)
  @Id
  private UUID id;

  @Schema(
      description = "Host's ID.",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = UUID.class)
  private UUID host;

  @Schema(
      description = "Name of the game.",
      example = "All night poker with Jimmy",
      implementation = String.class)
  private String name;

  @Schema(
      description = "Maximum number of players allowed in the game.",
      example = "10",
      implementation = String.class)
  private int maxPlayers;

  @Schema(
      description = "Buy-in required to play in the game.",
      example = "69",
      implementation = BigDecimal.class)
  private BigDecimal buyIn;

  @ArraySchema(schema = @Schema(implementation = PlayerModel.class))
  private List<PlayerModel> players;

  @ArraySchema(schema = @Schema(implementation = GameActionModel.class))
  private List<GameActionModel> gameActions; // TODO: Refactor to "LobbyActions"
}
