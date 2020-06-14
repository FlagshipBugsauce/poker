package com.poker.poker.models.game;

import com.poker.poker.documents.UserDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a player after the game has started. Contains several extra fields required for the
 * this stage of the game, such as the players score.
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Model representing a player in a game.")
public class GamePlayerModel extends PlayerModel {

  @Schema(description = "The players current score.", example = "69")
  protected int score = 0;

  @Schema(description = "Specifies whether a player is active.", example = "true")
  protected boolean active = true;

  public GamePlayerModel(PlayerModel playerModel) {
    super(playerModel);
  }

  public GamePlayerModel(UserDocument userDocument) {
    super(userDocument);
  }
}
