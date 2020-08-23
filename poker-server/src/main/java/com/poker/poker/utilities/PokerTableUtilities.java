package com.poker.poker.utilities;

import static com.poker.poker.models.enums.GameAction.AllInCheck;
import static com.poker.poker.models.enums.GameAction.Check;
import static com.poker.poker.models.enums.GameAction.Fold;
import static com.poker.poker.utilities.BigDecimalUtilities.max;
import static com.poker.poker.utilities.BigDecimalUtilities.sum;
import static com.poker.poker.utilities.CardUtilities.FACE_DOWN_CARD;
import static com.poker.poker.utilities.CardUtilities.rankHand;
import static ir.cafebabe.math.utils.BigDecimalUtils.is;
import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import com.poker.poker.events.GameActionEvent;
import com.poker.poker.models.enums.GameAction;
import com.poker.poker.models.game.Card;
import com.poker.poker.models.game.Deck;
import com.poker.poker.models.game.GamePlayer;
import com.poker.poker.models.game.HandRankModel;
import com.poker.poker.models.game.PokerTable;
import com.poker.poker.models.game.Pot;
import com.poker.poker.models.game.TableControls;
import com.poker.poker.models.game.Winner;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

/**
 * Static utility class for performing operations on a PokerTable object.
 */
@Slf4j
public final class PokerTableUtilities {

  private PokerTableUtilities() {}

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
  public static PokerTable hideCards(final PokerTable table) {
    // Clone table, creating deep copy of player list and cards.
    final PokerTable newTable = new PokerTable(table);
    // Replace the cards with face down cards.
    newTable
        .getPlayers()
        .forEach(p -> p.setCards(p.getCards().stream().map(c -> FACE_DOWN_CARD).collect(toList())));
    return newTable;
  }

  public static PokerTable hideFoldedCards(final PokerTable table) {
    final PokerTable newTable = new PokerTable(table);
    for (final GamePlayer p : newTable.getPlayers()) {
      if (p.isFolded() || p.isOut()) {
        p.setCards(p.getCards().stream().map(c -> FACE_DOWN_CARD).collect(toList()));
      }
    }
    return newTable;
  }

  /**
   * Adjusts an invalid wager so that it can be handled without producing any errors.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>table</code> is <code>non-null</code>.
   *   <li>List of players is <code>non-null</code>.
   *   <li><code>playerId</code> in the action event refers to a player in the game.
   *   <li>The <code>actingPlayer</code> field of <code>table</code> should refer to the player with
   *       ID = <code>playerId</code>.
   *   <li><code>toCall >= 0</code>.
   *   <li><code>raise >= 0</code>.
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Returned value should be a valid raise, i.e. <code>adjustedWager + toCall <= bankRoll
   *       </code>.
   * </ol>
   *
   * @param table Poker table.
   * @param event Action event.
   * @return Adjusted wager that will maintain the integrity of the poker table model.
   */
  public static BigDecimal adjustWager(final PokerTable table, final GameActionEvent event) {
    if (event.getType() != GameAction.Raise) {
      return null;
    }

    // Validate Pre-Conditions #1 and #2.
    assert table != null;
    assert table.getPlayers() != null;

    final List<GamePlayer> players = table.getPlayers();
    final GamePlayer player = table.getPlayer(event.getPlayerId());

    // Validate Pre-Conditions #3 and #4.
    assert player != null;
    assert players.get(table.getActingPlayer()).equals(player);

    final BigDecimal toCall = player.getToCall();
    final BigDecimal bankRoll = player.getChips();
    final BigDecimal raise = event.getRaise();

    // Validate Pre-Conditions #5 and #6.
    assert is(toCall).gte(ZERO);
    assert is(raise).gte(ZERO);

    final BigDecimal adjustedWager =
        raise.add(toCall).compareTo(bankRoll) > 0 ? bankRoll.subtract(toCall) : raise;

    // Validate Post-Condition.
    assert is(sum(adjustedWager, toCall)).lte(bankRoll);

    return adjustedWager;
  }

