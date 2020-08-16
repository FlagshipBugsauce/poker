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
import static com.poker.poker.utilities.CardUtilities.checkForFlush;
import static com.poker.poker.utilities.CardUtilities.checkForFourOfAKind;
import static com.poker.poker.utilities.CardUtilities.checkForFullHouse;
import static com.poker.poker.utilities.CardUtilities.checkForPair;
import static com.poker.poker.utilities.CardUtilities.checkForSet;
import static com.poker.poker.utilities.CardUtilities.checkForStraight;
import static com.poker.poker.utilities.CardUtilities.checkForStraightFlush;
import static com.poker.poker.utilities.CardUtilities.checkForTwoPair;
import static com.poker.poker.utilities.CardUtilities.rankHand;
import static com.poker.poker.utilities.CardUtilities.valueSorter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.poker.poker.models.enums.CardSuit;
import com.poker.poker.models.enums.CardValue;
import com.poker.poker.models.game.CardModel;
import com.poker.poker.models.game.HandRankModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

@SuppressWarnings("MagicNumber")
public class CardUtilitiesTests {

  public static List<CardModel> getAllWithValue(final CardValue value) {
    return Arrays.stream(CardSuit.values())
        .filter(suit -> suit != CardSuit.Back)
        .map(suit -> new CardModel(suit, value))
        .collect(Collectors.toList());
  }

  public static List<CardModel> getAllWithSuit(final CardSuit suit) {
    return Arrays.stream(CardValue.values())
        .filter(v -> v != CardValue.Back)
        .map(value -> new CardModel(suit, value))
        .sorted(valueSorter())
        .collect(Collectors.toList());
  }

  public static CardSuit getRandomSuit() {
    final List<CardSuit> suits =
        Arrays.stream(CardSuit.values())
            .filter(s -> s != CardSuit.Back)
            .collect(Collectors.toList());
    return suits.get((int) (Math.random() * suits.size()));
  }

  public static CardModel getCard(final CardValue value) {
    return getCard(getRandomSuit(), value);
  }

  public static CardModel getCard(final CardSuit suit, final CardValue value) {
    return new CardModel(suit, value);
  }

  public static List<CardModel> getPairWithKickers(
      final CardValue pair,
      final CardValue kicker1,
      final CardValue kicker2,
      final CardValue kicker3) {
    return new ArrayList<>(
        Arrays.asList(
            new CardModel(Hearts, pair),
            new CardModel(Spades, pair),
            new CardModel(getRandomSuit(), kicker1),
            new CardModel(getRandomSuit(), kicker2),
            new CardModel(getRandomSuit(), kicker3)));
  }

  public static List<CardModel> getTwoPairWithKicker(
      final CardValue pair1, final CardValue pair2, final CardValue kicker) {
    return new ArrayList<>(
        Arrays.asList(
            new CardModel(Hearts, pair1),
            new CardModel(Spades, pair1),
            new CardModel(Hearts, pair2),
            new CardModel(Spades, pair2),
            new CardModel(getRandomSuit(), kicker)));
  }

  public static List<CardModel> getSetWithKickers(
      final CardValue set, final CardValue kicker1, final CardValue kicker2) {
    return new ArrayList<>(
        Arrays.asList(
            new CardModel(Hearts, set),
            new CardModel(Spades, set),
            new CardModel(Clubs, set),
            new CardModel(getRandomSuit(), kicker1),
            new CardModel(getRandomSuit(), kicker2)));
  }

  public static List<CardModel> getFullHouse(final CardValue set, final CardValue pair) {
    return new ArrayList<>(
        Arrays.asList(
            new CardModel(Hearts, set),
            new CardModel(Spades, set),
            new CardModel(Clubs, set),
            new CardModel(Spades, pair),
            new CardModel(Hearts, pair)));
  }

