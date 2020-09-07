package com.poker.poker.models.game;

import static com.poker.poker.models.enums.HandPhase.PreFlop;
import static java.math.BigDecimal.ZERO;

import com.poker.poker.models.enums.HandPhase;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PokerTable {

  /**
   * List of players at the table. Contains player names, bank roll, score, etc...
   */
  @ArraySchema(schema = @Schema(implementation = GamePlayer.class))
  private List<GamePlayer> players;

  /**
   * Amount of time (in seconds) players have to act when it is their turn.
   */
  @Min(value = 0)
  @Schema(
      description = "Amount of time (in seconds) players have to act when it is their turn.",
      example = "25")
  private int turnDuration;

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
  @Schema(implementation = HandSummary.class)
  private HandSummary summary = null;

  /**
   * Winners of the hand.
   */
  @ArraySchema(schema = @Schema(implementation = Winner.class))
  private List<Winner> winners = new ArrayList<>();

  /**
   * This is incremented whenever some action is performed.
   */
  @Schema(description = "This is incremented whenever some action is performed.", example = "69")
  private int eventTracker = 0;

  /** Minimum raise amount. */
  @Schema(description = "Minimum raise amount.", example = "69", implementation = BigDecimal.class)
  private BigDecimal minRaise = ZERO;

  /** Total amount in the pot. */
  @Schema(
      description = "Total amount in the pot.",
      example = "420.69",
      implementation = BigDecimal.class)
  private BigDecimal pot = ZERO;

  /** Side-pots. */
  @ArraySchema(schema = @Schema(implementation = Pot.class))
  private List<Pot> pots = new ArrayList<>();

  /** Blinds. */
  @Schema(description = "Blinds.", example = "69", implementation = BigDecimal.class)
  private BigDecimal blind = ZERO;

  /** Blinds. */
  @Schema(description = "Current round.", example = "69")
  private int round = 0;

  /** The round will end once this player has acted. */
  @Schema(description = "The round will end once this player has acted.", example = "2")
  private int lastToAct = 0;

  /** Flag that is true when a betting round is taking place. */
  @Schema(description = "Flag that is true when a betting round is taking place.", example = "true")
  private boolean betting = false;

  /** Phase the hand is in. */
  @Schema(description = "Phase the hand is in.", example = "Flop", implementation = HandPhase.class)
  private HandPhase phase = PreFlop;

  private List<Card> sharedCards = new ArrayList<>();

  public PokerTable(final PokerTable table) {
    players = table.getPlayers().stream().map(GamePlayer::new).collect(Collectors.toList());
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
    phase = table.getPhase();
  }

  /**
   * Helper which retrieves the player with the specified ID from the list of players, or <code>null
   * </code> if no such player exists.
   *
   * @param id ID of the player.
   * @return Player with ID equal to <code>id</code>, or <code>null</code> if no such player exists.
   */
  public GamePlayer getPlayer(final UUID id) {
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
