package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Container for a list of game data, plus some other useful information.")
public class DrawGameDataContainer {

  @ArraySchema(schema = @Schema(implementation = DrawGameData.class))
  private List<DrawGameData> gameData;
}
