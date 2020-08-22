package com.poker.poker.models.game;

import com.poker.poker.models.user.UserModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
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

  /**
   * Specifies whether a player is active.
   */
  @Schema(description = "Specifies whether a player is active.", example = "true")
  protected boolean away = false;

  /**
   * Specifies whether a player is out of the game.
   */
  @Schema(description = "Specifies whether a player is out of the game.", example = "false")
  protected boolean out = false;

  /**
   * Cards.
   */
  @Schema(description = "Cards")
  protected List<CardModel> cards = new ArrayList<>();

  /**
   * Player controls.
   */
  @Schema(implementation = TableControlsModel.class)
  protected TableControlsModel controls = new TableControlsModel();

  /**
   * Player is no longer in the hand when this is true.
   */
  @Schema(description = "Player is no longer in the hand when this is true.", example = "false")
  protected boolean folded = false;

  /**
   * Player bet entire bankroll.
   */
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

  /**
   * Getter for player's current bank roll which bypasses the need to get this through the
   * <code>controls</code> field.
   *
   * @return Players current number of chips (bank roll).
   */
  public BigDecimal getChips() {
    return controls.getBankRoll();
  }

  /**
   * Setter for player's current bank roll which bypasses the need to set this through the
   * <code>controls</code> field.
   *
   * @param value The value that the player's bank roll should be set to.
   */
  public void setChips(final BigDecimal value) {
    controls.setBankRoll(value);
  }

  /**
   * Helper to add chips to the player's bank roll, which bypasses the need to do this through the
   * <code>controls</code> field.
   *
   * @param value The value that the player's bank roll should be increased by.
   */
  public void addChips(final BigDecimal value) {
    controls.setBankRoll(controls.getBankRoll().add(value));
  }

  /**
   * Helper to remove chips from the player's bank roll, which bypasses the need to do this through
   * the <code>controls</code> field.
   *
   * @param value The value that the player's bank roll should be decreased by.
   */
  public void removeChips(final BigDecimal value) {
    controls.setBankRoll(controls.getBankRoll().subtract(value));
  }

  /**
   * Getter for player's current wager which bypasses the need to get this through the
   * <code>controls</code> field.
   *
   * @return Current wager in a hand.
   */
  public BigDecimal getBet() {
    return controls.getCurrentBet();
  }

  /**
   * Setter for player's current wager which bypasses the need to set this through the
   * <code>controls</code> field.
   *
   * @param value The value that the player's current wager should be set to.
   */
  public void setBet(final BigDecimal value) {
    controls.setCurrentBet(value);
  }

  /**
   * Getter for player's to call amount which bypasses the need to get this through the
   * <code>controls</code> field.
   *
   * @return Amount required to call in a hand.
   */
  public BigDecimal getToCall() {
    return controls.getToCall();
  }

  /**
   * Setter for player's to call amount which bypasses the need to set this through the
   * <code>controls</code> field.
   *
   * @param value The value that the player's to call amount should be set to.
   */
  public void setToCall(final BigDecimal value) {
    controls.setToCall(value);
  }
}
