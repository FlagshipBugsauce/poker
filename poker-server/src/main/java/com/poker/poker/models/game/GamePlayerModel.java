package com.poker.poker.models.game;

import com.poker.poker.models.user.UserModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

  @Schema(description = "Specifies whether a player is active.", example = "true")
  protected boolean away;

  @Schema(description = "Specifies whether a player is out of the game.", example = "false")
  protected boolean out;

  @Schema(description = "Cards")
  protected List<CardModel> cards;

  @Schema(implementation = TableControlsModel.class)
  protected TableControlsModel controls;

  @Schema(description = "Player is no longer in the hand.", example = "false")
  protected boolean folded;

  public GamePlayerModel(final PlayerModel playerModel) {
    super(playerModel);
    away = false;
    out = false;
    controls = new TableControlsModel();
    cards = new ArrayList<>();
  }

  public GamePlayerModel(final GamePlayerModel player) {
    super(player);
    away = player.away;
    out = false;
    controls = player.controls;
    cards =
        player.cards.stream()
            .map(c -> new CardModel(c.getSuit(), c.getValue()))
            .collect(Collectors.toList());
  }

  public GamePlayerModel(final UserModel userModel) {
    super(userModel);
  }
}
