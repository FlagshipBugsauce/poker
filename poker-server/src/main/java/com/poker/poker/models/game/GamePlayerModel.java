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

  /** Specifies whether a player is active. */
  @Schema(description = "Specifies whether a player is active.", example = "true")
  protected boolean away = false;

  /** Specifies whether a player is out of the game. */
  @Schema(description = "Specifies whether a player is out of the game.", example = "false")
  protected boolean out = false;

  /** Cards. */
  @Schema(description = "Cards")
  protected List<CardModel> cards = new ArrayList<>();

  /** Player controls. */
  @Schema(implementation = TableControlsModel.class)
  protected TableControlsModel controls = new TableControlsModel();

  /** Player is no longer in the hand when this is true. */
  @Schema(description = "Player is no longer in the hand when this is true.", example = "false")
  protected boolean folded = false;

  /** Player bet entire bankroll. */
  @Schema(description = "Player bet entire bankroll.", example = "false")
  protected boolean allIn = false;

  public GamePlayerModel(final PlayerModel playerModel) {
    super(playerModel);
    away = false;
    out = false;
    folded = false;
    controls = new TableControlsModel();
    cards = new ArrayList<>();
    allIn = false;
  }

  public GamePlayerModel(final GamePlayerModel player) {
    super(player);
    away = player.isAway();
    out = player.isOut();
    folded = player.isFolded();
    allIn = player.isAllIn();
    controls = player.getControls();
    cards =
        player.getCards().stream()
            .map(c -> new CardModel(c.getSuit(), c.getValue()))
            .collect(Collectors.toList());
  }

  public GamePlayerModel(final UserModel userModel) {
    super(userModel);
  }
}
