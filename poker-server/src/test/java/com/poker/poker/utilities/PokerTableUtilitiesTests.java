package com.poker.poker.utilities;

import static com.poker.poker.models.enums.CardSuit.Clubs;
import static com.poker.poker.models.enums.CardSuit.Diamonds;
import static com.poker.poker.models.enums.CardSuit.Hearts;
import static com.poker.poker.models.enums.CardSuit.Spades;
import static com.poker.poker.models.enums.CardValue.Ace;
import static com.poker.poker.models.enums.CardValue.Eight;
import static com.poker.poker.models.enums.CardValue.Five;
import static com.poker.poker.models.enums.CardValue.Four;
import static com.poker.poker.models.enums.CardValue.Jack;
import static com.poker.poker.models.enums.CardValue.King;
import static com.poker.poker.models.enums.CardValue.Nine;
import static com.poker.poker.models.enums.CardValue.Queen;
import static com.poker.poker.models.enums.CardValue.Seven;
import static com.poker.poker.models.enums.CardValue.Six;
import static com.poker.poker.models.enums.CardValue.Ten;
import static com.poker.poker.models.enums.CardValue.Three;
import static com.poker.poker.models.enums.CardValue.Two;
import static com.poker.poker.models.enums.GameAction.AllInCheck;
import static com.poker.poker.models.enums.GameAction.Call;
import static com.poker.poker.models.enums.GameAction.Fold;
import static com.poker.poker.models.enums.GameAction.Raise;
import static com.poker.poker.utilities.BigDecimalUtilities.sum;
import static com.poker.poker.utilities.CardUtilities.FACE_DOWN_CARD;
import static com.poker.poker.utilities.CardUtilities.card;
import static com.poker.poker.utilities.PokerTableUtilities.dealCards;
import static com.poker.poker.utilities.PokerTableUtilities.determineWinners;
import static com.poker.poker.utilities.PokerTableUtilities.generateSidePots;
import static com.poker.poker.utilities.PokerTableUtilities.getPotTotal;
import static com.poker.poker.utilities.PokerTableUtilities.handlePlayerAction;
import static com.poker.poker.utilities.PokerTableUtilities.performBlindBets;
import static com.poker.poker.utilities.PokerTableUtilities.setupNewHand;
import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.poker.poker.models.enums.CardSuit;
import com.poker.poker.models.enums.CardValue;
import com.poker.poker.models.game.Card;
import com.poker.poker.models.game.Deck;
import com.poker.poker.models.game.GamePlayer;
import com.poker.poker.models.game.PokerTable;
import com.poker.poker.models.game.TableControls;
import com.poker.poker.models.game.Winner;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@Slf4j
@SuppressWarnings("MagicNumber")
public class PokerTableUtilitiesTests {

  public static final int SAMPLE_MIN_BANKROLL = 1000;
  public static final int SAMPLE_MAX_BANKROLL = 2000;
  public static final BigDecimal SAMPLE_BLIND = new BigDecimal(10);

  /**
   * Simulates a hand by setting up a table, then performing a sequence of actions. TODO: Document
   * the sequence of moves.
   *
   * @param table Poker table.
   * @param deck  Deck (can use a mocked deck to ensure players are given specific cards).
   */
  public static void performAndVerifyHandActionSequence_1(
      final PokerTable table, final Deck deck) {
    final List<GamePlayer> players = table.getPlayers();
    final GamePlayer p0 = players.get(0);
    final GamePlayer p1 = players.get(1);
    final GamePlayer p2 = players.get(2);
    final GamePlayer p3 = players.get(3);
    final GamePlayer p4 = players.get(4);
    final GamePlayer p5 = players.get(5);
    final GamePlayer p6 = players.get(6);
    final GamePlayer p7 = players.get(7);
    final GamePlayer p8 = players.get(8);
    final GamePlayer p9 = players.get(9);
    table.setRound(3);
    table.setDealer(8);
    final List<BigDecimal> chips =
        Stream.of(1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 400, 400)
            .map(BigDecimal::new)
            .collect(toList());
    for (int i = 0; i < players.size(); i++) {
      players.get(i).setChips(chips.get(i));
    }

    setupNewHand(table, deck);
    verifyHandAction(table, p1, p2, 980, 20, 20, 20, 30, false, false, 1, 2);

    final Collection<Integer> actingPlayers = new ArrayList<>();
    final Collection<Integer> playersThatActed = new ArrayList<>();

    // Player 2 raises 80 to 100.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Raise, p2.getId(), bd(80));
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p2, p3, 900, 100, 100, 100, 130, false, false, 1, 3);

    // Player 3 calls.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Call, p3.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p3, p4, 900, 100, 100, 100, 230, false, false, 1, 4);

    // Player 4 folds.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Fold, p4.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p4, p5, 1000, 0, 100, 100, 230, false, true, 1, 5);

    // Player 5 calls.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Call, p5.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p5, p6, 900, 100, 100, 100, 330, false, false, 1, 6);

