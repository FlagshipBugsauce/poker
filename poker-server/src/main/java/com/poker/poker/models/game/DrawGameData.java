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
@Schema(description = "Model representing player data in a game, i.e. cards drawn, etc...")
public class DrawGameData {

  @Schema(implementation = GamePlayer.class)
  private GamePlayer player;

  @Schema(description = "Flag that is true if this draw is next.", example = "true")
  private boolean acting;

  @ArraySchema(schema = @Schema(implementation = DrawGameDraw.class))
  private List<DrawGameDraw> draws;
}