  /** Basic test to see if this method can detect a straight flush. */
  @Test
  public void testCheckForStraightFlush_1() {
    // Given.
    final List<CardModel> cards = getAllWithSuit(Spades).subList(3, 8);
    cards.add(new CardModel(Hearts, Eight));
    cards.add(new CardModel(Diamonds, Two));
    Collections.shuffle(cards);

    // Test.
    final List<CardModel> straightFlush = checkForStraightFlush(cards);

    // Verify.
    assertEquals(getAllWithSuit(Spades).subList(3, 8), straightFlush);
  }

  /**
   * Basic test to see if this method can detect the best straight flush from multiple
   * possibilities.
   */
  @Test
  public void testCheckForStraightFlush_2() {
    // Given.
    final List<CardModel> cards = getAllWithSuit(Spades).subList(3, 10);
    Collections.shuffle(cards);

    // Test.
    final List<CardModel> straightFlush = checkForStraightFlush(cards);

    // Verify.
    assertEquals(getAllWithSuit(Spades).subList(5, 10), straightFlush);
  }

  /** Basic test to see if this method can detect when there is no straight flush. */
  @Test
  public void testCheckForStraightFlush_3() {
    // Given.
    final List<CardModel> cards = getAllWithSuit(Spades).subList(3, 7);
    cards.addAll(getAllWithSuit(Spades).subList(8, 10));
    Collections.shuffle(cards);

    // Test.
    final List<CardModel> straightFlush = checkForStraightFlush(cards);

    // Verify.
    assertNull(straightFlush);
  }

