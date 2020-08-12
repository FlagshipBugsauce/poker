package com.poker.poker.utilities;

import static com.poker.poker.utilities.BigDecimalUtilities.max;
import static com.poker.poker.utilities.BigDecimalUtilities.sum;
import static ir.cafebabe.math.utils.BigDecimalUtils.is;

import com.poker.poker.events.GameActionEvent;
import com.poker.poker.models.enums.CardSuit;
import com.poker.poker.models.enums.CardValue;
import com.poker.poker.models.enums.GameAction;
import com.poker.poker.models.game.CardModel;
import com.poker.poker.models.game.DeckModel;
import com.poker.poker.models.game.GamePlayerModel;
import com.poker.poker.models.game.HandSummaryModel;
import com.poker.poker.models.game.PokerTableModel;
import com.poker.poker.models.game.PotModel;
import com.poker.poker.models.game.TableControlsModel;
import com.poker.poker.models.game.WinnerModel;
import com.poker.poker.services.game.CardService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Static utility class for performing operations on a PokerTableModel object.
 */
@Slf4j
public final class PokerTableUtilities {

  private PokerTableUtilities() {
  }

  /**
   * Creates a clone of the argument poker table which has new player objects with face down cards.
   * When publishing a poker table update before cards are flipped, the table will be passed to this
   * method, which will ensure that players cannot see other players cards.
   *
   * @param table Poker table.
   * @return A clone of the table with all cards hidden (face down, i.e. client has no way of
   * knowing what these cards are because the values will not be sent to any clients if the table is
   * first processed by this method).
   */
  public static PokerTableModel hideCards(final PokerTableModel table) {
    // Clone table, creating deep copy of player list and cards.
    final PokerTableModel newTable = new PokerTableModel(table);
    // Replace the cards with face down cards.
    newTable
        .getPlayers()
        .forEach(p -> p.setCards(
            p.getCards()
                .stream()
                .map(c -> new CardModel(CardSuit.Back, CardValue.Back))
                .collect(Collectors.toList())));
    return newTable;
  }

  /**
   * Adjusts an invalid wager so that it can be handled without producing any errors.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>table</code> is <code>non-null</code>.</li>
   *   <li>List of players is <code>non-null</code>.</li>
   *   <li><code>playerId</code> in the action event refers to a player in the game.</li>
   *   <li>
   *     The <code>actingPlayer</code> field of <code>table</code> should refer to the player with
   *     ID = <code>playerId</code>.
   *   </li>
   *   <li><code>toCall >= 0</code>.</li>
   *   <li><code>raise >= 0</code>.</li>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Returned value should be a valid raise, i.e.
   *   <code>adjustedWager + toCall <= bankRoll</code>.</li>
   * </ol>
   *
   * @param table Poker table.
   * @param event Action event.
   * @return Adjusted wager that will maintain the integrity of the poker table model.
   */
  public static BigDecimal adjustWager(final PokerTableModel table, final GameActionEvent event) {
    if (event.getType() != GameAction.Raise) {
      return null;
    }

    // Validate Pre-Conditions #1 and #2.
    assert table != null;
    assert table.getPlayers() != null;

    final List<GamePlayerModel> players = table.getPlayers();
    final GamePlayerModel player = players
        .stream()
        .filter(p -> p.getId().equals(event.getPlayerId()))
        .findFirst()
        .orElse(null);

    // Validate Pre-Conditions #3 and #4.
    assert player != null;
    assert players.get(table.getActingPlayer()).equals(player);

    final BigDecimal toCall = player.getControls().getToCall();
    final BigDecimal bankRoll = player.getControls().getBankRoll();
    final BigDecimal raise = event.getRaise();

    // Validate Pre-Conditions #5 and #6.
    assert is(toCall).gte(BigDecimal.ZERO);
    assert is(raise).gte(BigDecimal.ZERO);
//    assert toCall.compareTo(BigDecimal.ZERO) >= 0;
//    assert raise.compareTo(BigDecimal.ZERO) >= 0;

    final BigDecimal adjustedWager = raise.add(toCall).compareTo(bankRoll) > 0 ?
        bankRoll.subtract(toCall) : raise;

    // Validate Post-Condition.
    assert is(sum(adjustedWager, toCall)).lte(bankRoll);
//    assert adjustedWager.add(toCall).compareTo(bankRoll) <= 0;

    return adjustedWager;
  }

