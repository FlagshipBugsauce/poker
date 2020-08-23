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
import com.poker.poker.models.game.Card;
import com.poker.poker.models.game.HandRankModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

@SuppressWarnings("MagicNumber")
public class CardUtilitiesTests {

  public static List<Card> getAllWithValue(final CardValue value) {
    return Arrays.stream(CardSuit.values())
        .filter(suit -> suit != CardSuit.Back)
        .map(suit -> new Card(suit, value))
        .collect(Collectors.toList());
  }

  public static List<Card> getAllWithSuit(final CardSuit suit) {
    return Arrays.stream(CardValue.values())
        .filter(v -> v != CardValue.Back)
        .map(value -> new Card(suit, value))
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

  public static Card getCard(final CardValue value) {
    return getCard(getRandomSuit(), value);
  }

  public static Card getCard(final CardSuit suit, final CardValue value) {
    return new Card(suit, value);
  }

  public static List<Card> getPairWithKickers(
      final CardValue pair,
      final CardValue kicker1,
      final CardValue kicker2,
      final CardValue kicker3) {
    return new ArrayList<>(
        Arrays.asList(
            new Card(Hearts, pair),
            new Card(Spades, pair),
            new Card(getRandomSuit(), kicker1),
            new Card(getRandomSuit(), kicker2),
            new Card(getRandomSuit(), kicker3)));
  }

  public static List<Card> getTwoPairWithKicker(
      final CardValue pair1, final CardValue pair2, final CardValue kicker) {
    return new ArrayList<>(
        Arrays.asList(
            new Card(Hearts, pair1),
            new Card(Spades, pair1),
            new Card(Hearts, pair2),
            new Card(Spades, pair2),
            new Card(getRandomSuit(), kicker)));
  }

  public static List<Card> getSetWithKickers(
      final CardValue set, final CardValue kicker1, final CardValue kicker2) {
    return new ArrayList<>(
        Arrays.asList(
            new Card(Hearts, set),
            new Card(Spades, set),
            new Card(Clubs, set),
            new Card(getRandomSuit(), kicker1),
            new Card(getRandomSuit(), kicker2)));
  }

  public static List<Card> getFullHouse(final CardValue set, final CardValue pair) {
    return new ArrayList<>(
        Arrays.asList(
            new Card(Hearts, set),
            new Card(Spades, set),
            new Card(Clubs, set),
            new Card(Spades, pair),
            new Card(Hearts, pair)));
  }

  /** Basic test to see if this method can detect a straight flush. */
  @Test
  public void testCheckForStraightFlush_1() {
    // Given.
    final List<Card> cards = getAllWithSuit(Spades).subList(3, 8);
    cards.add(new Card(Hearts, Eight));
    cards.add(new Card(Diamonds, Two));
    Collections.shuffle(cards);

    // Test.
    final List<Card> straightFlush = checkForStraightFlush(cards);

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
    final List<Card> cards = getAllWithSuit(Spades).subList(3, 10);
    Collections.shuffle(cards);

    // Test.
    final List<Card> straightFlush = checkForStraightFlush(cards);

    // Verify.
    assertEquals(getAllWithSuit(Spades).subList(5, 10), straightFlush);
  }

  /** Basic test to see if this method can detect when there is no straight flush. */
  @Test
  public void testCheckForStraightFlush_3() {
    // Given.
    final List<Card> cards = getAllWithSuit(Spades).subList(3, 7);
    cards.addAll(getAllWithSuit(Spades).subList(8, 10));
    Collections.shuffle(cards);

    // Test.
    final List<Card> straightFlush = checkForStraightFlush(cards);

    // Verify.
    assertNull(straightFlush);
  }

  /** Basic test to see if this method can detect a straight flush that starts with an Ace. */
  @Test
  public void testCheckForStraightFlush_4() {
    // Given.
    final List<Card> cards =
        Arrays.asList(
            getCard(Spades, Ace),
            getCard(Spades, Two),
            getCard(Spades, Three),
            getCard(Spades, Four),
            getCard(Spades, Five),
            getCard(Hearts, Ace),
            getCard(Clubs, Ace));
    Collections.shuffle(cards);
    final List<Card> expected =
        Arrays.asList(
            getCard(Spades, Ace),
            getCard(Spades, Two),
            getCard(Spades, Three),
            getCard(Spades, Four),
            getCard(Spades, Five));

    // Test.
    final List<Card> straightFlush = checkForStraightFlush(cards);

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
    final List<Card> cards = getAllWithValue(Ace);
    cards.add(new Card(Clubs, Eight));
    cards.add(new Card(Clubs, Two));
    cards.add(new Card(Diamonds, Six));
    Collections.shuffle(cards);
    final List<Card> expected = getAllWithValue(Ace);
    expected.add(new Card(Clubs, Eight));
    expected.sort(valueSorter());

    // Test.
    final List<Card> fourOfAKind = checkForFourOfAKind(cards);
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
    final List<Card> cards = getAllWithValue(Two);
    cards.add(new Card(Clubs, Eight));
    cards.add(new Card(Clubs, King));
    cards.add(new Card(Diamonds, Six));
    Collections.shuffle(cards);
    final List<Card> expected = getAllWithValue(Two);
    expected.add(new Card(Clubs, King));
    expected.sort(valueSorter());

    // Test.
    final List<Card> fourOfAKind = checkForFourOfAKind(cards);
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
    final List<Card> cards =
        Arrays.asList(
            new Card(Clubs, Eight),
            new Card(Diamonds, Eight),
            new Card(Hearts, Eight),
            new Card(Clubs, Four),
            new Card(Spades, Four),
            new Card(Diamonds, King),
            new Card(Hearts, Ace));
    Collections.shuffle(cards);
    final List<Card> expected = getAllWithValue(Two);
    expected.add(new Card(Clubs, King));
    expected.sort(valueSorter());

    // Test.
    final List<Card> fourOfAKind = checkForFourOfAKind(cards);

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
    final List<Card> cards =
        Arrays.asList(
            new Card(Clubs, Eight),
            new Card(Diamonds, Eight),
            new Card(Hearts, Eight),
            new Card(Clubs, Four),
            new Card(Spades, Four),
            new Card(Diamonds, King),
            new Card(Hearts, Ace));
    Collections.shuffle(cards);
    final List<Card> expected =
        Arrays.asList(
            new Card(Hearts, Eight),
            new Card(Clubs, Eight),
            new Card(Diamonds, Eight),
            new Card(Spades, Four),
            new Card(Clubs, Four));

    // Test.
    final List<Card> fullHouse = checkForFullHouse(cards);

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
    final List<Card> cards =
        Arrays.asList(
            new Card(Clubs, Two),
            new Card(Diamonds, Two),
            new Card(Hearts, Two),
            new Card(Clubs, Four),
            new Card(Spades, Four),
            new Card(Diamonds, King),
            new Card(Hearts, Ace));
    Collections.shuffle(cards);
    final List<Card> expected =
        Arrays.asList(
            new Card(Hearts, Two),
            new Card(Clubs, Two),
            new Card(Diamonds, Two),
            new Card(Spades, Four),
            new Card(Clubs, Four));

    // Test.
    final List<Card> fullHouse = checkForFullHouse(cards);

    // Verify.
    assertEquals(expected, fullHouse);
  }

  /** Testing the case where there is no full house because there is no set. */
  @Test
  public void testCheckForFullHouse_3() {
    // Given.
    final List<Card> cards =
        Arrays.asList(
            new Card(Clubs, Two),
            new Card(Diamonds, Two),
            new Card(Hearts, King),
            new Card(Clubs, Four),
            new Card(Spades, Four),
            new Card(Diamonds, King),
            new Card(Hearts, Ace));
    Collections.shuffle(cards);

    // Test.
    final List<Card> fullHouse = checkForFullHouse(cards);

    // Verify.
    assertNull(fullHouse);
  }

  /** Testing the case where there is no full house because there is only a set. */
  @Test
  public void testCheckForFullHouse_4() {
    // Given.
    final List<Card> cards =
        Arrays.asList(
            new Card(Clubs, Two),
            new Card(Diamonds, Two),
            new Card(Hearts, Two),
            new Card(Clubs, Four),
            new Card(Spades, Five),
            new Card(Diamonds, King),
            new Card(Hearts, Ace));
    Collections.shuffle(cards);

    // Test.
    final List<Card> fullHouse = checkForFullHouse(cards);

    // Verify.
    assertNull(fullHouse);
  }

  /** Testing that the method can detect a flush and returns the correct hand. */
  @Test
  public void testCheckForFlush_1() {
    // Given.
    final List<Card> cards = getAllWithSuit(Spades).subList(4, 7);
    cards.addAll(getAllWithSuit(Spades).subList(9, 11));
    cards.add(new Card(Clubs, Two));
    cards.add(new Card(Clubs, Six));
    Collections.shuffle(cards);
    final List<Card> expected = getAllWithSuit(Spades).subList(4, 7);
    expected.addAll(getAllWithSuit(Spades).subList(9, 11));

    // Test.
    final List<Card> flush = checkForFlush(cards);

    // Verify.
    assertEquals(expected, flush);
  }

  /** Testing that the method can detect a flush and returns the correct hand. */
  @Test
  public void testCheckForFlush_2() {
    // Given.
    final List<Card> cards = getAllWithSuit(Spades).subList(4, 8);
    cards.add(new Card(Clubs, Two));
    cards.add(new Card(Clubs, Six));
    cards.add(new Card(Diamonds, Six));
    Collections.shuffle(cards);

    // Test.
    final List<Card> flush = checkForFlush(cards);

    // Verify.
    assertNull(flush);
  }

  /** Final flush test for another basic case. */
  @Test
  public void testCheckForFlush_3() {
    // Given.
    final List<Card> cards = getAllWithSuit(Spades).subList(10, 13);
    cards.addAll(getAllWithSuit(Spades).subList(2, 4));
    cards.add(new Card(Clubs, Two));
    cards.add(new Card(Clubs, Six));
    Collections.shuffle(cards);
    final List<Card> expected = getAllWithSuit(Spades).subList(10, 13);
    expected.addAll(getAllWithSuit(Spades).subList(2, 4));
    expected.sort(valueSorter());

    // Test.
    final List<Card> flush = checkForFlush(cards);

    // Verify.
    assertEquals(expected, flush);
  }

  @Test
  public void testCheckForStraight_1() {
    // Given.
    final List<Card> cards =
        Arrays.asList(
            new Card(Hearts, Two),
            new Card(Spades, Three),
            new Card(Diamonds, Four),
            new Card(Clubs, Five),
            new Card(Hearts, Six),
            new Card(Diamonds, Three),
            new Card(Spades, Two));
    Collections.shuffle(cards);
    final List<Card> expected =
        Arrays.asList(
            new Card(Spades, Two),
            new Card(Spades, Three),
            new Card(Diamonds, Four),
            new Card(Clubs, Five),
            new Card(Hearts, Six));

    // Test
    final List<Card> straight = checkForStraight(cards);

    // Verify.
    assertEquals(expected, straight);
  }

  @Test
  public void testCheckForStraight_2() {
    // Given.
    final List<Card> cards =
        Arrays.asList(
            new Card(Hearts, Eight),
            new Card(Spades, Three),
            new Card(Diamonds, Four),
            new Card(Clubs, Five),
            new Card(Hearts, Six),
            new Card(Diamonds, Three),
            new Card(Spades, Ten));
    Collections.shuffle(cards);

    // Test
    final List<Card> straight = checkForStraight(cards);

    // Verify.
    assertNull(straight);
  }

  @Test
  public void testCheckForStraight_3() {
    // Given.
    final List<Card> cards =
        Arrays.asList(
            new Card(Hearts, Two),
            new Card(Spades, Three),
            new Card(Diamonds, Four),
            new Card(Clubs, Five),
            new Card(Hearts, Six),
            new Card(Diamonds, Seven),
            new Card(Spades, Eight));
    Collections.shuffle(cards);
    final List<Card> expected =
        Arrays.asList(
            new Card(Diamonds, Four),
            new Card(Clubs, Five),
            new Card(Hearts, Six),
            new Card(Diamonds, Seven),
            new Card(Spades, Eight));

    // Test
    final List<Card> straight = checkForStraight(cards);

    // Verify.
    assertEquals(expected, straight);
  }

  @Test
  public void testCheckForSet_1() {
    // Given.
    final List<Card> cards =
        Arrays.asList(
            new Card(Hearts, Two),
            new Card(Spades, Two),
            new Card(Diamonds, Two),
            new Card(Clubs, Five),
            new Card(Hearts, Six),
            new Card(Diamonds, Seven),
            new Card(Spades, Eight));
    Collections.shuffle(cards);
    final List<Card> expected =
        Arrays.asList(
            new Card(Spades, Two),
            new Card(Hearts, Two),
            new Card(Diamonds, Two),
            new Card(Diamonds, Seven),
            new Card(Spades, Eight));

    // Test
    final List<Card> set = checkForSet(cards);

    // Verify
    assertEquals(expected, set);
  }

  @Test
  public void testCheckForSet_2() {
    // Given.
    final List<Card> cards =
        Arrays.asList(
            new Card(Hearts, Two),
            new Card(Spades, Two),
            new Card(Diamonds, Ten),
            new Card(Clubs, Five),
            new Card(Hearts, Six),
            new Card(Diamonds, Seven),
            new Card(Spades, Eight));
    Collections.shuffle(cards);

    // Test
    final List<Card> set = checkForSet(cards);

    // Verify
    assertNull(set);
  }

  @Test
  public void testCheckForSet_3() {
    // Given.
    final List<Card> cards =
        Arrays.asList(
            new Card(Hearts, Jack),
            new Card(Spades, Jack),
            new Card(Diamonds, Two),
            new Card(Clubs, Five),
            new Card(Hearts, Six),
            new Card(Diamonds, Jack),
            new Card(Spades, Ace));
    Collections.shuffle(cards);
    final List<Card> expected =
        Arrays.asList(
            new Card(Spades, Jack),
            new Card(Hearts, Jack),
            new Card(Diamonds, Jack),
            new Card(Hearts, Six),
            new Card(Spades, Ace));

    // Test
    final List<Card> set = checkForSet(cards);

    // Verify
    assertEquals(expected, set);
  }

  @Test
  public void testCheckForTwoPairs_1() {
    // Given.
    final List<Card> cards =
        Arrays.asList(
            new Card(Hearts, Jack),
            new Card(Spades, Jack),
            new Card(Diamonds, Two),
            new Card(Clubs, Two),
            new Card(Hearts, Six),
            new Card(Diamonds, Seven),
            new Card(Spades, Ace));
    Collections.shuffle(cards);
    final List<Card> expected =
        Arrays.asList(
            new Card(Spades, Jack),
            new Card(Hearts, Jack),
            new Card(Clubs, Two),
            new Card(Diamonds, Two),
            new Card(Spades, Ace));

    // Test
    final List<Card> twoPairs = checkForTwoPair(cards);

    // Verify
    assertEquals(expected, twoPairs);
  }

  @Test
  public void testCheckForTwoPairs_2() {
    // Given.
    final List<Card> cards =
        Arrays.asList(
            new Card(Hearts, Jack),
            new Card(Spades, Jack),
            new Card(Diamonds, Two),
            new Card(Clubs, Nine),
            new Card(Hearts, Six),
            new Card(Diamonds, Seven),
            new Card(Spades, Ace));
    Collections.shuffle(cards);

    // Test
    final List<Card> twoPairs = checkForTwoPair(cards);

    // Verify
    assertNull(twoPairs);
  }

  @Test
  public void testCheckPair_1() {
    // Given.
    final List<Card> cards =
        Arrays.asList(
            new Card(Hearts, Jack),
            new Card(Spades, Jack),
            new Card(Diamonds, Two),
            new Card(Clubs, Three),
            new Card(Hearts, Four),
            new Card(Diamonds, Eight),
            new Card(Spades, Nine));
    Collections.shuffle(cards);
    final List<Card> expected =
        Arrays.asList(
            new Card(Spades, Jack),
            new Card(Hearts, Jack),
            new Card(Hearts, Four),
            new Card(Diamonds, Eight),
            new Card(Spades, Nine));

    // Test
    final List<Card> pair = checkForPair(cards);

    // Verify
    assertEquals(expected, pair);
  }

  @Test
  public void testCheckPair_2() {
    // Given.
    final List<Card> cards =
        Arrays.asList(
            new Card(Hearts, Jack),
            new Card(Spades, King),
            new Card(Diamonds, Two),
            new Card(Clubs, Three),
            new Card(Hearts, Four),
            new Card(Diamonds, Eight),
            new Card(Spades, Nine));
    Collections.shuffle(cards);

    // Test
    final List<Card> pair = checkForPair(cards);

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
    final List<Card> worstHand =
        Arrays.asList(
            new Card(Hearts, Two),
            new Card(Spades, Three),
            new Card(Diamonds, Four),
            new Card(Clubs, Five),
            new Card(Hearts, Seven),
            new Card(Diamonds, Eight),
            new Card(Spades, Nine));

    final List<Card> bestHighCard =
        Arrays.asList(
            new Card(Hearts, Ace),
            new Card(Spades, King),
            new Card(Diamonds, Queen),
            new Card(Clubs, Jack),
            new Card(Hearts, Nine),
            new Card(Diamonds, Eight),
            new Card(Spades, Seven));

    final List<Card> worstPair = getPairWithKickers(Two, Three, Four, Five);
    worstPair.addAll(Arrays.asList(getCard(Clubs, Seven), getCard(Diamonds, Eight)));

    final List<Card> bestPair = getPairWithKickers(Ace, King, Queen, Jack);
    bestPair.addAll(Arrays.asList(getCard(Nine), getCard(Eight)));

    final List<Card> worstTwoPair = getTwoPairWithKicker(Two, Three, Four);
    worstTwoPair.addAll(Arrays.asList(getCard(Five), getCard(Seven)));

    final List<Card> bestTwoPair = getTwoPairWithKicker(Ace, King, Queen);
    bestTwoPair.addAll(Arrays.asList(getCard(Jack), getCard(Nine)));

    final List<Card> worstSet = getSetWithKickers(Two, Three, Four);
    worstSet.addAll(Arrays.asList(getCard(Five), getCard(Seven)));

    final List<Card> bestSet = getSetWithKickers(Ace, King, Queen);
    bestSet.addAll(Arrays.asList(getCard(Jack), getCard(Nine)));

    // Also a set.
    final List<Card> worstStraight =
        Arrays.asList(
            new Card(Hearts, Ace),
            new Card(Spades, Two),
            new Card(Diamonds, Three),
            new Card(Clubs, Four),
            new Card(Hearts, Five),
            new Card(Diamonds, Ace),
            new Card(Spades, Ace));

    // Also a set.
    final List<Card> bestStraight =
        Arrays.asList(
            new Card(Hearts, Ace),
            new Card(Spades, King),
            new Card(Diamonds, Queen),
            new Card(Clubs, Jack),
            new Card(Hearts, Ten),
            new Card(Diamonds, Ace),
            new Card(Spades, Ace));

    // Also a straight.
    final List<Card> worstFlush =
        Arrays.asList(
            new Card(Hearts, Two),
            new Card(Hearts, Three),
            new Card(Hearts, Four),
            new Card(Hearts, Five),
            new Card(Hearts, Seven),
            new Card(Diamonds, Six),
            new Card(Spades, Eight));

    // Also a straight.
    final List<Card> bestFlush =
        Arrays.asList(
            new Card(Hearts, King),
            new Card(Hearts, Queen),
            new Card(Hearts, Jack),
            new Card(Hearts, Ace),
            new Card(Hearts, Nine),
            new Card(Diamonds, Ten),
            new Card(Spades, Ace));

    final List<Card> worstFullHouse = getFullHouse(Two, Three);
    worstFullHouse.addAll(Arrays.asList(getCard(Spades, Four), getCard(Spades, Five)));

    final List<Card> bestFullHouse = getFullHouse(Ace, King);
    bestFullHouse.addAll(Arrays.asList(getCard(Diamonds, King), getCard(Spades, Queen)));

    final List<Card> worstFourOfAKind = getAllWithValue(Two);
    worstFourOfAKind.addAll(Arrays.asList(getCard(Three), getCard(Four), getCard(Five)));

    final List<Card> bestFourOfAKind = getAllWithValue(Ace);
    bestFourOfAKind.addAll(Arrays.asList(getCard(King), getCard(King), getCard(King)));

    final List<Card> worstStraightFlush =
        Arrays.asList(
            getCard(Spades, Ace),
            getCard(Spades, Two),
            getCard(Spades, Three),
            getCard(Spades, Four),
            getCard(Spades, Five),
            getCard(Hearts, Ace),
            getCard(Clubs, Ace));

    final List<Card> bestStraightFlush = getAllWithSuit(Spades).subList(8, 13);
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