  /** Basic test to see if this method can detect a straight flush that starts with an Ace. */
  @Test
  public void testCheckForStraightFlush_4() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            getCard(Spades, Ace),
            getCard(Spades, Two),
            getCard(Spades, Three),
            getCard(Spades, Four),
            getCard(Spades, Five),
            getCard(Hearts, Ace),
            getCard(Clubs, Ace));
    Collections.shuffle(cards);
    final List<CardModel> expected =
        Arrays.asList(
            getCard(Spades, Ace),
            getCard(Spades, Two),
            getCard(Spades, Three),
            getCard(Spades, Four),
            getCard(Spades, Five));

    // Test.
    final List<CardModel> straightFlush = checkForStraightFlush(cards);

    // Verify.
    assertEquals(expected, straightFlush);
  }

  /**
   * Basic test to see if this method can detect when there is 4-of-a-kind and will return a list
   * with the high-card in the appropriate location.
   */
  @Test
  public void testCheckForFourOfAKind_1() {
    // Given.
    final List<CardModel> cards = getAllWithValue(Ace);
    cards.add(new CardModel(Clubs, Eight));
    cards.add(new CardModel(Clubs, Two));
    cards.add(new CardModel(Diamonds, Six));
    Collections.shuffle(cards);
    final List<CardModel> expected = getAllWithValue(Ace);
    expected.add(new CardModel(Clubs, Eight));
    expected.sort(valueSorter());

    // Test.
    final List<CardModel> fourOfAKind = checkForFourOfAKind(cards);
    fourOfAKind.sort(valueSorter());

    // Verify.
    assertEquals(expected, fourOfAKind);
  }

  /**
   * Basic test to see if this method can detect when there is 4-of-a-kind and will return a list
   * with the high-card in the appropriate location. This test has a slightly different case, where
   * the high-card is higher than the 4-of-a-kind.
   */
  @Test
  public void testCheckForFourOfAKind_2() {
    // Given.
    final List<CardModel> cards = getAllWithValue(Two);
    cards.add(new CardModel(Clubs, Eight));
    cards.add(new CardModel(Clubs, King));
    cards.add(new CardModel(Diamonds, Six));
    Collections.shuffle(cards);
    final List<CardModel> expected = getAllWithValue(Two);
    expected.add(new CardModel(Clubs, King));
    expected.sort(valueSorter());

    // Test.
    final List<CardModel> fourOfAKind = checkForFourOfAKind(cards);
    fourOfAKind.sort(valueSorter());

    // Verify.
    assertEquals(expected, fourOfAKind);
  }

  /**
   * Basic test to see if this method can detect when there is not a 4-of-a-kind in the 7 cards
   * provided.
   */
  @Test
  public void testCheckForFourOfAKind_3() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            new CardModel(Clubs, Eight),
            new CardModel(Diamonds, Eight),
            new CardModel(Hearts, Eight),
            new CardModel(Clubs, Four),
            new CardModel(Spades, Four),
            new CardModel(Diamonds, King),
            new CardModel(Hearts, Ace));
    Collections.shuffle(cards);
    final List<CardModel> expected = getAllWithValue(Two);
    expected.add(new CardModel(Clubs, King));
    expected.sort(valueSorter());

    // Test.
    final List<CardModel> fourOfAKind = checkForFourOfAKind(cards);

    // Verify.
    assertNull(fourOfAKind);
  }

  /**
   * Basic test to see if this method properly detects a full house and returns the 5-card hand with
   * the cards in the proper order.
   */
  @Test
  public void testCheckForFullHouse_1() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            new CardModel(Clubs, Eight),
            new CardModel(Diamonds, Eight),
            new CardModel(Hearts, Eight),
            new CardModel(Clubs, Four),
            new CardModel(Spades, Four),
            new CardModel(Diamonds, King),
            new CardModel(Hearts, Ace));
    Collections.shuffle(cards);
    final List<CardModel> expected =
        Arrays.asList(
            new CardModel(Hearts, Eight),
            new CardModel(Clubs, Eight),
            new CardModel(Diamonds, Eight),
            new CardModel(Spades, Four),
            new CardModel(Clubs, Four));

    // Test.
    final List<CardModel> fullHouse = checkForFullHouse(cards);

    // Verify.
    assertEquals(expected, fullHouse);
  }

  /**
   * Testing when the set is lower than the pair to ensure the full house is still detected and the
   * cards are still returned in the correct order.
   */
  @Test
  public void testCheckForFullHouse_2() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            new CardModel(Clubs, Two),
            new CardModel(Diamonds, Two),
            new CardModel(Hearts, Two),
            new CardModel(Clubs, Four),
            new CardModel(Spades, Four),
            new CardModel(Diamonds, King),
            new CardModel(Hearts, Ace));
    Collections.shuffle(cards);
    final List<CardModel> expected =
        Arrays.asList(
            new CardModel(Hearts, Two),
            new CardModel(Clubs, Two),
            new CardModel(Diamonds, Two),
            new CardModel(Spades, Four),
            new CardModel(Clubs, Four));

    // Test.
    final List<CardModel> fullHouse = checkForFullHouse(cards);

    // Verify.
    assertEquals(expected, fullHouse);
  }

  /** Testing the case where there is no full house because there is no set. */
  @Test
  public void testCheckForFullHouse_3() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            new CardModel(Clubs, Two),
            new CardModel(Diamonds, Two),
            new CardModel(Hearts, King),
            new CardModel(Clubs, Four),
            new CardModel(Spades, Four),
            new CardModel(Diamonds, King),
            new CardModel(Hearts, Ace));
    Collections.shuffle(cards);

    // Test.
    final List<CardModel> fullHouse = checkForFullHouse(cards);

    // Verify.
    assertNull(fullHouse);
  }

  /** Testing the case where there is no full house because there is only a set. */
  @Test
  public void testCheckForFullHouse_4() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            new CardModel(Clubs, Two),
            new CardModel(Diamonds, Two),
            new CardModel(Hearts, Two),
            new CardModel(Clubs, Four),
            new CardModel(Spades, Five),
            new CardModel(Diamonds, King),
            new CardModel(Hearts, Ace));
    Collections.shuffle(cards);

    // Test.
    final List<CardModel> fullHouse = checkForFullHouse(cards);

    // Verify.
    assertNull(fullHouse);
  }

  /** Testing that the method can detect a flush and returns the correct hand. */
  @Test
  public void testCheckForFlush_1() {
    // Given.
    final List<CardModel> cards = getAllWithSuit(Spades).subList(4, 7);
    cards.addAll(getAllWithSuit(Spades).subList(9, 11));
    cards.add(new CardModel(Clubs, Two));
    cards.add(new CardModel(Clubs, Six));
    Collections.shuffle(cards);
    final List<CardModel> expected = getAllWithSuit(Spades).subList(4, 7);
    expected.addAll(getAllWithSuit(Spades).subList(9, 11));

    // Test.
    final List<CardModel> flush = checkForFlush(cards);

    // Verify.
    assertEquals(expected, flush);
  }

  /** Testing that the method can detect a flush and returns the correct hand. */
  @Test
  public void testCheckForFlush_2() {
    // Given.
    final List<CardModel> cards = getAllWithSuit(Spades).subList(4, 8);
    cards.add(new CardModel(Clubs, Two));
    cards.add(new CardModel(Clubs, Six));
    cards.add(new CardModel(Diamonds, Six));
    Collections.shuffle(cards);

    // Test.
    final List<CardModel> flush = checkForFlush(cards);

    // Verify.
    assertNull(flush);
  }

  /** Final flush test for another basic case. */
  @Test
  public void testCheckForFlush_3() {
    // Given.
    final List<CardModel> cards = getAllWithSuit(Spades).subList(10, 13);
    cards.addAll(getAllWithSuit(Spades).subList(2, 4));
    cards.add(new CardModel(Clubs, Two));
    cards.add(new CardModel(Clubs, Six));
    Collections.shuffle(cards);
    final List<CardModel> expected = getAllWithSuit(Spades).subList(10, 13);
    expected.addAll(getAllWithSuit(Spades).subList(2, 4));
    expected.sort(valueSorter());

    // Test.
    final List<CardModel> flush = checkForFlush(cards);

    // Verify.
    assertEquals(expected, flush);
  }

  @Test
  public void testCheckForStraight_1() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            new CardModel(Hearts, Two),
            new CardModel(Spades, Three),
            new CardModel(Diamonds, Four),
            new CardModel(Clubs, Five),
            new CardModel(Hearts, Six),
            new CardModel(Diamonds, Three),
            new CardModel(Spades, Two));
    Collections.shuffle(cards);
    final List<CardModel> expected =
        Arrays.asList(
            new CardModel(Spades, Two),
            new CardModel(Spades, Three),
            new CardModel(Diamonds, Four),
            new CardModel(Clubs, Five),
            new CardModel(Hearts, Six));

    // Test
    final List<CardModel> straight = checkForStraight(cards);

    // Verify.
    assertEquals(expected, straight);
  }

  @Test
  public void testCheckForStraight_2() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            new CardModel(Hearts, Eight),
            new CardModel(Spades, Three),
            new CardModel(Diamonds, Four),
            new CardModel(Clubs, Five),
            new CardModel(Hearts, Six),
            new CardModel(Diamonds, Three),
            new CardModel(Spades, Ten));
    Collections.shuffle(cards);

    // Test
    final List<CardModel> straight = checkForStraight(cards);

    // Verify.
    assertNull(straight);
  }

  @Test
  public void testCheckForStraight_3() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            new CardModel(Hearts, Two),
            new CardModel(Spades, Three),
            new CardModel(Diamonds, Four),
            new CardModel(Clubs, Five),
            new CardModel(Hearts, Six),
            new CardModel(Diamonds, Seven),
            new CardModel(Spades, Eight));
    Collections.shuffle(cards);
    final List<CardModel> expected =
        Arrays.asList(
            new CardModel(Diamonds, Four),
            new CardModel(Clubs, Five),
            new CardModel(Hearts, Six),
            new CardModel(Diamonds, Seven),
            new CardModel(Spades, Eight));

    // Test
    final List<CardModel> straight = checkForStraight(cards);

    // Verify.
    assertEquals(expected, straight);
  }

  @Test
  public void testCheckForSet_1() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            new CardModel(Hearts, Two),
            new CardModel(Spades, Two),
            new CardModel(Diamonds, Two),
            new CardModel(Clubs, Five),
            new CardModel(Hearts, Six),
            new CardModel(Diamonds, Seven),
            new CardModel(Spades, Eight));
    Collections.shuffle(cards);
    final List<CardModel> expected =
        Arrays.asList(
            new CardModel(Spades, Two),
            new CardModel(Hearts, Two),
            new CardModel(Diamonds, Two),
            new CardModel(Diamonds, Seven),
            new CardModel(Spades, Eight));

    // Test
    final List<CardModel> set = checkForSet(cards);

    // Verify
    assertEquals(expected, set);
  }

  @Test
  public void testCheckForSet_2() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            new CardModel(Hearts, Two),
            new CardModel(Spades, Two),
            new CardModel(Diamonds, Ten),
            new CardModel(Clubs, Five),
            new CardModel(Hearts, Six),
            new CardModel(Diamonds, Seven),
            new CardModel(Spades, Eight));
    Collections.shuffle(cards);

    // Test
    final List<CardModel> set = checkForSet(cards);

    // Verify
    assertNull(set);
  }

  @Test
  public void testCheckForSet_3() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            new CardModel(Hearts, Jack),
            new CardModel(Spades, Jack),
            new CardModel(Diamonds, Two),
            new CardModel(Clubs, Five),
            new CardModel(Hearts, Six),
            new CardModel(Diamonds, Jack),
            new CardModel(Spades, Ace));
    Collections.shuffle(cards);
    final List<CardModel> expected =
        Arrays.asList(
            new CardModel(Spades, Jack),
            new CardModel(Hearts, Jack),
            new CardModel(Diamonds, Jack),
            new CardModel(Hearts, Six),
            new CardModel(Spades, Ace));

    // Test
    final List<CardModel> set = checkForSet(cards);

    // Verify
    assertEquals(expected, set);
  }

  @Test
  public void testCheckForTwoPairs_1() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            new CardModel(Hearts, Jack),
            new CardModel(Spades, Jack),
            new CardModel(Diamonds, Two),
            new CardModel(Clubs, Two),
            new CardModel(Hearts, Six),
            new CardModel(Diamonds, Seven),
            new CardModel(Spades, Ace));
    Collections.shuffle(cards);
    final List<CardModel> expected =
        Arrays.asList(
            new CardModel(Spades, Jack),
            new CardModel(Hearts, Jack),
            new CardModel(Clubs, Two),
            new CardModel(Diamonds, Two),
            new CardModel(Spades, Ace));

    // Test
    final List<CardModel> twoPairs = checkForTwoPair(cards);

    // Verify
    assertEquals(expected, twoPairs);
  }

  @Test
  public void testCheckForTwoPairs_2() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            new CardModel(Hearts, Jack),
            new CardModel(Spades, Jack),
            new CardModel(Diamonds, Two),
            new CardModel(Clubs, Nine),
            new CardModel(Hearts, Six),
            new CardModel(Diamonds, Seven),
            new CardModel(Spades, Ace));
    Collections.shuffle(cards);

    // Test
    final List<CardModel> twoPairs = checkForTwoPair(cards);

    // Verify
    assertNull(twoPairs);
  }

  @Test
  public void testCheckPair_1() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            new CardModel(Hearts, Jack),
            new CardModel(Spades, Jack),
            new CardModel(Diamonds, Two),
            new CardModel(Clubs, Three),
            new CardModel(Hearts, Four),
            new CardModel(Diamonds, Eight),
            new CardModel(Spades, Nine));
    Collections.shuffle(cards);
    final List<CardModel> expected =
        Arrays.asList(
            new CardModel(Spades, Jack),
            new CardModel(Hearts, Jack),
            new CardModel(Hearts, Four),
            new CardModel(Diamonds, Eight),
            new CardModel(Spades, Nine));

    // Test
    final List<CardModel> pair = checkForPair(cards);

    // Verify
    assertEquals(expected, pair);
  }

  @Test
  public void testCheckPair_2() {
    // Given.
    final List<CardModel> cards =
        Arrays.asList(
            new CardModel(Hearts, Jack),
            new CardModel(Spades, King),
            new CardModel(Diamonds, Two),
            new CardModel(Clubs, Three),
            new CardModel(Hearts, Four),
            new CardModel(Diamonds, Eight),
            new CardModel(Spades, Nine));
    Collections.shuffle(cards);

    // Test
    final List<CardModel> pair = checkForPair(cards);

    // Verify
    assertNull(pair);
  }

  /**
   * Testing all edge cases, i.e. best high card hand loses to worst pair hand, best pair hand loses
   * to worst two-pair hand, best two-pair hand loses to worst set hand, etc...
   */
  @Test
  public void testRankHand_1() {
    // Given.
    final List<CardModel> worstHand =
        Arrays.asList(
            new CardModel(Hearts, Two),
            new CardModel(Spades, Three),
            new CardModel(Diamonds, Four),
            new CardModel(Clubs, Five),
            new CardModel(Hearts, Seven),
            new CardModel(Diamonds, Eight),
            new CardModel(Spades, Nine));

    final List<CardModel> bestHighCard =
        Arrays.asList(
            new CardModel(Hearts, Ace),
            new CardModel(Spades, King),
            new CardModel(Diamonds, Queen),
            new CardModel(Clubs, Jack),
            new CardModel(Hearts, Nine),
            new CardModel(Diamonds, Eight),
            new CardModel(Spades, Seven));

    final List<CardModel> worstPair = getPairWithKickers(Two, Three, Four, Five);
    worstPair.addAll(Arrays.asList(getCard(Clubs, Seven), getCard(Diamonds, Eight)));

    final List<CardModel> bestPair = getPairWithKickers(Ace, King, Queen, Jack);
    bestPair.addAll(Arrays.asList(getCard(Nine), getCard(Eight)));

    final List<CardModel> worstTwoPair = getTwoPairWithKicker(Two, Three, Four);
    worstTwoPair.addAll(Arrays.asList(getCard(Five), getCard(Seven)));

    final List<CardModel> bestTwoPair = getTwoPairWithKicker(Ace, King, Queen);
    bestTwoPair.addAll(Arrays.asList(getCard(Jack), getCard(Nine)));

    final List<CardModel> worstSet = getSetWithKickers(Two, Three, Four);
    worstSet.addAll(Arrays.asList(getCard(Five), getCard(Seven)));

    final List<CardModel> bestSet = getSetWithKickers(Ace, King, Queen);
    bestSet.addAll(Arrays.asList(getCard(Jack), getCard(Nine)));

    // Also a set.
    final List<CardModel> worstStraight =
        Arrays.asList(
            new CardModel(Hearts, Ace),
            new CardModel(Spades, Two),
            new CardModel(Diamonds, Three),
            new CardModel(Clubs, Four),
            new CardModel(Hearts, Five),
            new CardModel(Diamonds, Ace),
            new CardModel(Spades, Ace));

    // Also a set.
    final List<CardModel> bestStraight =
        Arrays.asList(
            new CardModel(Hearts, Ace),
            new CardModel(Spades, King),
            new CardModel(Diamonds, Queen),
            new CardModel(Clubs, Jack),
            new CardModel(Hearts, Ten),
            new CardModel(Diamonds, Ace),
            new CardModel(Spades, Ace));

    // Also a straight.
    final List<CardModel> worstFlush =
        Arrays.asList(
            new CardModel(Hearts, Two),
            new CardModel(Hearts, Three),
            new CardModel(Hearts, Four),
            new CardModel(Hearts, Five),
            new CardModel(Hearts, Seven),
            new CardModel(Diamonds, Six),
            new CardModel(Spades, Eight));

    // Also a straight.
    final List<CardModel> bestFlush =
        Arrays.asList(
            new CardModel(Hearts, King),
            new CardModel(Hearts, Queen),
            new CardModel(Hearts, Jack),
            new CardModel(Hearts, Ace),
            new CardModel(Hearts, Nine),
            new CardModel(Diamonds, Ten),
            new CardModel(Spades, Ace));

    final List<CardModel> worstFullHouse = getFullHouse(Two, Three);
    worstFullHouse.addAll(Arrays.asList(getCard(Spades, Four), getCard(Spades, Five)));

    final List<CardModel> bestFullHouse = getFullHouse(Ace, King);
    bestFullHouse.addAll(Arrays.asList(getCard(Diamonds, King), getCard(Spades, Queen)));

    final List<CardModel> worstFourOfAKind = getAllWithValue(Two);
    worstFourOfAKind.addAll(Arrays.asList(getCard(Three), getCard(Four), getCard(Five)));

    final List<CardModel> bestFourOfAKind = getAllWithValue(Ace);
    bestFourOfAKind.addAll(Arrays.asList(getCard(King), getCard(King), getCard(King)));

    final List<CardModel> worstStraightFlush =
        Arrays.asList(
            getCard(Spades, Ace),
            getCard(Spades, Two),
            getCard(Spades, Three),
            getCard(Spades, Four),
            getCard(Spades, Five),
            getCard(Hearts, Ace),
            getCard(Clubs, Ace));

    final List<CardModel> bestStraightFlush = getAllWithSuit(Spades).subList(8, 13);
    bestStraightFlush.addAll(Arrays.asList(getCard(Hearts, Ace), getCard(Clubs, Ace)));

    // Shuffle hands.
    Collections.shuffle(worstHand);
    Collections.shuffle(bestHighCard);
    Collections.shuffle(worstPair);
    Collections.shuffle(bestPair);
    Collections.shuffle(worstTwoPair);
    Collections.shuffle(bestTwoPair);
    Collections.shuffle(worstSet);
    Collections.shuffle(bestSet);
    Collections.shuffle(worstStraight);
    Collections.shuffle(bestStraight);
    Collections.shuffle(worstFlush);
    Collections.shuffle(bestFlush);
    Collections.shuffle(worstFullHouse);
    Collections.shuffle(bestFullHouse);
    Collections.shuffle(worstFourOfAKind);
    Collections.shuffle(bestFourOfAKind);
    Collections.shuffle(worstStraightFlush);
    Collections.shuffle(bestStraightFlush);

    // Test.
    final List<HandRankModel> rankedHands =
        Arrays.asList(
            rankHand(worstHand),
            rankHand(bestHighCard),
            rankHand(worstPair),
            rankHand(bestPair),
            rankHand(worstTwoPair),
            rankHand(bestTwoPair),
            rankHand(worstSet),
            rankHand(bestSet),
            rankHand(worstStraight),
            rankHand(bestStraight),
            rankHand(worstFlush),
            rankHand(bestFlush),
            rankHand(worstFullHouse),
            rankHand(bestFullHouse),
            rankHand(worstFourOfAKind),
            rankHand(bestFourOfAKind),
            rankHand(worstStraightFlush),
            rankHand(bestStraightFlush));
    final List<Integer> rankings =
        rankedHands.stream().map(HandRankModel::getRank).sorted().collect(Collectors.toList());

    // Verify.
    for (int i = 0; i < rankings.size(); i++) {
      assertEquals(rankings.get(i), rankedHands.get(i).getRank());
    }
  }
}