  /**
   * Updates table control models, and the table model, after a player performs an action, to ensure
   * the UI is displaying the correct information and to track what has occurred in the game.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li>There are at least 2 players who have not folded or been eliminated.</li>
   *   <li>playerId is valid (i.e. refers to a player in the player list of the poker table).</li>
   *   <li>Raise is non-null if the action is Raise.</li>
   *   <li>Raise is >= table.minRaise if the action is Raise.</li>
   *   <li>
   *     If player is performing a Call or Raise action, the player's bank roll has enough chips to
   *     legally perform this action, i.e. bankRoll >= toCall + raise.
   *   </li>
   *   <li>Player can only check if calling is free, i.e. toCall field for the player is 0.</li>
   *   <li>
   *     Player can only check if their current bet is equal to the minimum raise. Note that the
   *     only player who could potentially check is the player who posted the big blind.
   *   </li>
   *   <li>Player can only perform AllInCheck if the player is all-in.</li>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>actingPlayer field updated correctly.</li>
   *   <li>playerThatActed field updated correctly.</li>
   *   <li>There is at least one player still active in the hand.</li>
   *   <li>Pot and bank rolls are unaffected if action was Fold, Check or AllInCheck.</li>
   *   <li>
   *     If action was Call, acting player's bankroll should have decreased by toCall, pot should
   *     have increased by toCall.
   *   </li>
   *   <li>
   *     If action was Raise, acting player's bankroll should have decreased by (toCall + raise),
   *     i.e. the amount required to make the raise.
   *   </li>
   *   <li>If action was Raise, pot should have increased by (toCall + raise).</li>
   *   <li>If action was Raise, minRaise field of the table should be equal to raise</li>
   *   <li>
   *     If action was Raise, toCall values of all other players should be set equal to
   *     (raise - currentBet).
   *   </li>
   *   <li>
   *     If action was Raise, and raise was equal to acting player's bank roll, this player's all-in
   *     field should be set to <code>true</code>.
   *   </li>
   *   <li>
   *     If action was Raise, the lastToAct field should be updated so that it is the index of the
   *     first active player before the player that raised.
   *   </li>
   * </ol>
   *
   * @param table    Poker table.
   * @param action   Action that was performed.
   * @param playerId ID of the player that performed the action.
   * @param raise    The amount raised if action was Raise, null otherwise.
   */
  public static void handlePlayerAction(
      final PokerTableModel table,
      final GameAction action,
      final UUID playerId,
      final BigDecimal raise) {
    final List<GamePlayerModel> players = table.getPlayers();

    // Validate Pre-Condition #1.
    assert players.stream().filter(p -> !p.isOut() && !p.isFolded()).count() >= 2;

    // Get player model
    final GamePlayerModel player = players
        .stream()
        .filter(p -> p.getId().equals(playerId))
        .findFirst()
        .orElse(null);
    // Validate Pre-Condition #2.
    assert player != null;

    final int playerIndex = table.getPlayers().indexOf(player);
    assert playerIndex != -1;

    final BigDecimal pot = table.getPot();
    final BigDecimal minRaise = table.getMinRaise();
    final TableControlsModel controls = player.getControls();
    final BigDecimal bankRoll = controls.getBankRoll();
    final BigDecimal toCall = controls.getToCall();
    final BigDecimal currentBet = controls.getCurrentBet();

    // Need to get this here to avoid violating Pre-Condition #3 of getNextActivePlayer.
    final int nextPlayerToAct = getNextActivePlayer(table, playerIndex, true);

    switch (action) {
      case Fold:
        player.setFolded(true);
        break;
      case Call:
        // Validate Pre-Condition #5.
        assert bankRoll.compareTo(toCall) >= 0;
        // Subtract toCall from bankRoll
        controls.setBankRoll(bankRoll.subtract(toCall));
        // Update currentBet to (currentBet + toCall).
        controls.setCurrentBet(currentBet.add(toCall));
        // Set toCall to 0, player just called.
        controls.setToCall(BigDecimal.ZERO);
        // Set pot to (pot + toCall).
        table.setPot(pot.add(toCall));
        break;
      case Check:
        // Validate Pre-Condition #6.
        assert toCall.equals(BigDecimal.ZERO);
        // Validate Pre-Condition #7.
//        assert minRaise.equals(currentBet); TODO: This is breaking an edge case
        break;
      case AllInCheck:
        // Validate Pre-Condition #8.
        assert player.isAllIn();
        break;
      case Raise:
        // Validate Pre-Condition #3.
        assert raise != null;
        // Validate Pre-condition #4.
        assert raise.compareTo(minRaise) >= 0 || raise.add(toCall).equals(bankRoll);
        // Validate Pre-condition #5.
        assert raise.add(toCall).compareTo(bankRoll) <= 0;
        // Subtract (toCall + raise) from bankRoll.
        controls.setBankRoll(bankRoll.subtract(toCall.add(raise)));
        // Update currentBet to (currentBet + toCall + raise).
        controls.setCurrentBet(currentBet.add(toCall).add(raise));
        // Set toCall to 0, player just raised.
        controls.setToCall(BigDecimal.ZERO);
        // Set pot to (pot + toCall + raise).
        table.setPot(pot.add(toCall).add(raise));
        // Set minRaise to raise + currentBet.
        table.setMinRaise(controls.getCurrentBet());
        // Update lastToAct (exit condition of betting round).
        table.setLastToAct(getNextActivePlayer(table, playerIndex, false));
        // Update toCall field for other players.
        players.forEach(p -> {
          final BigDecimal pToCall = p.getControls().getToCall();
          final BigDecimal pBankRoll = p.getControls().getBankRoll();
          p.getControls().setToCall(
              raise.add(pToCall).compareTo(pBankRoll) > 0 ? pBankRoll : raise.add(pToCall));
        });
        break;
    }

    // If player's bankRoll is 0, then this player is all-in.
    if (controls.getBankRoll().equals(BigDecimal.ZERO)) {
      player.setAllIn(true);
    }

    generateSidePots(table);

    table.setPlayerThatActed(playerIndex);
    table.setActingPlayer(nextPlayerToAct);
    table.actionPerformed();
  }