  /**
   * Updates table control models, and the table model, after a player performs an action, to ensure
   * the UI is displaying the correct information and to track what has occurred in the game.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li>There are at least 2 players who have not folded or been eliminated.
   *   <li>playerId is valid (i.e. refers to a player in the player list of the poker table).
   *   <li>Raise is non-null if the action is Raise.
   *   <li>Raise is >= table.minRaise if the action is Raise.
   *   <li>If player is performing a Call or Raise action, the player's bank roll has enough chips
   *       to legally perform this action, i.e. bankRoll >= toCall + raise.
   *   <li>Player can only check if calling is free, i.e. toCall field for the player is 0.
   *   <li>Player can only check if their current bet is equal to the minimum raise. Note that the
   *       only player who could potentially check is the player who posted the big blind.
   *   <li>Player can only perform AllInCheck if the player is all-in.
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>actingPlayer field updated correctly.
   *   <li>playerThatActed field updated correctly.
   *   <li>There is at least one player still active in the hand.
   *   <li>Pot and bank rolls are unaffected if action was Fold, Check or AllInCheck.
   *   <li>If action was Call, acting player's bankroll should have decreased by toCall, pot should
   *       have increased by toCall.
   *   <li>If action was Raise, acting player's bankroll should have decreased by (toCall + raise),
   *       i.e. the amount required to make the raise.
   *   <li>If action was Raise, pot should have increased by (toCall + raise).
   *   <li>If action was Raise, minRaise field of the table should be equal to raise
   *   <li>If action was Raise, toCall values of all other players should be set equal to (raise -
   *       currentBet).
   *   <li>If action was Raise, and raise was equal to acting player's bank roll, this player's
   *       all-in field should be set to <code>true</code>.
   *   <li>If action was Raise, the lastToAct field should be updated so that it is the index of the
   *       first active player before the player that raised.
   * </ol>
   *
   * @param table Poker table.
   * @param action Action that was performed.
   * @param playerId ID of the player that performed the action.
   * @param raise The amount raised if action was Raise, null otherwise.
   */
  public static void handlePlayerAction(
      final PokerTable table,
      final GameAction action,
      final UUID playerId,
      final BigDecimal raise) {
    final List<GamePlayer> players = table.getPlayers();

    // Validate Pre-Condition #1.
    assert players.stream().filter(p -> !p.isOut() && !p.isFolded()).count() >= 2;

    // Get player model
    final GamePlayer player = table.getPlayer(playerId);
    // Validate Pre-Condition #2.
    assert player != null;

    final int playerIndex = table.getPlayers().indexOf(player);
    assert playerIndex != -1;

    final BigDecimal pot = table.getPot();
    final BigDecimal minRaise = table.getMinRaise();
    final TableControls controls = player.getControls();
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
        controls.setToCall(ZERO);

        // Set pot to (pot + toCall).
        table.setPot(pot.add(toCall));
        break;
      case Check:
        // Validate Pre-Condition #6.
        assert toCall.equals(ZERO);
        // Validate Pre-Condition #7.
        //        assert minRaise.equals(currentBet); TODO: This is breaking an edge case
        break;
      case AllInCheck:
        // Validate Pre-Condition #8.
        assert player.isAllIn();
        break;
      case Raise:
        // Validate Pre-Conditions #3, #4 and #5.
        assert raise != null;
        assert raise.compareTo(minRaise) >= 0 || raise.add(toCall).equals(bankRoll);
        assert raise.add(toCall).compareTo(bankRoll) <= 0;

        final BigDecimal wager = raise.add(player.getToCall()).add(player.getBet());

        // Subtract (toCall + raise) from bankRoll.
        controls.setBankRoll(bankRoll.subtract(toCall.add(raise)));

        // Update currentBet to (currentBet + toCall + raise).
        controls.setCurrentBet(currentBet.add(toCall).add(raise));

        // Set toCall to 0, player just raised.
        controls.setToCall(ZERO);

        // Set pot to (pot + toCall + raise).
        table.setPot(pot.add(toCall).add(raise));

        // Set minRaise to raise + currentBet.
        table.setMinRaise(controls.getCurrentBet());

        // Update lastToAct (exit condition of betting round).
        table.setLastToAct(getNextActivePlayer(table, playerIndex, false));

        // Update toCall field for other players.
        players.forEach(
            p -> {
              //              final BigDecimal pToCall = p.getControls().getToCall();
              //              final BigDecimal pBankRoll = p.getControls().getBankRoll();
              //              p.setToCall(is(sum(raise, pToCall)).gt(pBankRoll) ? pBankRoll :
              // sum(raise, pToCall));
              final BigDecimal pToCall = wager.subtract(p.getBet());
              p.setToCall(is(pToCall).gte(p.getChips()) ? p.getChips() : pToCall);
            });
        break;
    }

