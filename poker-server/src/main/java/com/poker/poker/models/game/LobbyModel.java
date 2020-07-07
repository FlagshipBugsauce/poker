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
public class LobbyModel {

  @Schema(
      description = "Lobby's ID.",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = UUID.class)
  @Id
  private UUID id;

  @Schema(implementation = LobbyPlayerModel.class)
  private LobbyPlayerModel host;

  @Schema(implementation = GameParameterModel.class)
  private GameParameterModel parameters;

  @ArraySchema(schema = @Schema(implementation = LobbyPlayerModel.class))
  private List<LobbyPlayerModel> players;
}