  /**
   * Performs the basic poker table setup when a new round begins. This includes setting the
   * <code>dealer</code> field, <code>actingPlayer</code> field, performing the blind bets, etc...
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>table != null</code>.</li>
   *   <li><code>deck != null</code>.</li>
   *   <li><code>table.getPlayers() != null</code>.</li>
   *   <li>
   *     There are at least 2 players who have not been eliminated (i.e. with
   *     <code>bankRoll > 0</code>).
   *   </li>
   *   <li>
   *     The number of players with non-zero bank roll is the same as the number of players whose
   *     <code>out</code> status is false.
   *   </li>
   *   <li>All players who have not been eliminated have not folded.</li>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>round field incremented.</li>
   *   <li>Small blind bet made.</li>
   *   <li>Big blind bet made.</li>
   *   <li><code>pot</code> should be equal to sb + bb.</li>
   *   <li>bb player's <code>toCall = 0</code>.</li>
   *   <li>sb player's <code>toCall = table.blind</code>.</li>
   *   <li>All other players <code>toCall = 2 * table.blind</code>.</li>
   *   <li><code>actingPlayer</code> is the player 3 spots after the <code>dealer</code>.</li>
   *   <li>If <code>table.round % 10 == 0</code>, then <code>table.blind *= 2</code>.</li>
   *   <li>One card is dealt to each player.</li>
   * </ol>
   *
   * @param table Poker table.
   * @param deck  Deck being used on this poker table.
   */
  public static void newHandSetup(final PokerTableModel table, final DeckModel deck) {
    // Validate Pre-Conditions #1, #2 and #3.
    assert table != null;
    assert deck != null;
    assert table.getPlayers() != null;
    table.roundStarted();
    final int round = table.getRound();

    // Store pointer to player list.
    final List<GamePlayerModel> players = table.getPlayers();

    // Update folded, allIn, out and table controls.
    players.forEach(p -> {
      p.setFolded(false); // Should always be reset false.
      p.setAllIn(false); // Should always be reset to false.
      p.setOut(p.getControls().getBankRoll().equals(BigDecimal.ZERO));  // True when 0 chips left.
      p.setControls(new TableControlsModel(p.getControls().getBankRoll())); // Keep bankRoll only.
    });

    // Validate Pre-Condition #4, #5 and #6.
    final long numNotOut = players.stream().filter(p -> !p.isOut()).count();
    final long numWithNonZeroChips = players.stream()
        .filter(p -> !p.getControls().getBankRoll().equals(BigDecimal.ZERO)).count();
    assert numNotOut >= 2;
    assert numWithNonZeroChips >= 2;
    assert numNotOut == numWithNonZeroChips;
    assert players.stream().noneMatch(p -> !p.isOut() && p.isFolded());

    // Set the betting flag to indicate a betting round is occurring.
    table.setBetting(true);

    // Hide hand summary.
    table.setDisplayHandSummary(false);
    table.setSummary(null);

    // Set the dealer.
    setNextDealer(table);
    dealCards(table, deck);

    // Set first to act.
    table.setActingPlayer(getNextActivePlayer(table, table.getDealer(), true));

    final BigDecimal blind = table.getBlind();
    // Increase blinds, if appropriate.
    table.setBlind(round % 10 == 0 ? blind.add(blind) : blind);

    // Perform SB BB bets.
    performBlindBets(table);
  }