    // If player's bankRoll is 0, then this player is all-in.
    if (is(player.getChips()).eq(ZERO)) {
      player.setAllIn(true);
    }
    generateSidePots(table);
    table.setPlayerThatActed(playerIndex);
    table.setActingPlayer(nextPlayerToAct);
    table.actionPerformed();
  }

  /**
   * Performs the basic poker table setup when a new round begins. This includes setting the <code>
   * dealer</code> field, <code>actingPlayer</code> field, performing the blind bets, etc...
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>table != null</code>.
   *   <li><code>deck != null</code>.
   *   <li><code>table.getPlayers() != null</code>.
   *   <li>There are at least 2 players who have not been eliminated (i.e. with <code>bankRoll > 0
   *       </code>).
   *   <li>The number of players with non-zero bank roll is the same as the number of players whose
   *       <code>out</code> status is false.
   *   <li>All players who have not been eliminated have not folded.
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>round field incremented.
   *   <li>Small blind bet made.
   *   <li>Big blind bet made.
   *   <li><code>pot</code> should be equal to sb + bb.
   *   <li>bb player's <code>toCall = 0</code>.
   *   <li>sb player's <code>toCall = table.blind</code>.
   *   <li>All other players <code>toCall = 2 * table.blind</code>.
   *   <li><code>actingPlayer</code> is the player 3 spots after the <code>dealer</code>.
   *   <li>If <code>table.round % 10 == 0</code>, then <code>table.blind *= 2</code>.
   *   <li>One card is dealt to each player.
   * </ol>
   *
   * @param table Poker table.
   * @param deck Deck being used on this poker table.
   */
  public static void setupNewHand(final PokerTable table, final Deck deck) {
    // Validate Pre-Conditions #1, #2 and #3.
    assert table != null;
    assert deck != null;
    assert table.getPlayers() != null;
    table.roundStarted();
    final int round = table.getRound();

    // Store pointer to player list.
    final List<GamePlayer> players = table.getPlayers();

    // Clear shared cards.
    table.setSharedCards(new ArrayList<>());

    // Validate Pre-Condition #4, #5 and #6.
    final long numNotOut = players.stream().filter(p -> !p.isOut()).count();
    final long numNonZeroChips = players.stream().filter(p -> is(p.getChips()).notEq(ZERO)).count();
    assert numNotOut >= 2;
    assert numNonZeroChips >= 2;
    assert numNotOut == numNonZeroChips;
    assert players.stream().noneMatch(p -> !p.isOut() && p.isFolded());

    // Set the betting flag to indicate a betting round is occurring.
    table.setBetting(true);

    // Hide hand summary.
    table.setDisplayHandSummary(false);
    table.setSummary(null);

    // Set the dealer.
    setNextDealer(table);
    dealCards(table, deck, 2);

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
   *   <li><code>table != null</code>
   *   <li><code>deck != null</code>
   *   <li><code>table.getPlayers() != null</code>
   *   <li>At least 2 players remaining in the hand.
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>All active players were dealt <code>numCards</code> cards.
   *   <li>The dealer was the last person to be dealt a card.
   * </ol>
   *
   * @param table Poker table.
   * @param deck Deck.
   */
  public static void dealCards(
      final PokerTable table, final Deck deck, final int numCards) {
    // Validate Pre-Conditions.
    assert table != null;
    assert deck != null;
    assert table.getPlayers() != null;
    assert table.getPlayers().stream().filter(p -> is(p.getChips()).notEq(ZERO)).count() >= 2;

    final List<GamePlayer> players = table.getPlayers();
    players.forEach(p -> p.setCards(new ArrayList<>())); // Clear drawn cards.
    deck.restoreAndShuffle();
    final int dealer = table.getDealer();

    IntStream.range(0, numCards)
        .forEach(
            i -> {
              for (int j = (dealer + 1) % players.size();
                  j != dealer;
                  j = (j + 1) % players.size()) {
                if (!players.get(j).isFolded() && !players.get(j).isOut()) {
                  players.get(j).getCards().add(deck.draw());
                }
              }
              players.get(dealer).getCards().add(deck.draw());
            });

    // TODO: Temporarily dealing the shared cards.
    table.getSharedCards().add(deck.draw());
    table.getSharedCards().add(deck.draw());
    table.getSharedCards().add(deck.draw());
    deck.draw(); // Burn card.
    table.getSharedCards().add(deck.draw());
    deck.draw(); // Burn card.
    table.getSharedCards().add(deck.draw());
  }

  /**
   * Performs the small and big blind bets which take place at the beginning of a new round.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li>actingPlayer field should be the index of the first active player after the dealer.
   *   <li>currentBet field is equal to 0 for all players.
   *   <li>blind field on poker table is non-zero and non-null.
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Small and big blind bets have been posted.
   *   <li>Pot is equal to the sum of the posted bets.
   *   <li>toCall field of all players is accurate.
   *   <li>actingPlayer field is set to the first active player after the player that posted the big
   *       blind.
   * </ol>
   *
   * @param table Poker table.
   */
  public static void performBlindBets(final PokerTable table) {
    final List<GamePlayer> players = table.getPlayers();
    // Validate Pre-Conditions #1, #2 and #3.
    assert table.getActingPlayer() == getNextActivePlayer(table, table.getDealer(), true);
    assert players.stream().allMatch(p -> p.getBet().equals(ZERO));
    assert table.getBlind() != null && !table.getBlind().equals(ZERO);

    final int sbIndex = table.getActingPlayer();
    final int bbIndex = getNextActivePlayer(table, sbIndex, true);
    final int finalIndex = getNextActivePlayer(table, bbIndex, true);
    final BigDecimal sb = table.getBlind();
    final BigDecimal bb = table.getBlind().add(table.getBlind());

    final GamePlayer sbPlayer = players.get(sbIndex);
    final BigDecimal sbBankRoll = sbPlayer.getChips();
    final GamePlayer bbPlayer = players.get(bbIndex);
    final BigDecimal bbBankRoll = bbPlayer.getChips();

    final BigDecimal sbBet = is(sbBankRoll).gte(sb) ? sb : sbBankRoll;
    final BigDecimal bbBet = is(bbBankRoll).gte(bb) ? bb : bbBankRoll;
    final BigDecimal maxBet = max(sbBet, bbBet);

    // Update pot.
    table.setPot(sbBet.add(bbBet));

    // Update bank roll for sb and bb players.
    sbPlayer.setChips(sbBankRoll.subtract(sbBet));
    bbPlayer.setChips(bbBankRoll.subtract(bbBet));

    sbPlayer.setAllIn(is(sbPlayer.getChips()).eq(ZERO));
    bbPlayer.setAllIn(is(bbPlayer.getChips()).eq(ZERO));

    // Update current bet for sb and bb players.
    sbPlayer.setBet(sbBet);
    bbPlayer.setBet(bbBet);

    // Update minRaise.
    table.setMinRaise(bb);

    // Generate side pots.
    generateSidePots(table);

    // Update toCall field for all players.
    players.forEach(
        p ->
            p.setToCall(
                is(maxBet.subtract(p.getBet())).gte(p.getChips())
                    ? p.getChips()
                    : maxBet.subtract(p.getBet())));

    // Update lastToAct, actingPlaying and playerThatActed.
    table.setLastToAct(bbIndex);
    table.setActingPlayer(finalIndex);
    table.setPlayerThatActed(bbIndex);
  }

  /**
   * Generates the pot(s) based on player's currentBet field. Can have multiple pots in some edge
   * cases where players go all-in, while other players continue betting.
   *
   * @param table Poker table.
   */
  public static void generateSidePots(final PokerTable table) {
    final List<GamePlayer> players = table.getPlayers();
    final List<BigDecimal> allInBets =
        players.stream()
            .filter(GamePlayer::isAllIn)
            .map(GamePlayer::getBet)
            .sorted()
            .distinct()
            .collect(toList());

    final List<BigDecimal> bets =
        players.stream().map(GamePlayer::getBet).sorted().collect(toList());

    if (allInBets.isEmpty()
        || !bets.get(bets.size() - 1).equals(allInBets.get(allInBets.size() - 1))) {
      allInBets.add(bets.get(bets.size() - 1));
    }

    final List<Pot> pots = new ArrayList<>();
    for (final BigDecimal allInBet : allInBets) {
      final Pot pot = new Pot(allInBet, ZERO);
      for (final BigDecimal bet : bets) {
        pot.increaseTotal(is(allInBet).gte(bet) ? bet : allInBet);
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
  public static BigDecimal getPotTotal(final Collection<Pot> pots) {
    return sum(pots.stream().map(Pot::getTotal).collect(toList()));
  }

  /**
   * Utility to set the dealer field to the next player that is still in the game, when a new hand
   * begins.
   *
   * <ul>
   *   <b>Pre-Conditions:</b>
   *   <li>There are at least 2 players who are still in the current hand.
   * </ul>
   *
   * <ul>
   *   <b>Post-Conditions:</b>
   *   <li>dealer field has changed.
   * </ul>
   *
   * @param table Poker table.
   */
  public static void setNextDealer(final PokerTable table) {
    final List<GamePlayer> players = table.getPlayers();

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
   *   <li>startIndex is a valid index.
   *   <li>There are at least 2 players who are still in the current hand.
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Returns the previous/next active player in the rotation.
   * </ol>
   *
   * @param table Poker table.
   * @param startIndex Index in players list.
   * @param forward Should be <code>true</code> if searching forward, <code>false</code> if
   *     searching backward.
   * @return The active player next in the rotation after the player at index startIndex.
   */
  public static int getNextActivePlayer(
      final PokerTable table, final int startIndex, final boolean forward) {
    final List<GamePlayer> players = table.getPlayers();
    // Validate Pre-Conditions #1 and #2.
    assert startIndex >= 0 && startIndex < players.size();
    assert players.stream().filter(p -> !p.isOut() && !p.isFolded()).count() >= 2;

    int j =
        forward
            ? (startIndex + 1) % players.size()
            : (startIndex - 1 + players.size()) % players.size();
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
      final PokerTable table, final int startIndex, final int i, final boolean forward) {
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
   *   <li>Player model is valid, i.e. non-null.
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Returns the correct default action.
   * </ol>
   *
   * @param player Player the system is acting on behalf of.
   * @return The default action for this player.
   */
  public static GameAction defaultAction(final GamePlayer player) {
    return player.isAllIn() ? AllInCheck : is(player.getToCall()).eq(ZERO) ? Check : Fold;
  }

  /**
   * Creates a mapping of hand rank objects, keyed by numerical rank. Used to make dealing with ties
   * easier.
   *
   * @param ranks List of hand rank objects.
   * @return Mapping of hand rank objects, keyed by numerical rank.
   */
  public static Map<Integer, List<HandRankModel>> splitHandsByRank(
      final Iterable<HandRankModel> ranks) {
    final Map<Integer, List<HandRankModel>> map = new HashMap<>();
    for (final HandRankModel rank : ranks) {
      if (map.containsKey(rank.getRank())) {
        map.get(rank.getRank()).add(rank);
      } else {
        map.put(rank.getRank(), new ArrayList<>(singletonList(rank)));
      }
    }
    return map;
  }

  /**
   * Determines the winners when a hand is over, pays them out and updates the <code>winners</code>
   * field on the table.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li>There must be at least 1 person who hasn't folded or been eliminated.
   *   <li>The total in all side pots must be greater than 0.
   * </ol>
   *
   * @param table Poker table.
   */
  public static void determineWinners(final PokerTable table) {
    generateSidePots(table);

    // Get the list of players who have not been eliminated and who have not folded.
    final List<GamePlayer> candidates =
        table.getPlayers().stream().filter(p -> !p.isOut() && !p.isFolded()).collect(toList());

    final List<Pot> pots = table.getPots();

    // Validating Pre-Conditions.
    assert !candidates.isEmpty();
    assert is(getPotTotal(pots)).gt(ZERO);

    // Check how many players are in the hand (i.e. check if all but one folded).
    if (candidates.size() == 1) {
      candidates.get(0).addChips(getPotTotal(pots));
      table.setWinners(
          singletonList(
              new Winner(
                  candidates.get(0).getId(),
                  getPotTotal(pots),
                  asList(
                      FACE_DOWN_CARD,
                      FACE_DOWN_CARD,
                      FACE_DOWN_CARD,
                      FACE_DOWN_CARD,
                      FACE_DOWN_CARD))));
      return;
    }

    // More than 1 player means we need to give out winnings based on hand strength.
    final List<HandRankModel> handRanks =
        candidates.stream()
            .map(
                p -> {
                  final List<Card> cards = new ArrayList<>(p.getCards());
                  cards.addAll(table.getSharedCards()); // TODO: Don't add to this, make new List
                  final HandRankModel handRank = rankHand(cards);
                  handRank.setId(p.getId());
                  return handRank;
                })
            .collect(toList());

    final Map<Integer, List<HandRankModel>> rankMap = splitHandsByRank(handRanks);
    final Map<UUID, Winner> winners =
        new HashMap<UUID, Winner>() {
          {
            handRanks.forEach(r -> put(r.getId(), new Winner(r.getId(), ZERO, r.getHand())));
          }
        };

    // Iterate over ranks, from highest to lowest.
    for (final int rank : rankMap.keySet().stream().sorted((a, b) -> b - a).collect(toList())) {
      // Stop iterating when there are no more chips left to give out.
      if (is(getPotTotal(pots)).eq(ZERO)) {
        break;
      }
      /*
           Determine how many people are entitled to each side-pot.
           Give (pot total)/(count) to each person who is entitled to a share of the side-pot.
      */
      final List<GamePlayer> players =
          rankMap.get(rank).stream().map(p -> table.getPlayer(p.getId())).collect(toList());

      // Iterate over all side-pots.
      for (final Pot pot : pots) {
        // Determine which players are entitled to this particular side-pot.
        final List<GamePlayer> playersEntitledToPot =
            players.stream()
                .filter(
                    p -> is(p.getBet()).gte(pot.getWager())) // Players bet has to be >= pots wager
                .collect(toList());
        final int count = playersEntitledToPot.size();
        if (count == 0) {
          continue;
        }
        // TODO: Figure out exactly how rounding works here
        final BigDecimal winnings =
            pot.getTotal().divide(new BigDecimal(count), RoundingMode.FLOOR);
        playersEntitledToPot.forEach(
            p -> {
              p.addChips(winnings);
              winners.get(p.getId()).increaseWinnings(winnings);
            });
        // If at least 1 player was entitled to this side-pot, the new total should be 0.
        pot.setTotal(ZERO);
      }
    }

    table.setWinners(
        winners.values().stream()
            .filter(w -> is(w.getWinnings()).gt(ZERO))
            .sorted((a, b) -> b.getWinnings().compareTo(a.getWinnings()))
            .collect(toList()));
  }

  /**
   * Determines the winners, pays them out and then generates a summary of the hand, once the
   * betting has concluded.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li>There is either one player remaining in the hand, or the player
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>All players with 0 chips have their out status set to true.
   *   <li>All players with chips remaining have folded and allin status set to false.
   *   <li>Pot value reduced to 0.
   *   <li>Winning players bank roll is increased by the value of the pot.
   *   <li>Hand summary is generated and returned containing the index of the winning player and the
   *       winning card.
   * </ol>
   *
   * @param table Poker table.
   * @param broadcaster Runnable that will broadcast the table to the client.
   */
  public static void handleEndOfHand(final PokerTable table, final Runnable broadcaster) {
    // Betting round has concluded.
    table.setBetting(false);
    determineWinners(table);
    table.setDisplayHandSummary(true);
    broadcaster.run(); // Broadcast hand.

    // Not clearing cards until next hand begins.
    table
        .getPlayers()
        .forEach(
            p -> {
              p.setFolded(false); // Should always be reset false.
              p.setAllIn(false); // Should always be reset to false.
              p.setOut(is(p.getChips()).eq(ZERO)); // True when 0 chips left.
              p.setControls(new TableControls(p.getChips())); // Keep bankRoll.
            });
  }

  /**
   * Helper method used to generate various system messages when certain events occur, i.e. when a
   * player folds, checks, calls, etc...
   *
   * @param table Poker table.
   * @param event Event.
   * @return System message which will be displayed in the game chat.
   */
  public static String getSystemChatActionMessage(
      final PokerTable table, final GameActionEvent event) {
    final GamePlayer player = table.getPlayers().get(table.getActingPlayer());
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
        message =
            String.format(
                "%s %s raised by %s.",
                player.getFirstName(), player.getLastName(), event.getRaise());
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
