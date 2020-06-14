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
public class DrawGameDataContainerModel {
  @ArraySchema(schema = @Schema(implementation = DrawGameDataModel.class))
  private List<DrawGameDataModel> gameData;

  @Schema(description = "The current hand.", example = "3")
  private int currentHand;

  public void incrementHand(final int totalHands) {
    currentHand += currentHand < totalHands ? 1 : 0;
  }
}
