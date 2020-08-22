package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PokerTableModel {

  /**
   * List of players at the table. Contains player names, bank roll, score, etc...
   */
  @ArraySchema(schema = @Schema(implementation = GamePlayerModel.class))
  private List<GamePlayerModel> players;

  /**
   * Position of the player who is acting.
   */
  @Schema(description = "Position of the player who is acting.", example = "3")
  private int actingPlayer = 0;

  @Schema(description = "Position of the player that acted.", example = "3")
  private int playerThatActed = -1;

  /**
   * Position of the dealer.
   */
  @Schema(description = "Position of the dealer.", example = "3")
  private int dealer = 0;

  /**
   * Flag to determine whether the summary of winning hand should be displayed.
   */
  @Schema(
      description = "Flag to determine whether the summary of winning hand should be displayed.",
      example = "true")
  private boolean displayHandSummary = false;

  /**
   * Hand summary.
   */
  @Schema(implementation = HandSummaryModel.class)
  private HandSummaryModel summary = null;

  /**
   * Winners of the hand.
   */
  @ArraySchema(schema = @Schema(implementation = WinnerModel.class))
  private List<WinnerModel> winners = new ArrayList<>();

  /**
   * This is incremented whenever some action is performed.
   */
  @Schema(description = "This is incremented whenever some action is performed.", example = "69")
  private int eventTracker = 0;

  /**
   * Minimum raise amount.
   */
  @Schema(description = "Minimum raise amount.", example = "69", implementation = BigDecimal.class)
  private BigDecimal minRaise = BigDecimal.ZERO;

  /**
   * Total amount in the pot.
   */
  @Schema(
      description = "Total amount in the pot.",
      example = "420.69",
      implementation = BigDecimal.class)
  private BigDecimal pot = BigDecimal.ZERO;

  /**
   * Side-pots.
   */
  @ArraySchema(schema = @Schema(implementation = PotModel.class))
  private List<PotModel> pots = new ArrayList<>();

  /**
   * Blinds.
   */
  @Schema(description = "Blinds.", example = "69", implementation = BigDecimal.class)
  private BigDecimal blind = BigDecimal.ZERO;

  /**
   * Blinds.
   */
  @Schema(description = "Current round.", example = "69")
  private int round = 0;

  /**
   * The round will end once this player has acted.
   */
  @Schema(description = "The round will end once this player has acted.", example = "2")
  private int lastToAct = 0;

  /**
   * Flag that is true when a betting round is taking place.
   */
  @Schema(description = "Flag that is true when a betting round is taking place.", example = "true")
  private boolean betting = false;

  private List<CardModel> sharedCards = new ArrayList<>();

  public PokerTableModel(final PokerTableModel table) {
    players = table.getPlayers().stream().map(GamePlayerModel::new).collect(Collectors.toList());
    actingPlayer = table.getActingPlayer();
    playerThatActed = table.getPlayerThatActed();
    dealer = table.getDealer();
    displayHandSummary = table.isDisplayHandSummary();
    summary = table.getSummary();
    eventTracker = table.getEventTracker();
    minRaise = table.getMinRaise();
    pot = table.getPot();
    lastToAct = table.getLastToAct();
    betting = table.isBetting();
    round = table.getRound();
    blind = table.getBlind();
    pots = table.getPots();
    winners = table.getWinners();
    sharedCards = table.getSharedCards();
  }

  /**
   * Helper which retrieves the player with the specified ID from the list of players, or
   * <code>null</code> if no such player exists.
   *
   * @param id ID of the player.
   * @return Player with ID equal to <code>id</code>, or <code>null</code> if no such player exists.
   */
  public GamePlayerModel getPlayer(final UUID id) {
    return players.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
  }

  public void roundStarted() {
    round++;
  }

  public void playerActed() {
    incActingPlayer();
    incPlayerThatActed();
    actionPerformed();
  }

  public void actionPerformed() {
    eventTracker++;
  }

  public void incPlayerThatActed() {
    playerThatActed = (playerThatActed + 1) % players.size();
  }

  public void incActingPlayer() {
    actingPlayer = (actingPlayer + 1) % players.size();
  }
}