  /**
   * Clears any cards the players have, restores and shuffles the deck, and then deals cards to the
   * players who are not folded or eliminated, starting with the first active player to the left of
   * the dealer and ending with the dealer.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>table != null</code></li>
   *   <li><code>deck != null</code></li>
   *   <li><code>table.getPlayers() != null</code></li>
   *   <li>At least 2 players remaining in the hand.</li>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>All active players were dealt a card.</li>
   *   <li>The dealer was the last person to be dealt a card.</li>
   * </ol>
   *
   * @param table Poker table.
   * @param deck  Deck.
   */
  public static void dealCards(final PokerTableModel table, final DeckModel deck) {
    // Validate Pre-Conditions.
    assert table != null;
    assert deck != null;
    assert table.getPlayers() != null;
    assert table.getPlayers().stream()
        .filter(p -> !p.getControls().getBankRoll().equals(BigDecimal.ZERO)).count() >= 2;

    final List<GamePlayerModel> players = table.getPlayers();
    players.forEach(p -> p.setCards(new ArrayList<>())); // Clear drawn cards.
    deck.restoreAndShuffle();
    final int dealer = table.getDealer();
    for (int i = (dealer + 1) % players.size(); i != dealer; i = (i + 1) % players.size()) {
      if (!players.get(i).isFolded() && !players.get(i).isOut()) {
        players.get(i).getCards().add(deck.draw());
      }
    }
    players.get(dealer).getCards().add(deck.draw());
  }

