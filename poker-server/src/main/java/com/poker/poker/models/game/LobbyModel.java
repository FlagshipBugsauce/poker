package com.poker.poker.models.game;

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
@Document(collection = "lobby")
@Schema(description = "Game lobby containing information such as game parameters, players, etc...")
public class LobbyModel {

  /**
   * ID of the lobby (this ID is the same as the game ID).
   */
  @Schema(
      description = "Lobby's ID.",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = UUID.class)
  @Id
  private UUID id;

  /**
   * Player who hosted the game.
   */
  @Schema(implementation = LobbyPlayerModel.class, description = "Player who hosted the game.")
  private LobbyPlayerModel host;

  /**
   * Game parameters.
   */
  @Schema(implementation = GameParameterModel.class, description = "Game parameters.")
  private GameParameterModel parameters;

  /**
   * List of players currently in the lobby.
   */
  @ArraySchema(schema = @Schema(implementation = LobbyPlayerModel.class))
  private List<LobbyPlayerModel> players;
}
