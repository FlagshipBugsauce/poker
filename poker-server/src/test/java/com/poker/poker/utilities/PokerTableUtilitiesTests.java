package com.poker.poker.utilities;

import com.poker.poker.models.enums.CardSuit;
import com.poker.poker.models.enums.CardValue;
import com.poker.poker.models.enums.GameAction;
import com.poker.poker.models.game.CardModel;
import com.poker.poker.models.game.DeckModel;
import com.poker.poker.models.game.GamePlayerModel;
import com.poker.poker.models.game.PokerTableModel;
import com.poker.poker.models.game.PotModel;
import com.poker.poker.models.game.TableControlsModel;
import com.poker.poker.models.game.WinnerModel;
import com.poker.poker.services.game.CardService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
@SuppressWarnings("MagicNumber")
public class PokerTableUtilitiesTests {

  public static final int SAMPLE_MIN_BANKROLL = 1000;
  public static final int SAMPLE_MAX_BANKROLL = 2000;
  public static final BigDecimal SAMPLE_BLIND = new BigDecimal(10);

  public CardService cardService = new CardService();

  public PokerTableModel getSamplePokerTable(final int numPlayers) {
    final PokerTableModel table = new PokerTableModel();
    table.setPlayers(getSamplePlayers(numPlayers, SAMPLE_MIN_BANKROLL, SAMPLE_MAX_BANKROLL));
    // Safe to assume this works
    table.setBlind(SAMPLE_BLIND);

    return table;
  }

  public List<GamePlayerModel> getSamplePlayers(
      final int numPlayers, final int bankRollMin, final int bankRollMax) {
    return IntStream.range(0, numPlayers)
        .mapToObj(i -> getRandomPlayer(bankRollMin, bankRollMax))
        .collect(Collectors.toList());
  }