  /**
   * Performs the small and big blind bets which take place at the beginning of a new round.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li>actingPlayer field should be the index of the first active player after the dealer.</li>
   *   <li>currentBet field is equal to 0 for all players.</li>
   *   <li>blind field on poker table is non-zero and non-null.</li>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Small and big blind bets have been posted.</li>
   *   <li>Pot is equal to the sum of the posted bets.</li>
   *   <li>toCall field of all players is accurate.</li>
   *   <li>
   *     actingPlayer field is set to the first active player after the player that posted the big
   *     blind.
   *   </li>
   * </ol>
   *
   * @param table Poker table.
   */
  public static void performBlindBets(final PokerTableModel table) {
    final List<GamePlayerModel> players = table.getPlayers();
    // Validate Pre-Conditions #1, #2 and #3.
    assert table.getActingPlayer() == getNextActivePlayer(table, table.getDealer(), true);
    assert players.stream().allMatch(p -> p.getControls().getCurrentBet().equals(BigDecimal.ZERO));
    assert table.getBlind() != null && !table.getBlind().equals(BigDecimal.ZERO);

    final int sbIndex = table.getActingPlayer();
    final int bbIndex = getNextActivePlayer(table, sbIndex, true);
    final int finalIndex = getNextActivePlayer(table, bbIndex, true);
    final BigDecimal sb = table.getBlind();
    final BigDecimal bb = table.getBlind().add(table.getBlind());

    final GamePlayerModel sbPlayer = players.get(sbIndex);
    final BigDecimal sbBankRoll = sbPlayer.getControls().getBankRoll();
    final GamePlayerModel bbPlayer = players.get(bbIndex);
    final BigDecimal bbBankRoll = bbPlayer.getControls().getBankRoll();

    final BigDecimal sbBet = sbBankRoll.compareTo(sb) >= 0 ? sb : sbBankRoll;
    final BigDecimal bbBet = bbBankRoll.compareTo(bb) >= 0 ? bb : bbBankRoll;
    final BigDecimal maxBet = max(sbBet, bbBet);

    // Update pot.
    table.setPot(sbBet.add(bbBet));

    // Update bank roll for sb and bb players.
    sbPlayer.getControls().setBankRoll(sbBankRoll.subtract(sbBet));
    bbPlayer.getControls().setBankRoll(bbBankRoll.subtract(bbBet));

    if (sbPlayer.getControls().getBankRoll().equals(BigDecimal.ZERO)) {
      sbPlayer.setAllIn(true);
    }
    if (bbPlayer.getControls().getBankRoll().equals(BigDecimal.ZERO)) {
      bbPlayer.setAllIn(true);
    }

    // Update current bet for sb and bb players.
    sbPlayer.getControls().setCurrentBet(sbBet);
    bbPlayer.getControls().setCurrentBet(bbBet);

    // Update minRaise.
    table.setMinRaise(bb);

    // Generate side pots.
    generateSidePots(table);

    // Update toCall field for all players.
    players.forEach(p -> {
      final BigDecimal pBankRoll = p.getControls().getBankRoll();
      final BigDecimal pCurrentBet = p.getControls().getCurrentBet();
      final BigDecimal toCall = maxBet.subtract(pCurrentBet).compareTo(pBankRoll) >= 0 ?
          pBankRoll : maxBet.subtract(pCurrentBet);
      p.getControls().setToCall(toCall);
    });

    // Update lastToAct, actingPlaying and playerThatActed.
    table.setLastToAct(bbIndex);
    table.setActingPlayer(finalIndex);
    table.setPlayerThatActed(bbIndex);
  }

  // TODO: Refactor into BigDecimal Utils
  public static BigDecimal maxBigDecimal(final BigDecimal... nums) {
    BigDecimal result = BigDecimal.ZERO;
    for (BigDecimal num : nums) {
      result = num.compareTo(result) > 0 ? num : result;
    }
    return result;
  }

  /**
   * Generates the pot(s) based on player's currentBet field. Can have multiple pots in some edge
   * cases where players go all-in, while other players continue betting.
   *
   * @param table Poker table.
   */
  public static void generateSidePots(final PokerTableModel table) {
    final List<BigDecimal> allInBets = table.getPlayers()
        .stream()
        .filter(GamePlayerModel::isAllIn)
        .map(p -> p.getControls().getCurrentBet())
        .sorted()
        .collect(Collectors.toList());

    final List<BigDecimal> bets = table.getPlayers()
        .stream()
        .map(p -> p.getControls().getCurrentBet().add(BigDecimal.ZERO))
        .sorted()
        .collect(Collectors.toList());

    if (allInBets.isEmpty() ||
        !bets.get(bets.size() - 1).equals(allInBets.get(allInBets.size() - 1))) {
      allInBets.add(bets.get(bets.size() - 1));
    }

    final List<PotModel> pots = new ArrayList<>();
    for (final BigDecimal allInBet : allInBets) {
      final PotModel pot = new PotModel(allInBet, BigDecimal.ZERO);
      for (final BigDecimal bet : bets) {
        pot.increaseTotal(is(allInBet).gte(bet) ? bet : allInBet);
//        pot.increaseTotal(allInBet.compareTo(bet) >= 0 ? bet : allInBet);
      }
      pots.add(pot);
    }

    // Total_i - Total_i-1
    for (int i = pots.size() - 1; i > 0; i--) {
      pots.get(i).setTotal(pots.get(i).getTotal().subtract(pots.get(i - 1).getTotal()));
    }

    table.setPots(pots);
  }

