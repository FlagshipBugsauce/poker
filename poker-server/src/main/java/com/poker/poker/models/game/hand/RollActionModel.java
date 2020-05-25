package com.poker.poker.models.game.hand;

import com.poker.poker.models.game.GamePlayerModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RollActionModel extends HandActionModel {

  @Schema(description = "Player that performed the roll.", implementation = GamePlayerModel.class)
  protected GamePlayerModel player;

  @Schema(description = "Value that was rolled.", example = "27")
  protected int value;

  public RollActionModel(UUID id, String message, GamePlayerModel player, int value) {
    this.id = id;
    this.message = message;
    this.player = player;
    this.value = value;
  }
}