  public GamePlayerModel getRandomPlayer(final int bankRollMin, final int bankRollMax) {
    final GamePlayerModel player = new GamePlayerModel();
    player.setId(UUID.randomUUID());
    player.setFirstName(RandomStringUtils.randomAlphabetic(10));
    player.setLastName(RandomStringUtils.randomAlphabetic(10));
    final TableControlsModel controls = new TableControlsModel();
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
  public void createFakeBets1(final PokerTableModel table) {
    final List<GamePlayerModel> players = table.getPlayers();
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
  public void createFakeBets2(final PokerTableModel table) {
    final List<GamePlayerModel> players = table.getPlayers();
    players.get(0).getControls().setCurrentBet(new BigDecimal(10));
    players.get(1).getControls().setCurrentBet(new BigDecimal(20));
    players.get(2).getControls().setCurrentBet(new BigDecimal(40));
    players.get(3).getControls().setCurrentBet(new BigDecimal(60));
    players.get(3).getControls().setBankRoll(BigDecimal.ZERO);
    players.get(3).setAllIn(true);
    players.get(4).getControls().setCurrentBet(new BigDecimal(100));
    players.get(5).getControls().setCurrentBet(new BigDecimal(300));
    players.get(6).getControls().setCurrentBet(new BigDecimal(140));
    players.get(6).getControls().setBankRoll(BigDecimal.ZERO);
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
  public void createFakeBets3(final PokerTableModel table) {
    final List<GamePlayerModel> players = table.getPlayers();
    players.get(0).getControls().setCurrentBet(new BigDecimal(10));
    players.get(1).getControls().setCurrentBet(new BigDecimal(20));
    players.get(2).getControls().setCurrentBet(new BigDecimal(40));
    players.get(3).getControls().setCurrentBet(new BigDecimal(60));
    players.get(3).getControls().setBankRoll(BigDecimal.ZERO);
    players.get(3).setAllIn(true);
    players.get(4).getControls().setCurrentBet(new BigDecimal(100));
    players.get(5).getControls().setCurrentBet(new BigDecimal(300));
    players.get(6).getControls().setCurrentBet(new BigDecimal(140));
    players.get(6).getControls().setBankRoll(BigDecimal.ZERO);
    players.get(6).setAllIn(true);
    players.get(7).getControls().setCurrentBet(new BigDecimal(600));
    players.get(8).getControls().setCurrentBet(new BigDecimal(1200));
    players.get(8).getControls().setBankRoll(BigDecimal.ZERO);
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
  public void createFakeBets4(final PokerTableModel table) {
    final List<GamePlayerModel> players = table.getPlayers();
    players.get(0).getControls().setCurrentBet(new BigDecimal(1200));
    players.get(1).getControls().setCurrentBet(new BigDecimal(2400));
    players.get(2).getControls().setCurrentBet(new BigDecimal(40));
    players.get(3).getControls().setCurrentBet(new BigDecimal(60));
    players.get(3).getControls().setBankRoll(BigDecimal.ZERO);
    players.get(3).setAllIn(true);
    players.get(4).getControls().setCurrentBet(new BigDecimal(100));
    players.get(5).getControls().setCurrentBet(new BigDecimal(300));
    players.get(6).getControls().setCurrentBet(new BigDecimal(140));
    players.get(6).getControls().setBankRoll(BigDecimal.ZERO);
    players.get(6).setAllIn(true);
    players.get(7).getControls().setCurrentBet(new BigDecimal(600));
    players.get(8).getControls().setCurrentBet(new BigDecimal(1200));
    players.get(8).getControls().setBankRoll(BigDecimal.ZERO);
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
  public void createFakeBets5(final PokerTableModel table) {
    final List<GamePlayerModel> players = table.getPlayers();
    players.get(0).getControls().setCurrentBet(new BigDecimal(1000));
    players.get(1).getControls().setCurrentBet(new BigDecimal(500));
    players.get(1).getControls().setBankRoll(BigDecimal.ZERO);
    players.get(1).setAllIn(true);
    players.get(2).getControls().setCurrentBet(new BigDecimal(400));
    players.get(2).getControls().setBankRoll(BigDecimal.ZERO);
    players.get(2).setAllIn(true);
    players.get(3).getControls().setCurrentBet(new BigDecimal(200));
    players.get(3).getControls().setBankRoll(BigDecimal.ZERO);
    players.get(3).setAllIn(true);
    players.get(4).getControls().setCurrentBet(new BigDecimal(100));
    players.get(4).getControls().setBankRoll(BigDecimal.ZERO);
    players.get(4).setAllIn(true);
    players.get(5).getControls().setCurrentBet(new BigDecimal(1000));
    players.get(6).getControls().setCurrentBet(new BigDecimal(1000));
    players.get(7).getControls().setCurrentBet(new BigDecimal(300));
    players.get(7).getControls().setBankRoll(BigDecimal.ZERO);
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
  public void createAllButOneFoldedScenario(final PokerTableModel table) {
    final List<GamePlayerModel> players = table.getPlayers();
    players.forEach(
        p -> {
          p.getControls().setCurrentBet(new BigDecimal(1000));
          if (players.indexOf(p) != 0) {
            p.setFolded(true);
          }
        });
    createFakeHand(table);
    PokerTableUtilities.generateSidePots(table);
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
  public void createFakeHand(final PokerTableModel table) {
    final List<CardModel> cards = getSampleCards();
    final List<GamePlayerModel> players = table.getPlayers();
    for (int i = 0; i < players.size(); i++) {
      players.get(i).setCards(Collections.singletonList(cards.get(i)));
    }
  }

  public List<CardModel> getSampleCards() {
    return Arrays.asList(
        new CardModel(CardSuit.Spades, CardValue.Two),
        new CardModel(CardSuit.Spades, CardValue.Seven),
        new CardModel(CardSuit.Spades, CardValue.Nine),
        new CardModel(CardSuit.Spades, CardValue.Jack),
        new CardModel(CardSuit.Spades, CardValue.Ace),
        new CardModel(CardSuit.Hearts, CardValue.Six),
        new CardModel(CardSuit.Hearts, CardValue.Five),
        new CardModel(CardSuit.Diamonds, CardValue.Ten),
        new CardModel(CardSuit.Diamonds, CardValue.Three),
        new CardModel(CardSuit.Clubs, CardValue.Two));
  }

  /** Basic test where there are no side-pots. */
  @Test
  public void testPotGeneration_1() {
    final PokerTableModel table = getSamplePokerTable(10);
    createFakeBets1(table);

    // Test.
    PokerTableUtilities.generateSidePots(table);

    // Verify.
    Assertions.assertEquals(1, table.getPots().size());
    Assertions.assertEquals(new BigDecimal(150), table.getPots().get(0).getTotal());
  }

  @Test
  public void testPotGeneration_2() {
    final PokerTableModel table = getSamplePokerTable(10);
    createFakeBets2(table);

    // Test.
    PokerTableUtilities.generateSidePots(table);

    // Verify.
    Assertions.assertEquals(3, table.getPots().size());
    Assertions.assertEquals(new BigDecimal(370), table.getPots().get(0).getTotal());
    Assertions.assertEquals(new BigDecimal(280), table.getPots().get(1).getTotal());
    Assertions.assertEquals(new BigDecimal(620), table.getPots().get(2).getTotal());
    Assertions.assertEquals(new BigDecimal(60), table.getPots().get(0).getWager());
    Assertions.assertEquals(new BigDecimal(140), table.getPots().get(1).getWager());
    Assertions.assertEquals(new BigDecimal(600), table.getPots().get(2).getWager());
    Assertions.assertEquals(
        new BigDecimal(1270), PokerTableUtilities.getTotalInAllSidePots(table.getPots()));
  }

  @Test
  public void testPotGeneration_3() {
    final PokerTableModel table = getSamplePokerTable(10);
    createFakeBets3(table);

    // Test.
    PokerTableUtilities.generateSidePots(table);

    // Verify.
    Assertions.assertEquals(3, table.getPots().size());
    Assertions.assertEquals(new BigDecimal(430), table.getPots().get(0).getTotal());
    Assertions.assertEquals(new BigDecimal(360), table.getPots().get(1).getTotal());
    Assertions.assertEquals(new BigDecimal(1680), table.getPots().get(2).getTotal());
    Assertions.assertEquals(new BigDecimal(60), table.getPots().get(0).getWager());
    Assertions.assertEquals(new BigDecimal(140), table.getPots().get(1).getWager());
    Assertions.assertEquals(new BigDecimal(1200), table.getPots().get(2).getWager());
    Assertions.assertEquals(
        new BigDecimal(2470), PokerTableUtilities.getTotalInAllSidePots(table.getPots()));
  }

  @Test
  public void testPotGeneration_4() {
    final PokerTableModel table = getSamplePokerTable(10);
    createFakeBets4(table);

    // Test.
    PokerTableUtilities.generateSidePots(table);

    // Verify.
    Assertions.assertEquals(4, table.getPots().size());
    Assertions.assertEquals(new BigDecimal(580), table.getPots().get(0).getTotal());
    Assertions.assertEquals(new BigDecimal(600), table.getPots().get(1).getTotal());
    Assertions.assertEquals(new BigDecimal(4860), table.getPots().get(2).getTotal());
    Assertions.assertEquals(new BigDecimal(1200), table.getPots().get(3).getTotal());
    Assertions.assertEquals(new BigDecimal(60), table.getPots().get(0).getWager());
    Assertions.assertEquals(new BigDecimal(140), table.getPots().get(1).getWager());
    Assertions.assertEquals(new BigDecimal(1200), table.getPots().get(2).getWager());
    Assertions.assertEquals(new BigDecimal(2400), table.getPots().get(3).getWager());
    Assertions.assertEquals(
        new BigDecimal(7240), PokerTableUtilities.getTotalInAllSidePots(table.getPots()));
  }

  @Test
  public void testPotGeneration_5() {
    final PokerTableModel table = getSamplePokerTable(10);
    createFakeBets5(table);

    // Test.
    PokerTableUtilities.generateSidePots(table);

    // Verify.
    Assertions.assertEquals(6, table.getPots().size());
    Assertions.assertEquals(new BigDecimal(1000), table.getPots().get(0).getTotal());
    Assertions.assertEquals(new BigDecimal(900), table.getPots().get(1).getTotal());
    Assertions.assertEquals(new BigDecimal(800), table.getPots().get(2).getTotal());
    Assertions.assertEquals(new BigDecimal(700), table.getPots().get(3).getTotal());
    Assertions.assertEquals(new BigDecimal(600), table.getPots().get(4).getTotal());
    Assertions.assertEquals(new BigDecimal(2500), table.getPots().get(5).getTotal());
    Assertions.assertEquals(new BigDecimal(100), table.getPots().get(0).getWager());
    Assertions.assertEquals(new BigDecimal(200), table.getPots().get(1).getWager());
    Assertions.assertEquals(new BigDecimal(300), table.getPots().get(2).getWager());
    Assertions.assertEquals(new BigDecimal(400), table.getPots().get(3).getWager());
    Assertions.assertEquals(new BigDecimal(500), table.getPots().get(4).getWager());
    Assertions.assertEquals(new BigDecimal(1000), table.getPots().get(5).getWager());
    Assertions.assertEquals(
        new BigDecimal(6500), PokerTableUtilities.getTotalInAllSidePots(table.getPots()));
  }

  /**
   * Tests a relatively complex situation with 6 side-pots, to ensure that each winner receives the
   * appropriate amount of chips and the winners are selected correctly.
   *
   * <p>Unfortunately this test will be obsolete once we moved to 2-card hands.
   */
  @Test
  public void testDetermineWinners_1() {
    // Setup.
    final PokerTableModel table = getSamplePokerTable(10);
    final List<CardModel> cards = getSampleCards();
    cards.sort((a, b) -> cardService.compare(b, a));
    createFakeBets5(table);
    createFakeHand(table);

    // Test
    PokerTableUtilities.generateSidePots(table);
    PokerTableUtilities.determineWinners(table, cardService);

    // Verify
    final List<WinnerModel> winners = table.getWinners();
    for (int i = 0; i < winners.size(); i++) {
      Assertions.assertEquals(cards.get(i), winners.get(i).getCards().get(0));
    }
    Assertions.assertEquals(new BigDecimal(1000), winners.get(0).getWinnings());
    Assertions.assertEquals(new BigDecimal(900), winners.get(1).getWinnings());
    Assertions.assertEquals(new BigDecimal(800), winners.get(2).getWinnings());
    Assertions.assertEquals(new BigDecimal(700), winners.get(3).getWinnings());
    Assertions.assertEquals(new BigDecimal(600), winners.get(4).getWinnings());
    Assertions.assertEquals(new BigDecimal(2500), winners.get(5).getWinnings());
  }

  /**
   * Tests a simpler situation where all players but one have folded, to ensure that the winner
   * selected is the player remaining in the hand and also that this player's card is not revealed.
   */
  @Test
  public void testDetermineWinners_2() {
    // Setup.
    final PokerTableModel table = getSamplePokerTable(10);
    final List<GamePlayerModel> players = table.getPlayers();
    createAllButOneFoldedScenario(table);

    // Test.
    PokerTableUtilities.determineWinners(table, cardService);

    // Verify.
    Assertions.assertEquals(players.get(0).getId(), table.getWinners().get(0).getId());
    Assertions.assertEquals(new BigDecimal(10000), table.getWinners().get(0).getWinnings());
    Assertions.assertEquals(1, table.getPots().size());
  }

  /** Basic test of card dealing where all players are active in the hand. */
  @Test
  public void testDealCards_1() {
    // Setup.
    final PokerTableModel table = getSamplePokerTable(10);
    final List<GamePlayerModel> players = table.getPlayers();
    final DeckModel deck = new DeckModel();
    table.setDealer(9);

    // Test.
    PokerTableUtilities.dealCards(table, deck);
    final List<CardModel> dealtCards = deck.getUsedCards();

    // Verify.
    for (int i = 0; i < players.size(); i++) {
      Assertions.assertEquals(dealtCards.get(i), players.get(i).getCards().get(0));
      Assertions.assertEquals(1, players.get(i).getCards().size());
    }
    Assertions.assertEquals(42, deck.numCardsRemaining());
    Assertions.assertEquals(10, deck.numCardsUsed());
  }

  /** Test of card dealing where some players are not active in the hand. */
  @Test
  public void testDealCards_2() {
    // Setup.
    final PokerTableModel table = getSamplePokerTable(10);
    final List<GamePlayerModel> players = table.getPlayers();
    final DeckModel deck = new DeckModel();
    // Set 3 players to out.
    players.get(2).setOut(true);
    players.get(1).setOut(true);
    players.get(6).setOut(true);
    table.setDealer(9);

    // Test.
    PokerTableUtilities.dealCards(table, deck);

    final List<CardModel> dealtCards = deck.getUsedCards();
    int j = 0;
    int i = 0;

    // Verify.
    while (j < 7) {
      if (!players.get(i).isOut()) {
        Assertions.assertEquals(dealtCards.get(j), players.get(i).getCards().get(0));
        j++;
      }
      i++;
    }
    Assertions.assertEquals(45, deck.numCardsRemaining());
    Assertions.assertEquals(7, deck.numCardsUsed());
  }

  /**
   * Test of card dealing where some players are not active in the hand and the dealer is set to
   * some player in the middle of the player list.
   */
  @Test
  public void testDealCards_3() {
    // Setup.
    final PokerTableModel table = getSamplePokerTable(10);
    final List<GamePlayerModel> players = table.getPlayers();
    final DeckModel deck = new DeckModel();
    // Set 3 players to out.
    players.get(2).setOut(true);
    players.get(1).setOut(true);
    players.get(6).setOut(true);
    players.get(7).setOut(true);
    players.get(8).setOut(true);
    players.get(9).setOut(true);
    table.setDealer(4);

    // Test.
    PokerTableUtilities.dealCards(table, deck);

    final List<CardModel> dealtCards = deck.getUsedCards();
    // Verify
    Assertions.assertEquals(dealtCards.get(0), players.get(5).getCards().get(0));
    Assertions.assertEquals(dealtCards.get(1), players.get(0).getCards().get(0));
    Assertions.assertEquals(dealtCards.get(2), players.get(3).getCards().get(0));
    Assertions.assertEquals(dealtCards.get(3), players.get(4).getCards().get(0));

    Assertions.assertEquals(48, deck.numCardsRemaining());
    Assertions.assertEquals(4, deck.numCardsUsed());
  }

  /** Basic test of general case where sb and bb both have enough chips to post their blinds. */
  @Test
  public void testPerformBlindBets_1() {
    // Setup.
    final PokerTableModel table = getSamplePokerTable(10);
    final List<GamePlayerModel> players = table.getPlayers();
    table.setDealer(3); // sb = 4, bb = 5
    table.setActingPlayer(4); // Satisfy Pre-Condition.
    final BigDecimal sbBankRollInitial = players.get(4).getControls().getBankRoll();
    final BigDecimal bbBankRollInitial = players.get(5).getControls().getBankRoll();
    final BigDecimal sb = table.getBlind();
    final BigDecimal bb = table.getBlind().add(table.getBlind());

    // Test.
    PokerTableUtilities.performBlindBets(table);

    // Verify.
    Assertions.assertEquals(sb, players.get(4).getControls().getCurrentBet());
    Assertions.assertEquals(bb, players.get(5).getControls().getCurrentBet());
    Assertions.assertEquals(
        sbBankRollInitial.subtract(sb), players.get(4).getControls().getBankRoll());
    Assertions.assertEquals(
        bbBankRollInitial.subtract(bb), players.get(5).getControls().getBankRoll());
    Assertions.assertEquals(6, table.getActingPlayer());
    players.forEach(
        p ->
            Assertions.assertEquals(
                bb, p.getControls().getToCall().add(p.getControls().getCurrentBet())));
    Assertions.assertEquals(BigDecimal.ZERO, players.get(5).getControls().getToCall());
    Assertions.assertEquals(sb, players.get(4).getControls().getToCall());
    Assertions.assertEquals(1, table.getPots().size());
    Assertions.assertEquals(sb.add(bb), table.getPots().get(0).getTotal());
    Assertions.assertEquals(bb, table.getMinRaise());
  }

  /**
   * Test of edge case where some players have been eliminated from the game. Ensuring that proper
   * players have their blinds posted.
   */
  @Test
  public void testPerformBlindBets_2() {
    // Setup.
    final PokerTableModel table = getSamplePokerTable(10);
    final List<GamePlayerModel> players = table.getPlayers();
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
    PokerTableUtilities.performBlindBets(table);

    // Verify.
    Assertions.assertEquals(sb, players.get(6).getControls().getCurrentBet());
    Assertions.assertEquals(bb, players.get(1).getControls().getCurrentBet());
    Assertions.assertEquals(
        sbBankRollInitial.subtract(sb), players.get(6).getControls().getBankRoll());
    Assertions.assertEquals(
        bbBankRollInitial.subtract(bb), players.get(1).getControls().getBankRoll());
    Assertions.assertEquals(2, table.getActingPlayer());
    players.forEach(
        p ->
            Assertions.assertEquals(
                bb, p.getControls().getToCall().add(p.getControls().getCurrentBet())));
    Assertions.assertEquals(BigDecimal.ZERO, players.get(1).getControls().getToCall());
    Assertions.assertEquals(sb, players.get(6).getControls().getToCall());
    Assertions.assertEquals(1, table.getPots().size());
    Assertions.assertEquals(sb.add(bb), table.getPots().get(0).getTotal());
    Assertions.assertEquals(bb, table.getMinRaise());
  }

  /**
   * Test of edge case where the BB player doesn't have enough chips to post the full blinds, but
   * has more than the SB.
   */
  @Test
  public void testPerformBlindBets_3() {
    // Setup.
    final PokerTableModel table = getSamplePokerTable(10);
    final List<GamePlayerModel> players = table.getPlayers();
    table.setDealer(3); // sb = 4, bb = 5
    table.setActingPlayer(4); // Satisfy Pre-Condition.
    final BigDecimal bbBankRollInitial = new BigDecimal(15);
    players.get(5).getControls().setBankRoll(bbBankRollInitial);
    final BigDecimal sbBankRollInitial = players.get(4).getControls().getBankRoll();

    final BigDecimal sb = table.getBlind();
    final BigDecimal bb = table.getBlind().add(table.getBlind());

    // Test.
    PokerTableUtilities.performBlindBets(table);

    // Verify.
    Assertions.assertEquals(sb, players.get(4).getControls().getCurrentBet());
    Assertions.assertEquals(bbBankRollInitial, players.get(5).getControls().getCurrentBet());
    Assertions.assertEquals(
        sbBankRollInitial.subtract(sb), players.get(4).getControls().getBankRoll());
    Assertions.assertEquals(BigDecimal.ZERO, players.get(5).getControls().getBankRoll());
    Assertions.assertEquals(6, table.getActingPlayer());
    Assertions.assertTrue(players.get(5).isAllIn());
    Assertions.assertEquals(BigDecimal.ZERO, players.get(5).getControls().getToCall());
    Assertions.assertEquals(
        bbBankRollInitial.subtract(sb), players.get(4).getControls().getToCall());
    Assertions.assertEquals(1, table.getPots().size());
    Assertions.assertEquals(sb.add(bbBankRollInitial), table.getPots().get(0).getTotal());
    Assertions.assertEquals(bb, table.getMinRaise());
  }

  /**
   * Test of edge case where the BB player doesn't have enough chips to post the full blinds, but
   * has less than the SB.
   */
  @Test
  public void testPerformBlindBets_4() {
    // Setup.
    final PokerTableModel table = getSamplePokerTable(10);
    final List<GamePlayerModel> players = table.getPlayers();
    table.setDealer(3); // sb = 4, bb = 5
    table.setActingPlayer(4); // Satisfy Pre-Condition.
    final BigDecimal bbBankRollInitial = new BigDecimal(5);
    players.get(5).getControls().setBankRoll(bbBankRollInitial);
    final BigDecimal sbBankRollInitial = players.get(4).getControls().getBankRoll();

    final BigDecimal sb = table.getBlind();
    final BigDecimal bb = table.getBlind().add(table.getBlind());

    // Test.
    PokerTableUtilities.performBlindBets(table);

    // Verify.
    Assertions.assertEquals(sb, players.get(4).getControls().getCurrentBet());
    Assertions.assertEquals(bbBankRollInitial, players.get(5).getControls().getCurrentBet());
    Assertions.assertEquals(
        sbBankRollInitial.subtract(sb), players.get(4).getControls().getBankRoll());
    Assertions.assertEquals(BigDecimal.ZERO, players.get(5).getControls().getBankRoll());
    Assertions.assertEquals(6, table.getActingPlayer());
    Assertions.assertTrue(players.get(5).isAllIn());
    Assertions.assertEquals(BigDecimal.ZERO, players.get(5).getControls().getToCall());
    Assertions.assertEquals(BigDecimal.ZERO, players.get(4).getControls().getToCall());
    Assertions.assertEquals(2, table.getPots().size());
    Assertions.assertEquals(
        bbBankRollInitial.add(bbBankRollInitial), table.getPots().get(0).getTotal());
    Assertions.assertEquals(
        bbBankRollInitial.add(sb), PokerTableUtilities.getTotalInAllSidePots(table.getPots()));
    Assertions.assertEquals(bb, table.getMinRaise());
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
    final PokerTableModel table = getSamplePokerTable(10);
    final List<GamePlayerModel> players = table.getPlayers();
    final DeckModel deck = new DeckModel();
    final List<CardModel> dealtCards = deck.peek(10);

    table.setDealer(0);
    table.setRound(0);
    final BigDecimal sb = table.getBlind();
    final BigDecimal pot = sb.add(sb).add(sb);

    // Test.
    PokerTableUtilities.newHandSetup(table, deck);

    // Verify.
    Assertions.assertEquals(1, table.getRound());
    Assertions.assertEquals(pot, PokerTableUtilities.getTotalInAllSidePots(table.getPots()));
    Assertions.assertEquals(sb, players.get(2).getControls().getCurrentBet());
    Assertions.assertEquals(sb.add(sb), players.get(3).getControls().getCurrentBet());
    players.stream()
        .filter(p -> players.indexOf(p) != 2 && players.indexOf(p) != 3)
        .forEach(p -> Assertions.assertEquals(sb.add(sb), p.getControls().getToCall()));
    Assertions.assertEquals(4, table.getActingPlayer());
    // TODO: Check that cards were dealt
  }

  /** Ensuring the blinds are increased */
  @Test
  public void testNewHandSetup_2() {
    final PokerTableModel table = getSamplePokerTable(10);
    final DeckModel deck = new DeckModel();
    final List<CardModel> dealtCards = deck.peek(10);

    table.setRound(-1);
    final BigDecimal sb = table.getBlind();

    // Test.
    PokerTableUtilities.newHandSetup(table, deck);

    // Verify.
    Assertions.assertEquals(sb.add(sb), table.getBlind());
    Assertions.assertEquals(
        sb.multiply(new BigDecimal(6)), PokerTableUtilities.getTotalInAllSidePots(table.getPots()));
  }

  /** Simulates some actions and ensures they were handled correctly. */
  @Test
  public void testHandleHandAction_1() {
    final PokerTableModel table = getSamplePokerTable(10);
    final List<GamePlayerModel> players = table.getPlayers();
    final DeckModel deck = new DeckModel();

    table.setRound(0);
    table.setDealer(4);
    players.get(2).getControls().setBankRoll(new BigDecimal(100));
    players.get(3).getControls().setBankRoll(new BigDecimal(250));
    players.get(5).getControls().setBankRoll(new BigDecimal(300));
    PokerTableUtilities.newHandSetup(table, deck);
    final List<CardModel> dealtCards = deck.getUsedCards();

    final List<Integer> expectedActingPlayers = Arrays.asList(8, 9, 0, 1, 2, 3, 4, 5, 6, 7);
    final List<Integer> actingPlayers = new ArrayList<>();
    actingPlayers.add(table.getActingPlayer());

    // Perform several actions
    PokerTableUtilities.handlePlayerAction(table, GameAction.Call, players.get(8).getId(), null);
    actingPlayers.add(table.getActingPlayer());
    PokerTableUtilities.handlePlayerAction(table, GameAction.Call, players.get(9).getId(), null);
    actingPlayers.add(table.getActingPlayer());
    PokerTableUtilities.handlePlayerAction(table, GameAction.Call, players.get(0).getId(), null);
    actingPlayers.add(table.getActingPlayer());
    PokerTableUtilities.handlePlayerAction(
        table, GameAction.Raise, players.get(1).getId(), new BigDecimal(30));
    actingPlayers.add(table.getActingPlayer());
    // toCall should be 50
    PokerTableUtilities.handlePlayerAction(
        table, GameAction.Raise, players.get(2).getId(), new BigDecimal(50));
    actingPlayers.add(table.getActingPlayer());
    // toCall should be 100
    PokerTableUtilities.handlePlayerAction(
        table, GameAction.Raise, players.get(3).getId(), new BigDecimal(150));
    actingPlayers.add(table.getActingPlayer());
    PokerTableUtilities.handlePlayerAction(table, GameAction.Fold, players.get(4).getId(), null);
    actingPlayers.add(table.getActingPlayer());
    PokerTableUtilities.handlePlayerAction(
        table, GameAction.Raise, players.get(5).getId(), new BigDecimal(50));
    actingPlayers.add(table.getActingPlayer());
    PokerTableUtilities.handlePlayerAction(table, GameAction.Fold, players.get(6).getId(), null);
    actingPlayers.add(table.getActingPlayer());
    PokerTableUtilities.handlePlayerAction(table, GameAction.Call, players.get(7).getId(), null);

    // Verify.
    final List<PotModel> pots = table.getPots();
    Assertions.assertEquals(new BigDecimal(1070), table.getPot());
    Assertions.assertEquals(table.getPot(), PokerTableUtilities.getTotalInAllSidePots(pots));
    Assertions.assertEquals(3, pots.size());
    Assertions.assertEquals(3, players.stream().filter(GamePlayerModel::isAllIn).count());
    Assertions.assertEquals(2, players.stream().filter(GamePlayerModel::isFolded).count());
    // Check players cards.
    Assertions.assertEquals(dealtCards.get(0), players.get(6).getCards().get(0));
    Assertions.assertEquals(dealtCards.get(1), players.get(7).getCards().get(0));
    Assertions.assertEquals(dealtCards.get(2), players.get(8).getCards().get(0));
    Assertions.assertEquals(dealtCards.get(3), players.get(9).getCards().get(0));
    Assertions.assertEquals(dealtCards.get(4), players.get(0).getCards().get(0));
    Assertions.assertEquals(dealtCards.get(5), players.get(1).getCards().get(0));
    Assertions.assertEquals(dealtCards.get(6), players.get(2).getCards().get(0));
    Assertions.assertEquals(dealtCards.get(7), players.get(3).getCards().get(0));
    Assertions.assertEquals(dealtCards.get(8), players.get(4).getCards().get(0));
    Assertions.assertEquals(dealtCards.get(9), players.get(5).getCards().get(0));
    players.forEach(p -> Assertions.assertEquals(1, p.getCards().size()));
    // Check acting players
    actingPlayers.forEach(
        i -> Assertions.assertEquals(expectedActingPlayers.indexOf(i), actingPlayers.indexOf(i)));
    // Check lastToAct
    Assertions.assertEquals(3, table.getLastToAct());
  }
}