  /**
   * Determines the total amount in all pots in the collection provided.
   *
   * @param pots Iterable collection of "pot's".
   * @return Total amount in all pots combined.
   */
  public static BigDecimal getTotalInAllSidePots(final Collection<PotModel> pots) {
//    BigDecimal allPotsTotal = BigDecimal.ZERO;
//    for (PotModel pot : pots) {
//      allPotsTotal = pot.getTotal().add(allPotsTotal);
//    }
    return sum(pots.stream().map(PotModel::getTotal).collect(Collectors.toList()));
  }

  /**
   * Utility to set the dealer field to the next player that is still in the game, when a new hand
   * begins.
   *
   * <ul>
   *   <b>Pre-Conditions:</b>
   *   <li>There are at least 2 players who are still in the current hand.</li>
   * </ul>
   *
   * <ul>
   *   <b>Post-Conditions:</b>
   *   <li>dealer field has changed.</li>
   * </ul>
   *
   * @param table Poker table.
   */
  public static void setNextDealer(final PokerTableModel table) {
    final List<GamePlayerModel> players = table.getPlayers();

    // Validate Pre-Condition #1.
    assert players.stream().filter(p -> !p.isOut()).count() >= 2;

    final int oldDealer = table.getDealer();
    table.setDealer(getNextActivePlayer(table, oldDealer, true));

    // Validate Post-Condition #1.
    assert oldDealer != table.getDealer();
  }

  /**
   * Determines the next player in the rotation before or after the player at index startIndex. Used
   * to determine who the next dealer is, the next player to act, etc...
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li>startIndex is a valid index.</li>
   *   <li>There are at least 2 players who are still in the current hand.</li>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Returns the previous/next active player in the rotation.</li>
   * </ol>
   *
   * @param table      Poker table.
   * @param startIndex Index in players list.
   * @param forward    Should be <code>true</code> if searching forward, <code>false</code> if
   *                   searching backward.
   * @return The active player next in the rotation after the player at index startIndex.
   */
  public static int getNextActivePlayer(
      final PokerTableModel table, final int startIndex, final boolean forward) {
    final List<GamePlayerModel> players = table.getPlayers();
    // Validate Pre-Conditions #1 and #2.
    assert startIndex >= 0 && startIndex < players.size();
    assert players.stream().filter(p -> !p.isOut() && !p.isFolded()).count() >= 2;

    int j = forward ? (startIndex + 1) % players.size() :
        (startIndex - 1 + players.size()) % players.size();
    // Loop-Invariant: jth player has either been eliminated, or has folded.
    // Exit-Condition: jth player is active in the current hand.
    while (players.get(j).isOut() || players.get(j).isFolded()) {
      j = forward ? (j + 1) % players.size() : (j - 1 + players.size()) % players.size();
    }
    return j;
  }

  /**
   * Returns the ith next active player.
   *
   * @param table      Poker table.
   * @param startIndex Start index.
   * @param i          i.
   * @param forward    Forward or backward.
   * @return ith next active player.
   */
  public static int getIthNextActivePlayer(
      final PokerTableModel table, final int startIndex, final int i, final boolean forward) {
    int result = startIndex;
    for (int j = 0; j < i; j++) {
      result = getNextActivePlayer(table, result, forward);
    }
    return result;
  }

  /**
   * Utility to determine the default action that should be performed if a player is away and it is
   * their turn to act.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li>Player model is valid, i.e. non-null.</li>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Returns the correct default action.</li>
   * </ol>
   *
   * @param player Player the system is acting on behalf of.
   * @return The default action for this player.
   */
  public static GameAction defaultAction(final GamePlayerModel player) {
    return player.isAllIn() ? GameAction.AllInCheck :
        player.getControls().getToCall().equals(BigDecimal.ZERO) ?
            GameAction.Check : GameAction.Fold;
  }