    // Player 6 raises 100 to 200.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Raise, p6.getId(), bd(100));
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p6, p7, 800, 200, 200, 200, 530, false, false, 5, 7);

    // Player 7 raises 200 to 400.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Raise, p7.getId(), bd(200));
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p7, p8, 600, 400, 400, 400, 930, false, false, 6, 8);

    // Player 8 calls and goes all-in.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Call, p8.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p8, p9, 0, 400, 400, 400, 1330, true, false, 6, 9);

    // Player 9 calls and goes all-in.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Call, p9.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p9, p0, 0, 400, 400, 390, 1730, true, false, 6, 0);

    // Player 0 calls 400.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Call, p0.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p0, p1, 600, 400, 400, 380, 2120, false, false, 6, 1);

    // Player 1 folds.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Fold, p1.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p1, p2, 980, 20, 400, 300, 2120, false, true, 6, 2);

    // Player 2 folds.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Fold, p2.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p2, p3, 900, 100, 400, 300, 2120, false, true, 6, 3);

    // Player 3 calls 400.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Call, p3.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p3, p4, 600, 400, 400, 400, 2420, false, false, 6, 5);

    // Player 4 folded already, so we should bypass this player.
    verifyHandAction(table, p4, p5, 1000, 0, 400, 300, 2420, false, true, 6, 5);

    // Player 5 calls 400.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Call, p5.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p5, p6, 600, 400, 400, 200, 2720, false, false, 6, 6);

    // Player 6 raises by 600 to 1000, going all-in.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Raise, p6.getId(), bd(600));
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p6, p7, 0, 1000, 1000, 600, 3520, true, false, 5, 7);

    // Player 7 folds.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Fold, p7.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p7, p8, 600, 400, 1000, 0, 3520, false, true, 5, 8);

    // Player 8 performs an AllInCheck.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, AllInCheck, p8.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p8, p9, 0, 400, 1000, 0, 3520, true, false, 5, 9);

    // Player 9 performs an AllInCheck.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, AllInCheck, p9.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p9, p0, 0, 400, 1000, 600, 3520, true, false, 5, 0);

    // Player 0 folds.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Fold, p0.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p0, p1, 600, 400, 1000, 980, 3520, false, true, 5, 3);

    // Player 1 folded already, so we should bypass this player.
    verifyHandAction(table, p1, p2, 980, 20, 1000, 900, 3520, false, true, 5, 3);

    // Player 2 folded already, so we should bypass this player.
    verifyHandAction(table, p2, p3, 900, 100, 1000, 600, 3520, false, true, 5, 3);

    // Player 3 calls 1000.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Call, p3.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p3, p4, 0, 1000, 1000, 1000, 4120, true, false, 5, 5);

    // Player 4 folded already, so we should bypass this player.
    verifyHandAction(table, p4, p5, 1000, 0, 1000, 600, 4120, false, true, 5, 5);

    // Player 5 calls 1000.
    actingPlayers.add(table.getActingPlayer());
    handlePlayerAction(table, Call, p5.getId(), null);
    playersThatActed.add(table.getPlayerThatActed());

    // Verify post-conditions.
    verifyHandAction(table, p5, p6, 0, 1000, 1000, 0, 4720, true, false, 5, 6);

    // Betting round should be over, let's verify the check that would normally determine this.
    final boolean exitCondition = table.getPlayerThatActed() == table.getLastToAct();
    assertTrue(exitCondition);

    final Collection<Integer> expectedActingPlayers =
        asList(2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 5, 6, 7, 8, 9, 0, 3, 5);
    assertEquals(expectedActingPlayers, actingPlayers);
    assertEquals(expectedActingPlayers, playersThatActed);
  }

  public static void verifyHandAction(
      final PokerTable table,
      final GamePlayer player,
      final GamePlayer nextPlayer,
      final int expectedChips,
      final int expectedBet,
      final int expectedMinRaise,
      final int expectedToCall,
      final int expectedPotTotal,
      final boolean expectedAllinStatus,
      final boolean expectedFoldedStatus,
      final int expectedLastToAct,
      final int expectedActingPlayer) {
    assertEquals(bd(expectedChips), player.getChips());
    assertEquals(bd(expectedBet), player.getBet());
    assertEquals(bd(expectedMinRaise), table.getMinRaise());
    assertEquals(bd(expectedToCall), nextPlayer.getToCall());
    assertEquals(bd(expectedPotTotal), getPotTotal(table.getPots()));
    assertEquals(expectedAllinStatus, player.isAllIn());
    assertEquals(expectedFoldedStatus, player.isFolded());
    assertEquals(expectedLastToAct, table.getLastToAct());
    assertEquals(expectedActingPlayer, table.getActingPlayer());
  }

  public static BigDecimal bd(final int val) {
    return new BigDecimal(val);
  }

  public PokerTable getSamplePokerTable(final int numPlayers) {
    final PokerTable table = new PokerTable();
    table.setPlayers(getSamplePlayers(numPlayers, SAMPLE_MIN_BANKROLL, SAMPLE_MAX_BANKROLL));
    // Safe to assume this works
    table.setBlind(SAMPLE_BLIND);

    return table;
  }

  public GamePlayer getRandomPlayer(final int bankRollMin, final int bankRollMax) {
    final GamePlayer player = new GamePlayer();
    player.setId(UUID.randomUUID());
    player.setFirstName(RandomStringUtils.randomAlphabetic(10));
    player.setLastName(RandomStringUtils.randomAlphabetic(10));
    final TableControls controls = new TableControls();
    final int bankRollRange = bankRollMax - bankRollMin + 1;
    controls.setBankRoll(new BigDecimal(Math.round(Math.random() * bankRollRange + bankRollMin)));
    player.setControls(controls);
    return player;
  }

  /**
   * Creates a sequence of the following fake bets:
   *
   * <ol>
   *   <li>10
   *   <li>20
   *   <li>40
   *   <li>80
   * </ol>
   *
   * @param table Poker table.
   */
  public void createFakeBets1(final PokerTable table) {
    final List<GamePlayer> players = table.getPlayers();
    players.get(0).getControls().setCurrentBet(new BigDecimal(10));
    players.get(1).getControls().setCurrentBet(new BigDecimal(20));
    players.get(2).getControls().setCurrentBet(new BigDecimal(40));
    players.get(3).getControls().setCurrentBet(new BigDecimal(80));
  }

  /**
   * Creates a sequence of the following fake bets:
   *
   * <ol>
   *   <li>10
   *   <li>20
   *   <li>40
   *   <li>60, all-in
   *   <li>100
   *   <li>300
   *   <li>140, all-in
   *   <li>600
   * </ol>
   *
   * @param table Poker table.
   */
  public void createFakeBets2(final PokerTable table) {
    final List<GamePlayer> players = table.getPlayers();
    players.get(0).getControls().setCurrentBet(new BigDecimal(10));
    players.get(1).getControls().setCurrentBet(new BigDecimal(20));
    players.get(2).getControls().setCurrentBet(new BigDecimal(40));
    players.get(3).getControls().setCurrentBet(new BigDecimal(60));
    players.get(3).getControls().setBankRoll(ZERO);
    players.get(3).setAllIn(true);
    players.get(4).getControls().setCurrentBet(new BigDecimal(100));
    players.get(5).getControls().setCurrentBet(new BigDecimal(300));
    players.get(6).getControls().setCurrentBet(new BigDecimal(140));
    players.get(6).getControls().setBankRoll(ZERO);
    players.get(6).setAllIn(true);
    players.get(7).getControls().setCurrentBet(new BigDecimal(600));
  }

  /**
   * Creates a sequence of the following fake bets:
   *
   * <ol>
   *   <li>10
   *   <li>20
   *   <li>40
   *   <li>60, all-in
   *   <li>100
   *   <li>300
   *   <li>140, all-in
   *   <li>600
   *   <li>1200, all-in
   * </ol>
   *
   * @param table Poker table.
   */
  public void createFakeBets3(final PokerTable table) {
    final List<GamePlayer> players = table.getPlayers();
    players.get(0).getControls().setCurrentBet(new BigDecimal(10));
    players.get(1).getControls().setCurrentBet(new BigDecimal(20));
    players.get(2).getControls().setCurrentBet(new BigDecimal(40));
    players.get(3).getControls().setCurrentBet(new BigDecimal(60));
    players.get(3).getControls().setBankRoll(ZERO);
    players.get(3).setAllIn(true);
    players.get(4).getControls().setCurrentBet(new BigDecimal(100));
    players.get(5).getControls().setCurrentBet(new BigDecimal(300));
    players.get(6).getControls().setCurrentBet(new BigDecimal(140));
    players.get(6).getControls().setBankRoll(ZERO);
    players.get(6).setAllIn(true);
    players.get(7).getControls().setCurrentBet(new BigDecimal(600));
    players.get(8).getControls().setCurrentBet(new BigDecimal(1200));
    players.get(8).getControls().setBankRoll(ZERO);
    players.get(8).setAllIn(true);
  }

  /**
   * Creates a sequence of the following fake bets:
   *
   * <ol>
   *   <li>1200
   *   <li>2400
   *   <li>40
   *   <li>60, all-in
   *   <li>100
   *   <li>300
   *   <li>140, all-in
   *   <li>600
   *   <li>1200, all-in
   *   <li>1200
   * </ol>
   *
   * @param table Poker table.
   */
  public void createFakeBets4(final PokerTable table) {
    final List<GamePlayer> players = table.getPlayers();
    players.get(0).getControls().setCurrentBet(new BigDecimal(1200));
    players.get(1).getControls().setCurrentBet(new BigDecimal(2400));
    players.get(2).getControls().setCurrentBet(new BigDecimal(40));
    players.get(3).getControls().setCurrentBet(new BigDecimal(60));
    players.get(3).getControls().setBankRoll(ZERO);
    players.get(3).setAllIn(true);
    players.get(4).getControls().setCurrentBet(new BigDecimal(100));
    players.get(5).getControls().setCurrentBet(new BigDecimal(300));
    players.get(6).getControls().setCurrentBet(new BigDecimal(140));
    players.get(6).getControls().setBankRoll(ZERO);
    players.get(6).setAllIn(true);
    players.get(7).getControls().setCurrentBet(new BigDecimal(600));
    players.get(8).getControls().setCurrentBet(new BigDecimal(1200));
    players.get(8).getControls().setBankRoll(ZERO);
    players.get(8).setAllIn(true);
    players.get(9).getControls().setCurrentBet(new BigDecimal(1200));
  }

  /**
   * Creates a sequence of the following fake bets:
   *
   * <ol>
   *   <li>1000
   *   <li>500, all-in
   *   <li>400, all-in
   *   <li>200, all-in
   *   <li>100, all-in
   *   <li>1000
   *   <li>1000
   *   <li>300, all-in
   *   <li>1000
   *   <li>1000
   * </ol>
   *
   * <p>We want to make the bets so each of these players are all-in.
   *
   * <ol>
   *   <li>Player 4 - AS
   *   <li>Player 3 - JS
   *   <li>Player 7 - 10D
   *   <li>Player 2 - 9S
   *   <li>Player 1 - 7S
   *   <li>Player 5 - 6H
   *   <li>Player 6 - 5H
   *   <li>Player 8 - 3D
   *   <li>Player 0 - 2S
   *   <li>Player 9 - 2C
   * </ol>
   *
   * @param table Poker table.
   */
  public void createFakeBets5(final PokerTable table) {
    final List<GamePlayer> players = table.getPlayers();
    players.get(0).getControls().setCurrentBet(new BigDecimal(1000));
    players.get(1).getControls().setCurrentBet(new BigDecimal(500));
    players.get(1).getControls().setBankRoll(ZERO);
    players.get(1).setAllIn(true);
    players.get(2).getControls().setCurrentBet(new BigDecimal(400));
    players.get(2).getControls().setBankRoll(ZERO);
    players.get(2).setAllIn(true);
    players.get(3).getControls().setCurrentBet(new BigDecimal(200));
    players.get(3).getControls().setBankRoll(ZERO);
    players.get(3).setAllIn(true);
    players.get(4).getControls().setCurrentBet(new BigDecimal(100));
    players.get(4).getControls().setBankRoll(ZERO);
    players.get(4).setAllIn(true);
    players.get(5).getControls().setCurrentBet(new BigDecimal(1000));
    players.get(6).getControls().setCurrentBet(new BigDecimal(1000));
    players.get(7).getControls().setCurrentBet(new BigDecimal(300));
    players.get(7).getControls().setBankRoll(ZERO);
    players.get(7).setAllIn(true);
    players.get(8).getControls().setCurrentBet(new BigDecimal(1000));
    players.get(9).getControls().setCurrentBet(new BigDecimal(1000));
  }

  /**
   * Creates a scenario where all players but one have folded, to help test whether a winner can be
   * properly determined,
   *
   * @param table Poker table.
   */
  public void createAllButOneFoldedScenario(final PokerTable table) {
    final List<GamePlayer> players = table.getPlayers();
    players.forEach(
        p -> {
          p.getControls().setCurrentBet(new BigDecimal(1000));
          if (players.indexOf(p) != 0) {
            p.setFolded(true);
          }
        });
    createFakeHand(table);
    generateSidePots(table);
  }

  /**
   * Creates a fake hand.
   *
   * <ol>
   *   <li>Player 4 - AS
   *   <li>Player 3 - JS
   *   <li>Player 7 - 10D
   *   <li>Player 2 - 9S
   *   <li>Player 1 - 7S
   *   <li>Player 5 - 6H
   *   <li>Player 6 - 5H
   *   <li>Player 8 - 3D
   *   <li>Player 0 - 2S
   *   <li>Player 9 - 2C
   * </ol>
   *
   * @param table Poker table.
   */
  public void createFakeHand(final PokerTable table) {
    final List<Card> cards = getSampleCards();
    final List<GamePlayer> players = table.getPlayers();
    for (int i = 0; i < players.size(); i++) {
      players.get(i).setCards(Collections.singletonList(cards.get(i)));
    }
  }

  public List<GamePlayer> getSamplePlayers(
      final int numPlayers, final int bankRollMin, final int bankRollMax) {
    return IntStream.range(0, numPlayers)
        .mapToObj(i -> getRandomPlayer(bankRollMin, bankRollMax))
        .collect(toList());
  }

  public List<Card> getSampleCards() {
    return asList(
        new Card(Spades, CardValue.Two),
        new Card(Spades, CardValue.Seven),
        new Card(Spades, CardValue.Nine),
        new Card(Spades, CardValue.Jack),
        new Card(Spades, CardValue.Ace),
        new Card(CardSuit.Hearts, Six),
        new Card(CardSuit.Hearts, CardValue.Five),
        new Card(CardSuit.Diamonds, CardValue.Ten),
        new Card(CardSuit.Diamonds, CardValue.Three),
        new Card(CardSuit.Clubs, CardValue.Two));
  }

  /**
   * Creates a sample deck which is spy'd so that the <code>restoreAndShuffle()</code> method does
   * nothing, so that when the cards are dealt (using the <code>dealCards</code> method, we'll have:
   *
   * <p><b>Shared Cards:</b>
   *
   * <ol>
   *   <li>KS
   *   <li>QS
   *   <li>10S
   *   <li>KC
   *   <li>2D
   * </ol>
   *
   * <ol>
   *   <b>Player Hands:</b>
   *   <li>AS, JS => Straight flush
   *   <li>KD, KH => 4-of-a-kind
   *   <li>QH, QD => Queens and Kings Full House
   *   <li>2S, 9S => K/Q/10/9/2 Flush
   *   <li>3S, 8S => K/Q/10/8/3 Flush
   *   <li>AD, JC => 10->A Straight
   *   <li>AH, JH => 10->A Straight
   *   <li>JD, 9H => 9->K Straight
   *   <li>2H, 4H => KK + Q, 10, 4 Kickers
   *   <li>2C, 3C => KK + Q, 10, 3 Kickers
   * </ol>
   *
   * @return Sample deck with specified hands.
   */
  public Deck getSampleDeck_1() {
    final List<Card> cards =
        new ArrayList<>(
            asList(
                card(Spades, Six),
                card(Spades, Seven),
                card(Hearts, Three),
                card(Hearts, Two),
                card(Hearts, Five),
                card(Hearts, Six),
                card(Hearts, Seven),
                card(Hearts, Eight),
                card(Hearts, Ten),
                card(Clubs, Ace),
                card(Clubs, Two),
                card(Hearts, Four),
                card(Clubs, Six),
                card(Clubs, Seven),
                card(Clubs, Eight),
                card(Clubs, Nine),
                card(Clubs, Ten),
                card(Clubs, Queen),
                card(Diamonds, Four),
                card(Diamonds, Five),
                card(Diamonds, Six),
                card(Diamonds, Seven),
                card(Diamonds, Eight),
                card(Diamonds, Nine),
                card(Diamonds, Ten),
                card(Diamonds, Two), // River
                card(Spades, Five), // Burn
                card(Clubs, King), // Turn
                card(Spades, Four), // Burn
                card(Spades, Ten), // Flop
                card(Spades, Queen), // Flop
                card(Spades, King), // Flop
                card(Clubs, Four), // Dealer 2nd Card
                card(Clubs, Five), // Player 8 2nd Card
                card(Hearts, Nine), // Player 7 2nd Card
                card(Hearts, Jack), // Player 6 2nd Card
                card(Clubs, Jack), // Player 5 2nd Card
                card(Spades, Eight), // Player 4 2nd Card
                card(Spades, Nine), // Player 3 2nd Card
                card(Diamonds, Queen), // Player 2 2nd Card
                card(Hearts, King), // Player 1 2nd Card
                card(Spades, Jack), // Player 0 2nd Card = 41
                card(Clubs, Three), // Dealer 1st Card
                card(Diamonds, Three), // Player 8 1st Card
                card(Diamonds, Jack), // Player 7 1st Card
                card(Hearts, Ace), // Player 6 1st Card
                card(Diamonds, Ace), // Player 5 1st Card
                card(Spades, Three), // Player 4 1st Card
                card(Spades, Two), // Player 3 1st Card
                card(Hearts, Queen), // Player 2 1st Card
                card(Diamonds, King), // Player 1 1st Card
                card(Spades, Ace) // Player 0 1st Card = 51
                ));

    final Deck mockDeck = Mockito.spy(Deck.class);
    Mockito.doNothing().when(mockDeck).restoreAndShuffle();
    mockDeck.setCards(cards);
    return mockDeck;
  }

  /**
   * Creates a sample deck which is spy'd so that the <code>restoreAndShuffle()</code> method does
   * nothing, so that when the cards are dealt (using the <code>dealCards</code> method, we'll have
   * a royal flush on the board, meaning that all players will have the same value hand.
   *
   * @return Sample deck with specified hands.
   */
  public Deck getSampleDeck_2() {
    final List<Card> cards =
        new ArrayList<>(
            asList(
                card(Diamonds, Two),
                card(Clubs, King),
                card(Hearts, Three),
                card(Hearts, Five),
                card(Hearts, Six),
                card(Hearts, Seven),
                card(Hearts, Eight),
                card(Hearts, Ten),
                card(Clubs, Ace),
                card(Clubs, Four),
                card(Clubs, Five),
                card(Clubs, Six),
                card(Clubs, Seven),
                card(Clubs, Eight),
                card(Clubs, Nine),
                card(Clubs, Ten),
                card(Clubs, Queen),
                card(Diamonds, Three),
                card(Diamonds, Four),
                card(Diamonds, Five),
                card(Diamonds, Six),
                card(Diamonds, Seven),
                card(Diamonds, Eight),
                card(Diamonds, Nine),
                card(Diamonds, Ten),
                card(Spades, Ace), // River
                card(Spades, Five), // Burn
                card(Spades, Jack), // Turn
                card(Spades, Four), // Burn
                card(Spades, Ten), // Flop
                card(Spades, Queen), // Flop
                card(Spades, King), // Flop
                card(Clubs, Three), // Dealer 2nd Card
                card(Hearts, Four), // Player 8 2nd Card
                card(Hearts, Nine), // Player 7 2nd Card
                card(Hearts, Jack), // Player 6 2nd Card
                card(Clubs, Jack), // Player 5 2nd Card
                card(Spades, Eight), // Player 4 2nd Card
                card(Spades, Nine), // Player 3 2nd Card
                card(Diamonds, Queen), // Player 2 2nd Card
                card(Hearts, King), // Player 1 2nd Card
                card(Spades, Seven), // Player 0 2nd Card
                card(Clubs, Two), // Dealer 1st Card
                card(Hearts, Two), // Player 8 1st Card
                card(Diamonds, Jack), // Player 7 1st Card
                card(Hearts, Ace), // Player 6 1st Card
                card(Diamonds, Ace), // Player 5 1st Card
                card(Spades, Three), // Player 4 1st Card
                card(Spades, Two), // Player 3 1st Card
                card(Hearts, Queen), // Player 2 1st Card
                card(Diamonds, King), // Player 1 1st Card
                card(Spades, Two) // Player 0 1st Card
                ));

    final Deck mockDeck = Mockito.spy(Deck.class);
    Mockito.doNothing().when(mockDeck).restoreAndShuffle();
    mockDeck.setCards(cards);
    return mockDeck;
  }

  /** Basic test where there are no side-pots. */
  @Test
  public void testPotGeneration_1() {
    final PokerTable table = getSamplePokerTable(10);
    createFakeBets1(table);

    // Test.
    generateSidePots(table);

    // Verify.
    assertEquals(1, table.getPots().size());
    assertEquals(new BigDecimal(150), table.getPots().get(0).getTotal());
  }

  @Test
  public void testPotGeneration_2() {
    final PokerTable table = getSamplePokerTable(10);
    createFakeBets2(table);

    // Test.
    generateSidePots(table);

    // Verify.
    assertEquals(3, table.getPots().size());
    assertEquals(new BigDecimal(370), table.getPots().get(0).getTotal());
    assertEquals(new BigDecimal(280), table.getPots().get(1).getTotal());
    assertEquals(new BigDecimal(620), table.getPots().get(2).getTotal());
    assertEquals(new BigDecimal(60), table.getPots().get(0).getWager());
    assertEquals(new BigDecimal(140), table.getPots().get(1).getWager());
    assertEquals(new BigDecimal(600), table.getPots().get(2).getWager());
    assertEquals(new BigDecimal(1270), getPotTotal(table.getPots()));
  }

  @Test
  public void testPotGeneration_3() {
    final PokerTable table = getSamplePokerTable(10);
    createFakeBets3(table);

    // Test.
    generateSidePots(table);

    // Verify.
    assertEquals(3, table.getPots().size());
    assertEquals(new BigDecimal(430), table.getPots().get(0).getTotal());
    assertEquals(new BigDecimal(360), table.getPots().get(1).getTotal());
    assertEquals(new BigDecimal(1680), table.getPots().get(2).getTotal());
    assertEquals(new BigDecimal(60), table.getPots().get(0).getWager());
    assertEquals(new BigDecimal(140), table.getPots().get(1).getWager());
    assertEquals(new BigDecimal(1200), table.getPots().get(2).getWager());
    assertEquals(new BigDecimal(2470), getPotTotal(table.getPots()));
  }

  @Test
  public void testPotGeneration_4() {
    final PokerTable table = getSamplePokerTable(10);
    createFakeBets4(table);

    // Test.
    generateSidePots(table);

    // Verify.
    assertEquals(4, table.getPots().size());
    assertEquals(new BigDecimal(580), table.getPots().get(0).getTotal());
    assertEquals(new BigDecimal(600), table.getPots().get(1).getTotal());
    assertEquals(new BigDecimal(4860), table.getPots().get(2).getTotal());
    assertEquals(new BigDecimal(1200), table.getPots().get(3).getTotal());
    assertEquals(new BigDecimal(60), table.getPots().get(0).getWager());
    assertEquals(new BigDecimal(140), table.getPots().get(1).getWager());
    assertEquals(new BigDecimal(1200), table.getPots().get(2).getWager());
    assertEquals(new BigDecimal(2400), table.getPots().get(3).getWager());
    assertEquals(new BigDecimal(7240), getPotTotal(table.getPots()));
  }

  @Test
  public void testPotGeneration_5() {
    final PokerTable table = getSamplePokerTable(10);
    createFakeBets5(table);

    // Test.
    generateSidePots(table);

    // Verify.
    assertEquals(6, table.getPots().size());
    assertEquals(new BigDecimal(1000), table.getPots().get(0).getTotal());
    assertEquals(new BigDecimal(900), table.getPots().get(1).getTotal());
    assertEquals(new BigDecimal(800), table.getPots().get(2).getTotal());
    assertEquals(new BigDecimal(700), table.getPots().get(3).getTotal());
    assertEquals(new BigDecimal(600), table.getPots().get(4).getTotal());
    assertEquals(new BigDecimal(2500), table.getPots().get(5).getTotal());
    assertEquals(new BigDecimal(100), table.getPots().get(0).getWager());
    assertEquals(new BigDecimal(200), table.getPots().get(1).getWager());
    assertEquals(new BigDecimal(300), table.getPots().get(2).getWager());
    assertEquals(new BigDecimal(400), table.getPots().get(3).getWager());
    assertEquals(new BigDecimal(500), table.getPots().get(4).getWager());
    assertEquals(new BigDecimal(1000), table.getPots().get(5).getWager());
    assertEquals(new BigDecimal(6500), getPotTotal(table.getPots()));
  }

  @Test
  public void testDetermineWinners_1() {
    // Setup.
    final PokerTable table = getSamplePokerTable(10);
    final List<GamePlayer> players = table.getPlayers();
    final Deck deck = getSampleDeck_1();
    table.setDealer(9);
    dealCards(table, deck, 2);

    // Set bets and bankrolls

    // Player 0 is All-In, bet 100, has best hand
    players.get(0).setChips(ZERO);
    players.get(0).setBet(new BigDecimal(100));
    players.get(0).setAllIn(true);

    // Player 1 is All-In, bet 200, has 2nd best hand
    players.get(1).setChips(ZERO);
    players.get(1).setBet(new BigDecimal(200));
    players.get(1).setAllIn(true);

    // Player 2 folded, but bet 100 before folding.
    players.get(2).setChips(new BigDecimal(500));
    players.get(2).setFolded(true);
    players.get(2).setBet(new BigDecimal(100));

    // Player 3 has bet 300
    players.get(3).setChips(new BigDecimal(500));
    players.get(3).setBet(new BigDecimal(300));

    // Player 4 has bet 300
    players.get(4).setChips(new BigDecimal(500));
    players.get(4).setBet(new BigDecimal(300));

    // Both 5 and 6 have 10->A Straights.
    // Player 5 has bet 300
    players.get(5).setChips(new BigDecimal(500));
    players.get(5).setBet(new BigDecimal(300));

    // Player 6 has bet 300
    players.get(6).setChips(new BigDecimal(500));
    players.get(6).setBet(new BigDecimal(300));

    // Player 7 has bet 300, has 9->K Straight, so will lose to p5 and p6
    players.get(7).setChips(new BigDecimal(500));
    players.get(7).setBet(new BigDecimal(300));

    // Player 8 folded, but bet 200 before folding.
    players.get(8).setChips(new BigDecimal(500));
    players.get(8).setFolded(true);
    players.get(8).setBet(new BigDecimal(200));

    // Player 9 folded, but bet 200 before folding.
    players.get(9).setChips(new BigDecimal(500));
    players.get(9).setFolded(true);
    players.get(9).setBet(new BigDecimal(200));

    // Test.
    determineWinners(table);

    // Verify.
    // Expecting player 0 with straight flush to win 1000
    // Expecting player 1 with 4-of-a-kind to win 800
    // Expecting player 3 with K/Q/10/9/2 Flush to win 500
    // Winners should be sorted according to amount won.
    final List<Winner> winners = table.getWinners();
    assertEquals(new BigDecimal(1000), winners.get(0).getWinnings());
    assertEquals(new BigDecimal(800), winners.get(1).getWinnings());
    assertEquals(new BigDecimal(500), winners.get(2).getWinnings());
    assertEquals(players.get(0).getId(), winners.get(0).getId());
    assertEquals(players.get(1).getId(), winners.get(1).getId());
    assertEquals(players.get(3).getId(), winners.get(2).getId());
  }

  /** Case where all players tie (there is a royal flush on the board). */
  @Test
  public void testDetermineWinners_2() {
    // Setup.
    final PokerTable table = getSamplePokerTable(10);
    final List<GamePlayer> players = table.getPlayers();
    final Deck deck = getSampleDeck_2();
    table.setDealer(9);
    dealCards(table, deck, 2);

    // Set bets and bankrolls

    // Player 0 bet 500.
    players.forEach(
        p -> {
          p.setChips(new BigDecimal(500));
          p.setBet(new BigDecimal(500));
        });

    // Test.
    determineWinners(table);

    // Verify.
    // Expecting players 5 and 6 to tie with a 10->Ace straight, winning 1000 chips each.
    // Expecting player 7 to win the remaining chips with a 9->K straight.
    final List<Winner> winners = table.getWinners();
    winners.forEach(w -> assertEquals(new BigDecimal(500), w.getWinnings()));
    assertEquals(10, winners.size());
  }

  @Test
  public void testDetermineWinners_3() {
    // Setup.
    final PokerTable table = getSamplePokerTable(10);
    final List<GamePlayer> players = table.getPlayers();
    final Deck deck = getSampleDeck_2();
    table.setDealer(9);
    dealCards(table, deck, 2);

    // Set bets and bankrolls

    // Player 0 bet 100 and is all-in.
    players.get(0).setChips(ZERO);
    players.get(0).setBet(new BigDecimal(100));
    players.get(0).setAllIn(true);

    // Player 1 bet 100 and is all-in.
    players.get(1).setChips(ZERO);
    players.get(1).setBet(new BigDecimal(100));
    players.get(1).setAllIn(true);

    // Player 2 bet 200 and is all-in.
    players.get(2).setChips(ZERO);
    players.get(2).setBet(new BigDecimal(200));
    players.get(2).setAllIn(true);

    // Player 3 bet 200 and is all-in.
    players.get(3).setChips(ZERO);
    players.get(3).setBet(new BigDecimal(200));
    players.get(3).setAllIn(true);

    // Player 4 bet 300 and is all-in.
    players.get(4).setChips(ZERO);
    players.get(4).setBet(new BigDecimal(300));
    players.get(4).setAllIn(true);

    // Player 5 bet 400 and is all-in.
    players.get(5).setChips(ZERO);
    players.get(5).setBet(new BigDecimal(400));
    players.get(5).setAllIn(true);

    // Player 6 bet 500 and is all-in.
    players.get(6).setChips(ZERO);
    players.get(6).setBet(new BigDecimal(500));
    players.get(6).setAllIn(true);

    // Player 7 bet 600 and is all-in.
    players.get(7).setChips(ZERO);
    players.get(7).setBet(new BigDecimal(600));
    players.get(7).setAllIn(true);

    // Player 8 bet 500.
    players.get(8).setChips(new BigDecimal(500));
    players.get(8).setBet(new BigDecimal(1000));

    // Player 9 bet 500.
    players.get(9).setChips(new BigDecimal(500));
    players.get(9).setBet(new BigDecimal(1000));

    // Test.
    determineWinners(table);

    // Verify.
    // Expecting players 5 and 6 to tie with a 10->Ace straight, winning 1000 chips each.
    // Expecting player 7 to win the remaining chips with a 9->K straight.
    final List<Winner> winners = table.getWinners();
    assertEquals(new BigDecimal(1000), winners.get(0).getWinnings());
    assertEquals(new BigDecimal(1000), winners.get(1).getWinnings());
    assertEquals(new BigDecimal(600), winners.get(2).getWinnings());
    assertEquals(new BigDecimal(500), winners.get(3).getWinnings());
    assertEquals(new BigDecimal(400), winners.get(4).getWinnings());
    assertEquals(new BigDecimal(300), winners.get(5).getWinnings());
    assertEquals(new BigDecimal(200), winners.get(6).getWinnings());
    assertEquals(new BigDecimal(200), winners.get(7).getWinnings());
    assertEquals(new BigDecimal(100), winners.get(8).getWinnings());
    assertEquals(new BigDecimal(100), winners.get(9).getWinnings());
  }

  /** Case where we have a mix of all-ins, tied hands and some players have folded. */
  @Test
  public void testDetermineWinners_4() {
    // Setup.
    final PokerTable table = getSamplePokerTable(10);
    final List<GamePlayer> players = table.getPlayers();
    final Deck deck = getSampleDeck_2();
    table.setDealer(9);
    dealCards(table, deck, 2);

    // Set bets and bankrolls
    final List<BigDecimal> bets =
        Stream.of(1, 1, 2, 2, 5, 5, 5, 5, 10, 10)
            .map(i -> new BigDecimal(i * 100))
            .collect(toList());
    final List<BigDecimal> chips =
        Stream.of(0, 0, 0, 0, 500, 500, 500, 500, 500, 500).map(BigDecimal::new).collect(toList());
    final List<Boolean> allIn =
        Stream.of(true, true, true, true, false, false, false, false, false, false)
            .collect(toList());
    final List<Boolean> folded =
        Stream.of(false, false, false, false, true, true, true, true, false, false)
            .collect(toList());
    for (int i = 0; i < players.size(); i++) {
      players.get(i).setBet(bets.get(i));
      players.get(i).setChips(chips.get(i));
      players.get(i).setAllIn(allIn.get(i));
      players.get(i).setFolded(folded.get(i));
    }

    // Test.
    determineWinners(table);

    // Verify.
    // Expecting players 5 and 6 to tie with a 10->Ace straight, winning 1000 chips each.
    // Expecting player 7 to win the remaining chips with a 9->K straight.
    final List<Winner> winners = table.getWinners();
    assertEquals(6, winners.size());
    assertEquals(new BigDecimal(1766), winners.get(0).getWinnings());
    assertEquals(new BigDecimal(1766), winners.get(1).getWinnings());
    assertEquals(new BigDecimal(366), winners.get(2).getWinnings());
    assertEquals(new BigDecimal(366), winners.get(3).getWinnings());
    assertEquals(new BigDecimal(166), winners.get(4).getWinnings());
    assertEquals(new BigDecimal(166), winners.get(5).getWinnings());
    testDetermineWinners_5();
  }

  /**
   * Test where 9 players are all-in and the last player calls. We have a somewhat interesting case
   * here where hand ranks are <code>0 > 1 > 2 > 3 > 4 > 5 = 6 > 7 > 8 > 9</code>, but wagers are.
   * <code>0 > 1 > 2 > 3 > 4 > 5 > 6 > 7 > 8 > 9</code>. This results in player 5 receiving much
   * less than player 6, even though their hands have the same rank.
   *
   * <p>When players 5 and 6 tie, a relatively unintuitive situation in created where player 5 wins
   * only 250 chips and player 6 wins 650 chips, despite the fact that they have the same hand and
   * player 6 only wagered 100 chips more than player 5. If player 6 had a worse hand, then player 5
   * would win 500 and player 6 would win 400 and if player 6 had the better hand, player 6 would
   * win 900. In each case, these two players are entitled to a total of 900 chips between them. The
   * reason why player 6 receives so much more in the event of a tie is because the additional 100
   * chips wagered entitles player 6 to that 100 chips plus 100 extra chips from the other three
   * players with worse hands.
   */
  @Test
  public void testDetermineWinners_5() {
    // Setup.
    final PokerTable table = getSamplePokerTable(10);
    final List<GamePlayer> players = table.getPlayers();
    final Deck deck = getSampleDeck_1();
    table.setDealer(9);
    dealCards(table, deck, 2);

    // Set bets and bankrolls.
    final List<BigDecimal> bets =
        Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 9).map(i -> new BigDecimal(i * 100)).collect(toList());
    final List<BigDecimal> chips =
        Stream.of(0, 0, 0, 0, 0, 0, 0, 0, 0, 500).map(BigDecimal::new).collect(toList());
    final List<Boolean> allIn =
        Stream.of(true, true, true, true, true, true, true, true, true, false).collect(toList());
    for (int i = 0; i < players.size(); i++) {
      players.get(i).setBet(bets.get(i));
      players.get(i).setChips(chips.get(i));
      players.get(i).setAllIn(allIn.get(i));
    }

    // Test.
    determineWinners(table);

    // Verify.
    assertEquals(9, table.getWinners().size());
    assertEquals(
        Stream.of(1000, 900, 800, 700, 650, 600, 300, 250, 200)
            .map(BigDecimal::new)
            .collect(toList()),
        table.getWinners().stream().map(Winner::getWinnings).collect(toList()));
  }

  /** Case where all but one player have folded. */
  @Test
  public void testDetermineWinners_6() {
    // Setup.
    final PokerTable table = getSamplePokerTable(10);
    final List<GamePlayer> players = table.getPlayers();
    table.setDealer(9);
    dealCards(table, new Deck(), 2);

    // Set bets and bankrolls.
    players.forEach(p -> p.setChips(new BigDecimal(500)));
    players.forEach(p -> p.setFolded(true));
    players.forEach(p -> p.setBet(new BigDecimal(500)));
    players.get(0).setFolded(false);

    // Testing.
    determineWinners(table);

    // Verify.
    players.forEach(
        p ->
            assertEquals(
                players.indexOf(p) == 0 ? new BigDecimal(5500) : new BigDecimal(500),
                p.getChips()));
    assertEquals(1, table.getWinners().size());
    assertEquals(players.get(0).getId(), table.getWinners().get(0).getId());
    assertEquals(new BigDecimal(5000), table.getWinners().get(0).getWinnings());
    assertEquals(
        asList(FACE_DOWN_CARD, FACE_DOWN_CARD, FACE_DOWN_CARD, FACE_DOWN_CARD, FACE_DOWN_CARD),
        table.getWinners().get(0).getCards());
  }

  /** Common case where we have many wagers but only one winner. */
  @Test
  public void testDetermineWinners_7() {
    // Setup.
    final PokerTable table = getSamplePokerTable(10);
    final List<GamePlayer> players = table.getPlayers();
    table.setDealer(9);
    dealCards(table, getSampleDeck_1(), 2);

    // Set bets and bankrolls.
    players.forEach(p -> p.setChips(new BigDecimal(500)));
    players.forEach(p -> p.setBet(new BigDecimal(500)));
    players.get(0).setFolded(false);

    // Testing.
    determineWinners(table);

    // Verify.
    players.forEach(
        p ->
            assertEquals(
                players.indexOf(p) == 0 ? new BigDecimal(5500) : new BigDecimal(500),
                p.getChips()));
    assertEquals(1, table.getWinners().size());
    assertEquals(players.get(0).getId(), table.getWinners().get(0).getId());
    assertEquals(new BigDecimal(5000), table.getWinners().get(0).getWinnings());
  }

  /** Basic test of card dealing where all players are active in the hand. */
  @Test
  public void testDealCards_1() {
    // Setup.
    final PokerTable table = getSamplePokerTable(10);
    final List<GamePlayer> players = table.getPlayers();
    final Deck deck = new Deck();
    table.setDealer(9);

    // Test.
    dealCards(table, deck, 2);
    final List<Card> usedCards = deck.getUsedCards();

    // Verify.
    for (int i = 0; i < players.size(); i++) {
      assertEquals(usedCards.get(i), players.get(i).getCards().get(0));
      assertEquals(usedCards.get(i + 10), players.get(i).getCards().get(1));
      assertEquals(2, players.get(i).getCards().size());
    }

    assertEquals(usedCards.get(20), table.getSharedCards().get(0));
    assertEquals(usedCards.get(21), table.getSharedCards().get(1));
    assertEquals(usedCards.get(22), table.getSharedCards().get(2));
    assertEquals(usedCards.get(24), table.getSharedCards().get(3));
    assertEquals(usedCards.get(26), table.getSharedCards().get(4));
  }

  /**
   * Test of edge case where some players have been eliminated from the game. Ensuring that proper
   * players have their blinds posted.
   */
  @Test
  public void testPerformBlindBets_2() {
    // Setup.
    final PokerTable table = getSamplePokerTable(10);
    final List<GamePlayer> players = table.getPlayers();
    table.setDealer(3); // sb = 4, bb = 5
    players.get(4).setOut(true);
    players.get(5).setOut(true);
    players.get(7).setOut(true);
    players.get(8).setOut(true);
    players.get(9).setOut(true);
    players.get(0).setOut(true);
    table.setActingPlayer(6); // Satisfy Pre-Condition.
    final BigDecimal sbBankRollInitial = players.get(6).getControls().getBankRoll();
    final BigDecimal bbBankRollInitial = players.get(1).getControls().getBankRoll();
    final BigDecimal sb = table.getBlind();
    final BigDecimal bb = table.getBlind().add(table.getBlind());

    // Test.
    performBlindBets(table);

    // Verify.
    assertEquals(sb, players.get(6).getControls().getCurrentBet());
    assertEquals(bb, players.get(1).getControls().getCurrentBet());
    assertEquals(sbBankRollInitial.subtract(sb), players.get(6).getControls().getBankRoll());
    assertEquals(bbBankRollInitial.subtract(bb), players.get(1).getControls().getBankRoll());
    assertEquals(2, table.getActingPlayer());
    players.forEach(
        p -> assertEquals(bb, p.getControls().getToCall().add(p.getControls().getCurrentBet())));
    assertEquals(ZERO, players.get(1).getControls().getToCall());
    assertEquals(sb, players.get(6).getControls().getToCall());
    assertEquals(1, table.getPots().size());
    assertEquals(sb.add(bb), table.getPots().get(0).getTotal());
    assertEquals(bb, table.getMinRaise());
  }

  /**
   * Test of edge case where the BB player doesn't have enough chips to post the full blinds, but
   * has more than the SB.
   */
  @Test
  public void testPerformBlindBets_3() {
    // Setup.
    final PokerTable table = getSamplePokerTable(10);
    final List<GamePlayer> players = table.getPlayers();
    table.setDealer(3); // sb = 4, bb = 5
    table.setActingPlayer(4); // Satisfy Pre-Condition.
    final BigDecimal bbBankRollInitial = new BigDecimal(15);
    players.get(5).getControls().setBankRoll(bbBankRollInitial);
    final BigDecimal sbBankRollInitial = players.get(4).getControls().getBankRoll();

    final BigDecimal sb = table.getBlind();
    final BigDecimal bb = table.getBlind().add(table.getBlind());

    // Test.
    performBlindBets(table);

    // Verify.
    assertEquals(sb, players.get(4).getControls().getCurrentBet());
    assertEquals(bbBankRollInitial, players.get(5).getControls().getCurrentBet());
    assertEquals(sbBankRollInitial.subtract(sb), players.get(4).getControls().getBankRoll());
    assertEquals(ZERO, players.get(5).getControls().getBankRoll());
    assertEquals(6, table.getActingPlayer());
    Assertions.assertTrue(players.get(5).isAllIn());
    assertEquals(ZERO, players.get(5).getControls().getToCall());
    assertEquals(bbBankRollInitial.subtract(sb), players.get(4).getControls().getToCall());
    assertEquals(1, table.getPots().size());
    assertEquals(sb.add(bbBankRollInitial), table.getPots().get(0).getTotal());
    assertEquals(bb, table.getMinRaise());
  }

  /** Test of card dealing where some players are not active in the hand. */
  @Test
  public void testDealCards_2() {
    // Setup.
    final PokerTable table = getSamplePokerTable(10);
    final List<GamePlayer> players = table.getPlayers();
    final Deck deck = new Deck();
    table.setDealer(9);
    // Eliminate 3 players
    players.get(2).setOut(true);
    players.get(1).setOut(true);
    players.get(6).setOut(true);

    // Test.
    dealCards(table, deck, 2);
    final List<Card> usedCards = deck.getUsedCards();

    // Verify.
    int j = 0;
    int i = 0;
    while (j < 7) {
      if (!players.get(i).isOut()) {
        assertEquals(usedCards.get(j), players.get(i).getCards().get(0));
        assertEquals(usedCards.get(j + 7), players.get(i).getCards().get(1));
        j++;
      }
      i++;
    }
    assertEquals(usedCards.get(14), table.getSharedCards().get(0));
    assertEquals(usedCards.get(15), table.getSharedCards().get(1));
    assertEquals(usedCards.get(16), table.getSharedCards().get(2));
    assertEquals(usedCards.get(18), table.getSharedCards().get(3));
    assertEquals(usedCards.get(20), table.getSharedCards().get(4));
  }

  @Test
  public void testDealCards_3() {
    // Setup.
    final PokerTable table = getSamplePokerTable(10);
    final List<GamePlayer> players = table.getPlayers();
    final Deck deck = new Deck();
    table.setDealer(9);
    // Eliminate 3 players
    players.get(2).setOut(true);
    players.get(1).setOut(true);
    players.get(6).setOut(true);
    players.get(7).setOut(true);
    players.get(8).setOut(true);
    players.get(0).setOut(true);

    // Test.
    dealCards(table, deck, 2);
    final List<Card> usedCards = deck.getUsedCards();

    // Verify.
    int j = 0;
    int i = 0;
    while (j < 4) {
      if (!players.get(i).isOut()) {
        assertEquals(usedCards.get(j), players.get(i).getCards().get(0));
        assertEquals(usedCards.get(j + 4), players.get(i).getCards().get(1));
        j++;
      }
      i++;
    }
    assertEquals(usedCards.get(8), table.getSharedCards().get(0));
    assertEquals(usedCards.get(9), table.getSharedCards().get(1));
    assertEquals(usedCards.get(10), table.getSharedCards().get(2));
    assertEquals(usedCards.get(12), table.getSharedCards().get(3));
    assertEquals(usedCards.get(14), table.getSharedCards().get(4));
  }

  /** Basic test of general case where sb and bb both have enough chips to post their blinds. */
  @Test
  public void testPerformBlindBets_1() {
    // Setup.
    final PokerTable table = getSamplePokerTable(10);
    final List<GamePlayer> players = table.getPlayers();
    table.setDealer(3); // sb = 4, bb = 5
    table.setActingPlayer(4); // Satisfy Pre-Condition.
    final BigDecimal sbBankRollInitial = players.get(4).getControls().getBankRoll();
    final BigDecimal bbBankRollInitial = players.get(5).getControls().getBankRoll();
    final BigDecimal sb = table.getBlind();
    final BigDecimal bb = table.getBlind().add(table.getBlind());

    // Test.
    performBlindBets(table);

    // Verify.
    assertEquals(sb, players.get(4).getControls().getCurrentBet());
    assertEquals(bb, players.get(5).getControls().getCurrentBet());
    assertEquals(sbBankRollInitial.subtract(sb), players.get(4).getControls().getBankRoll());
    assertEquals(bbBankRollInitial.subtract(bb), players.get(5).getControls().getBankRoll());
    assertEquals(6, table.getActingPlayer());
    players.forEach(
        p -> assertEquals(bb, p.getControls().getToCall().add(p.getControls().getCurrentBet())));
    assertEquals(ZERO, players.get(5).getControls().getToCall());
    assertEquals(sb, players.get(4).getControls().getToCall());
    assertEquals(1, table.getPots().size());
    assertEquals(sb.add(bb), table.getPots().get(0).getTotal());
    assertEquals(bb, table.getMinRaise());
  }

  /**
   * Test of edge case where the BB player doesn't have enough chips to post the full blinds, but
   * has less than the SB.
   */
  @Test
  public void testPerformBlindBets_4() {
    // Setup.
    final PokerTable table = getSamplePokerTable(10);
    final List<GamePlayer> players = table.getPlayers();
    table.setDealer(3); // sb = 4, bb = 5
    table.setActingPlayer(4); // Satisfy Pre-Condition.
    final BigDecimal bbBankRollInitial = new BigDecimal(5);
    players.get(5).getControls().setBankRoll(bbBankRollInitial);
    final BigDecimal sbBankRollInitial = players.get(4).getControls().getBankRoll();

    final BigDecimal sb = table.getBlind();
    final BigDecimal bb = table.getBlind().add(table.getBlind());

    // Test.
    performBlindBets(table);

    // Verify.
    assertEquals(sb, players.get(4).getControls().getCurrentBet());
    assertEquals(bbBankRollInitial, players.get(5).getControls().getCurrentBet());
    assertEquals(sbBankRollInitial.subtract(sb), players.get(4).getControls().getBankRoll());
    assertEquals(ZERO, players.get(5).getControls().getBankRoll());
    assertEquals(6, table.getActingPlayer());
    Assertions.assertTrue(players.get(5).isAllIn());
    assertEquals(ZERO, players.get(5).getControls().getToCall());
    assertEquals(ZERO, players.get(4).getControls().getToCall());
    assertEquals(2, table.getPots().size());
    assertEquals(bbBankRollInitial.add(bbBankRollInitial), table.getPots().get(0).getTotal());
    assertEquals(bbBankRollInitial.add(sb), getPotTotal(table.getPots()));
    assertEquals(bb, table.getMinRaise());
  }

  /**
   * Test to ensure Post-Conditions are satisfied.
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
   */
  @Test
  public void testNewHandSetup_1() {
    final PokerTable table = getSamplePokerTable(10);
    final List<GamePlayer> players = table.getPlayers();
    final Deck deck = new Deck();
    final List<Card> dealtCards = deck.peek(10);

    table.setDealer(0);
    table.setRound(0);
    final BigDecimal sb = table.getBlind();
    final BigDecimal pot = sb.add(sb).add(sb);

    // Test.
    setupNewHand(table, deck);

    // Verify.
    assertEquals(1, table.getRound());
    assertEquals(pot, getPotTotal(table.getPots()));
    assertEquals(sb, players.get(2).getControls().getCurrentBet());
    assertEquals(sb.add(sb), players.get(3).getControls().getCurrentBet());
    players.stream()
        .filter(p -> players.indexOf(p) != 2 && players.indexOf(p) != 3)
        .forEach(p -> assertEquals(sb.add(sb), p.getControls().getToCall()));
    assertEquals(4, table.getActingPlayer());
    // TODO: Check that cards were dealt
  }

  /** Ensuring the blinds are increased */
  @Test
  public void testNewHandSetup_2() {
    final PokerTable table = getSamplePokerTable(10);
    final Deck deck = new Deck();

    table.setRound(-1);
    final BigDecimal sb = table.getBlind();

    // Test.
    setupNewHand(table, deck);

    // Verify.
    assertEquals(sb.add(sb), table.getBlind());
    assertEquals(sb.multiply(new BigDecimal(6)), getPotTotal(table.getPots()));
  }

  /**
   * Simulates some actions and ensures they were handled correctly. TODO: Document the sequence of
   * moves
   */
  @Test
  public void testHandleHandAction_1() {
    final PokerTable table = getSamplePokerTable(10);
    final Deck deck = getSampleDeck_1();

    performAndVerifyHandActionSequence_1(table, deck);
  }

  @Test
  public void testFullHand_1() {
    final PokerTable table = getSamplePokerTable(10);
    final Deck deck = getSampleDeck_1();

    // Test.
    performAndVerifyHandActionSequence_1(table, deck);
    determineWinners(table);

    // Verify.
    final List<Winner> winners = table.getWinners();
    assertEquals(1, winners.size());
    assertEquals(bd(4720), winners.get(0).getWinnings());
    assertEquals(table.getPlayers().get(3).getId(), winners.get(0).getId());
  }

  /**
   * Give players 5 and 6 a royal flush to ensure they win the hand. The pot should be split 2 ways,
   * since the players have hands with equal rank.
   */
  @Test
  public void testFullHand_2() {
    final PokerTable table = getSamplePokerTable(10);
    final Deck deck = getSampleDeck_1();
    final GamePlayer p5 = table.getPlayers().get(5);
    final GamePlayer p6 = table.getPlayers().get(6);

    //    card(Clubs, Four),  // Dealer 2nd Card
    //    card(Clubs, Five), // Player 8 2nd Card
    //    card(Hearts, Nine), // Player 7 2nd Card
    //    card(Hearts, Jack), // Player 6 2nd Card
    //    card(Clubs, Jack), // Player 5 2nd Card
    //    card(Spades, Eight), // Player 4 2nd Card
    //    card(Spades, Nine), // Player 3 2nd Card
    //    card(Diamonds, Queen), // Player 2 2nd Card
    //    card(Hearts, King), // Player 1 2nd Card
    //    card(Spades, Jack), // Player 0 2nd Card = 41
    //    card(Clubs, Three),  // Dealer 1st Card = 42
    //    card(Diamonds, Three), // Player 8 1st Card = 43
    //    card(Diamonds, Jack), // Player 7 1st Card = 44
    //    card(Hearts, Ace), // Player 6 1st Card = 45
    //    card(Diamonds, Ace), // Player 5 1st Card = 46
    //    card(Spades, Three), // Player 4 1st Card = 47
    //    card(Spades, Two), // Player 3 1st Card = 48
    //    card(Hearts, Queen), // Player 2 1st Card = 49
    //    card(Diamonds, King), // Player 1 1st Card = 50
    //    card(Spades, Ace) // Player 0 1st Card = 51

    deck.getCards().set(46, card(Spades, Ace));
    deck.getCards().set(36, card(Spades, Jack));
    deck.getCards().set(45, card(Spades, Ace));
    deck.getCards().set(35, card(Spades, Jack));

    // Test.
    performAndVerifyHandActionSequence_1(table, deck);
    determineWinners(table);

    // Verify.
    final List<Winner> winners = table.getWinners();
    assertEquals(2, winners.size());
    assertEquals(bd(4720 / 2), winners.get(0).getWinnings());
    assertEquals(bd(4720 / 2), winners.get(1).getWinnings());
    assertEquals(1, winners.stream().filter(w -> w.getId().equals(p5.getId())).count());
    assertEquals(1, winners.stream().filter(w -> w.getId().equals(p6.getId())).count());
  }

  /**
   * Give all players a royal flush (on the board), so any player that doesn't fold will win a share
   * of the pot. Since 2 players only have 400 chips, while the other 3 players that win have bet
   * 1000, there will be 2 players that win less than the other 3.
   */
  @Test
  public void testFullHand_3() {
    final PokerTable table = getSamplePokerTable(10);
    final Deck deck = getSampleDeck_2();
    final GamePlayer p3 = table.getPlayers().get(3);
    final GamePlayer p5 = table.getPlayers().get(5);
    final GamePlayer p6 = table.getPlayers().get(6);
    final GamePlayer p8 = table.getPlayers().get(8);
    final GamePlayer p9 = table.getPlayers().get(9);

    // Test.
    performAndVerifyHandActionSequence_1(table, deck);
    determineWinners(table);

    // Verify.
    final List<Winner> winners = table.getWinners();
    assertEquals(5, winners.size());
    assertEquals(bd(4720), sum(winners.stream().map(Winner::getWinnings).collect(toList())));
    assertEquals(3, winners.stream().filter(w -> w.getWinnings().equals(bd(1184))).count());
    assertEquals(2, winners.stream().filter(w -> w.getWinnings().equals(bd(584))).count());

    assertEquals(1, winners.stream().filter(w -> w.getId().equals(p3.getId())).count());
    assertEquals(1, winners.stream().filter(w -> w.getId().equals(p5.getId())).count());
    assertEquals(1, winners.stream().filter(w -> w.getId().equals(p6.getId())).count());
    assertEquals(1, winners.stream().filter(w -> w.getId().equals(p8.getId())).count());
    assertEquals(1, winners.stream().filter(w -> w.getId().equals(p9.getId())).count());

    assertEquals(
        bd(1184),
        Objects.requireNonNull(
                winners.stream().filter(w -> w.getId().equals(p3.getId())).findFirst().orElse(null))
            .getWinnings());
    assertEquals(
        bd(1184),
        Objects.requireNonNull(
                winners.stream().filter(w -> w.getId().equals(p5.getId())).findFirst().orElse(null))
            .getWinnings());
    assertEquals(
        bd(1184),
        Objects.requireNonNull(
                winners.stream().filter(w -> w.getId().equals(p6.getId())).findFirst().orElse(null))
            .getWinnings());
    assertEquals(
        bd(584),
        Objects.requireNonNull(
                winners.stream().filter(w -> w.getId().equals(p8.getId())).findFirst().orElse(null))
            .getWinnings());
    assertEquals(
        bd(584),
        Objects.requireNonNull(
                winners.stream().filter(w -> w.getId().equals(p9.getId())).findFirst().orElse(null))
            .getWinnings());
  }
}