  /**
   * Determines the winners when a hand is over.
   *
   * @param table       Poker table.
   * @param cardService Card service (contains card comparator).
   */
  public static void determineWinners(
      final PokerTableModel table, final Comparator<CardModel> cardService) {
    generateSidePots(table);
    final List<GamePlayerModel> players =
        table.getPlayers().stream()
            .filter(p -> !p.isOut() && !p.isFolded())
            .map(GamePlayerModel::new)
            .sorted((a, b) -> -cardService.compare(a.getCards().get(0), b.getCards().get(0)))
            .collect(Collectors.toList());
    final List<PotModel> pots = table.getPots();
    final List<WinnerModel> winners = new ArrayList<>();

    if (players.size() == 1) {
      // Then we're not revealing the cards and the only player left wins all pots.
      final GamePlayerModel winner = players.get(0);
      final List<CardModel> faceDownCards = Collections.singletonList(CardService.FACE_DOWN_CARD);
      winners.add(new WinnerModel(winner.getId(), BigDecimal.ZERO, faceDownCards));
      winners.get(0).setWinnings(getTotalInAllSidePots(table.getPots()));
      table.setWinners(winners);
      return;
    }

    /*
         If we have more than one player who is still in the hand, then we need to determine which
         side pots go to each player. We'll keep giving out chips until the amount remaining in
         all of the side pots reaches 0.
     */
    while (!getTotalInAllSidePots(pots).equals(BigDecimal.ZERO)) {
      final GamePlayerModel winningPlayer = players.remove(0);
      final BigDecimal winningWager = winningPlayer.getControls().getCurrentBet();
      final WinnerModel winner =
          new WinnerModel(winningPlayer.getId(), BigDecimal.ZERO, winningPlayer.getCards());

      for (final PotModel pot : pots) {
        if (winningWager.compareTo(pot.getWager()) < 0) {
          break;
        }
        winner.increaseWinnings(pot.getTotal());
        pot.setTotal(BigDecimal.ZERO);
      }
      winners.add(winner);
    }
    table.setWinners(winners);
  }

  /**
   * Generates a summary of the hand, once the betting has concluded.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li>There is either one player remaining in the hand, or the player</li>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>All players with 0 chips have their out status set to true.</li>
   *   <li>All players with chips remaining have folded and allin status set to false.</li>
   *   <li>Pot value reduced to 0.</li>
   *   <li>Winning players bank roll is increased by the value of the pot.</li>
   *   <li>
   *     Hand summary is generated and returned containing the index of the winning player and the
   *     winning card.
   *   </li>
   * </ol>
   *
   * @param table Poker table.
   */
  public static void handleEndOfHand(
      final PokerTableModel table, final Comparator<CardModel> cardService) {

    // Betting round has concluded.
    table.setBetting(false);
    determineWinners(table, cardService);
    table.setSummary(new HandSummaryModel(
        table.getWinners().get(0).getCards().get(0),
        table.getPlayers()
            .indexOf(table.getPlayers()
                .stream()
                .filter(p -> p.getId().equals(table.getWinners().get(0).getId()))
                .findFirst()
                .orElse(null))));

    table.getWinners().forEach(w -> {
      final GamePlayerModel player = Objects.requireNonNull(table.getPlayers()
          .stream()
          .filter(p -> p.getId().equals(w.getId()))
          .findFirst().orElse(null));
      player.getControls().setBankRoll(player.getControls().getBankRoll().add(w.getWinnings()));
    });
    // Not clearing cards until next hand begins.
    table.setDisplayHandSummary(true);
  }

  public static String getSystemChatActionMessage(
      final PokerTableModel table, final GameActionEvent event) {
    final GamePlayerModel player = table.getPlayers().get(table.getActingPlayer());
    final String message;

    assert event.getType() != null;
    switch (event.getType()) {
      case Fold:
        message = String.format("%s %s folded.", player.getFirstName(), player.getLastName());
        break;
      case Check:
        message = String.format("%s %s checked.", player.getFirstName(), player.getLastName());
        break;
      case Call:
        message = String.format("%s %s called.", player.getFirstName(), player.getLastName());
        break;
      case Raise:
        message = String.format(
            "%s %s raised by %s.",
            player.getFirstName(),
            player.getLastName(),
            event.getRaise());
        break;
      case AllInCheck:
        message = String.format("%s %s is ALL-IN!", player.getFirstName(), player.getLastName());
        break;
      default:
        message = "Error.";
        break;
    }

    return message;
  }
}