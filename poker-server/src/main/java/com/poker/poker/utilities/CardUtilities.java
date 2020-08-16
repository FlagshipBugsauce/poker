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
import static com.poker.poker.models.enums.HandType.Flush;
import static com.poker.poker.models.enums.HandType.FourOfAKind;
import static com.poker.poker.models.enums.HandType.FullHouse;
import static com.poker.poker.models.enums.HandType.HighCard;
import static com.poker.poker.models.enums.HandType.Pair;
import static com.poker.poker.models.enums.HandType.Set;
import static com.poker.poker.models.enums.HandType.Straight;
import static com.poker.poker.models.enums.HandType.StraightFlush;
import static com.poker.poker.models.enums.HandType.TwoPair;

import com.poker.poker.models.enums.CardSuit;
import com.poker.poker.models.enums.CardValue;
import com.poker.poker.models.enums.HandType;
import com.poker.poker.models.game.CardModel;
import com.poker.poker.models.game.HandRankModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Data;

/**
 * Utility class that can be used to sort collections of cards, rank hands, and also has some useful
 * constants.
 */
public final class CardUtilities {

  /**
   * Sorting order constant which can be passed to the comparator methods to specify the sort order.
   */
  public static final int ASCENDING = 1;
  /**
   * Sorting order constant which can be passed to the comparator methods to specify the sort order.
   */
  public static final int DESCENDING = -1;

  /**
   * This constant is the numerical base used to calculate hand ranks. Hand ranks are 6 digit base
   * 15 numbers. The most significant digit for all hands is the hand type, since this has the most
   * significant impact on the rank of a hand. The next most significant digit varies depending on
   * the hand type. For a straight flush, the next most significant digit is the high card. For a
   * full house, the next most significant digit is the value of the set, and after that, the value
   * of the pair. For a pair, the most significant digits are the pair value, followed by the values
   * of the three kickers, etc...
   */
  public static final int RANK_BASE = 15;

  /** Face down card. */
  public static final CardModel FACE_DOWN_CARD = new CardModel(CardSuit.Back, CardValue.Back);

  /**
   * Mapping of card values to a number that is less than 15. These values are used for sorting and
   * determining hand ranks.
   */
  public static final Map<CardValue, Integer> cardValues =
      new HashMap<CardValue, Integer>() {
        {
          put(CardValue.Back, 1);
          put(Two, 2);
          put(Three, 3);
          put(Four, 4);
          put(Five, 5);
          put(Six, 6);
          put(Seven, 7);
          put(Eight, 8);
          put(Nine, 9);
          put(Ten, 10);
          put(Jack, 11);
          put(Queen, 12);
          put(King, 13);
          put(Ace, 14);
        }
      };

  /** Mapping of card suits to numbers, to help with sorting. */
  public static final Map<CardSuit, Integer> cardSuitValues =
      new HashMap<CardSuit, Integer>() {
        {
          put(CardSuit.Back, 0);
          put(Spades, 1);
          put(Hearts, 2);
          put(Clubs, 3);
          put(Diamonds, 4);
        }
      };

  /** Mapping of hand types to numbers, used to generate hand ranks. */
  public static final Map<HandType, Integer> handTypeValues =
      new HashMap<HandType, Integer>() {
        {
          put(StraightFlush, 8);
          put(FourOfAKind, 7);
          put(FullHouse, 6);
          put(Flush, 5);
          put(Straight, 4);
          put(Set, 3);
          put(TwoPair, 2);
          put(Pair, 1);
          put(HighCard, 0);
        }
      };

  /**
   * Ordered list of hand evaluators, which helps to easily rank hands by iterating over this list.
   * Accurate and predictable results are only guaranteed if each evaluator is executed in the order
   * of the list.
   */
  public static final List<Evaluator> evaluators =
      Arrays.asList(
          new Evaluator(StraightFlush, CardUtilities::checkForStraightFlush),
          new Evaluator(FourOfAKind, CardUtilities::checkForFourOfAKind),
          new Evaluator(FullHouse, CardUtilities::checkForFullHouse),
          new Evaluator(Flush, CardUtilities::checkForFlush),
          new Evaluator(Straight, CardUtilities::checkForStraight),
          new Evaluator(Set, CardUtilities::checkForSet),
          new Evaluator(TwoPair, CardUtilities::checkForTwoPair),
          new Evaluator(Pair, CardUtilities::checkForPair),
          new Evaluator(HighCard, CardUtilities::checkForHighCard));

  /** Private constructor to prevent creating instances of static class. */
  private CardUtilities() {}

  /**
   * All methods in this class that take in a collection of cards must satisfy these pre-conditions.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>cards != null</code>
   *   <li>All elements in <code>cards</code> are <code>non-null</code>
   *   <li>No elements have a suit or value of <code>Back</code>
   * </ol>
   *
   * @param cards Cards.
   * @return <code>true</code> if all pre-conditions are satisfied.
   */
  public static boolean sharedPreCondition(final Collection<CardModel> cards) {
    // Validate Pre-Conditions.
    assert cards != null;
    assert cards.stream().noneMatch(Objects::isNull);
    assert cards.stream()
        .noneMatch(c -> c.getValue() == CardValue.Back || c.getSuit() == CardSuit.Back);
    return true;
  }

  /**
   * Helper function to easily retrieve the value of a card.
   *
   * @param card Card.
   * @return The value of a card.
   */
  public static int val(final CardModel card) {
    return cardValues.get(card.getValue());
  }

  /**
   * Helper function that will give Aces a value of 1, instead of 14. This is needed to detect
   * straights from Ace to 5.
   *
   * @param card Card.
   * @return Value of a card, treating Ace as 1.
   */
  public static int lowAceVal(final CardModel card) {
    return card.getValue() == Ace ? 1 : cardValues.get(card.getValue());
  }

  /**
   * Helper function to easily retrieve the suit value of a card. Suit value is only to help with
   * sorting.
   *
   * @param card Card.
   * @return The value of a card's suit.
   */
  public static int suitVal(final CardModel card) {
    return cardSuitValues.get(card.getSuit());
  }

  /**
   * Creates a Comparator to sort cards in ascending order based on their value. In the event of a
   * tie, cards are then sorted based on their suit. Spades > Hearts > Clubs > Diamonds.
   *
   * @return Comparator that sorts cards in ascending order based on their value.
   */
  public static Comparator<CardModel> valueSorter() {
    return valueSorter(ASCENDING);
  }

  /**
   * Creates a Comparator that allows sorting of cards based on their value in the specified order.
   * In the event of a tie, cards are then sorted based on their suit. Spades > Hearts > Clubs >
   * Diamonds.
   *
   * @param order Order the card should be sorted in. Should use <code>ASCENDING</code> and <code>
   *     DESCENDING</code> constants as the argument.
   * @return Comparator that sorts cards in the specified order based on their value.
   */
  public static Comparator<CardModel> valueSorter(final int order) {
    return (a, b) -> (val(a) == val(b) ? suitVal(a) - suitVal(b) : val(a) - val(b)) * order;
  }

  /**
   * Creates a Comparator to sort cards in ascending order based on their value. This comparator
   * will treat Ace as the lowest value. In the event of a tie, cards are then sorted based on their
   * suit. Spades > Hearts > Clubs > Diamonds.
   *
   * @return Comparator that sorts cards in ascending order based on their value.
   */
  public static Comparator<CardModel> lowAceValueSorter() {
    return lowAceValueSorter(ASCENDING);
  }

  /**
   * Creates a Comparator that allows sorting of cards based on their value in the specified order.
   * This comparator will treat Ace as the lowest value. In the event of a tie, cards are then
   * sorted based on their suit. Spades > Hearts > Clubs > Diamonds.
   *
   * @param order Order the card should be sorted in. Should use <code>ASCENDING</code> and <code>
   *     DESCENDING</code> constants as the argument.
   * @return Comparator that sorts cards in the specified order based on their value.
   */
  public static Comparator<CardModel> lowAceValueSorter(final int order) {
    return (a, b) ->
        (val(a) == val(b) ? suitVal(a) - suitVal(b) : lowAceVal(a) - lowAceVal(b)) * order;
  }

  /**
   * Creates a Comparator to sort cards based on their suit, in ascending order. Card values are
   * used to break ties.
   *
   * @return Comparator that sorts cards in ascending order based on their suit.
   */
  public static Comparator<CardModel> suitSorter() {
    return suitSorter(ASCENDING);
  }

  /**
   * Creates a Comparator that allows sorting of cards based on their suit in the specified order.
   * Card values are used to break ties.
   *
   * @param order Order the card should be sorted in. Should use <code>ASCENDING</code> and <code>
   *     DESCENDING</code> constants as the argument.
   * @return Comparator that sorts cards in the specified order based on their suit.
   */
  public static Comparator<CardModel> suitSorter(final int order) {
    return (a, b) -> (suitVal(a) == suitVal(b) ? val(a) - val(b) : suitVal(a) - suitVal(b)) * order;
  }

  /**
   * Splits up a collection of cards into separate lists, based on suit of each card, and stores the
   * lists in a map keyed by the suit corresponding to each list, which is returned.
   *
   * @param cards Cards.
   * @return Map keyed by suit, which stores lists of cards which have the same suit.
   */
  public static Map<CardSuit, List<CardModel>> splitBySuit(final Collection<CardModel> cards) {
    assert sharedPreCondition(cards);

    final Map<CardSuit, List<CardModel>> result = new HashMap<>();
    for (final CardSuit suit : CardSuit.values()) {
      result.put(
          suit,
          cards.stream()
              .filter(c -> c.getSuit() == suit)
              .sorted(valueSorter())
              .collect(Collectors.toList()));
    }
    return result;
  }

  /**
   * Splits up a collection of cards into separate lists, based on the value of each card, and
   * stores the lists in a map keyed by the value corresponding to each list, which is returned.
   *
   * @param cards Cards.
   * @return Map keyed by value, which stores lists of cards which have the same value.
   */
  public static Map<CardValue, List<CardModel>> splitByValue(final Collection<CardModel> cards) {
    assert sharedPreCondition(cards);

    final Map<CardValue, List<CardModel>> result = new HashMap<>();
    for (final CardValue v : CardValue.values()) {
      result.put(
          v,
          cards.stream()
              .filter(c -> c.getValue() == v)
              .sorted(suitSorter())
              .collect(Collectors.toList()));
    }
    return result;
  }

  /**
   * Helper which will detect a straight, accounting for the edge case where the straight begins
   * with an Ace. If a straight is detected, the cards that make up the straight are returned in
   * sorted order, otherwise <code>null</code> is returned.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>cards != null</code>
   *   <li>All elements in <code>cards</code> are <code>non-null</code>
   *   <li>No elements have a suit or value of <code>Back</code>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Returns <code>null</code> if there aren't 5 cards with consecutive values.
   *   <li>Returns the highest 5 consecutive cards if there are 5 cards with consecutive values.
   * </ol>
   *
   * @param cards Cards.
   * @return Five consecutive cards, if there are 5 consecutive cards in <code>cards</code>, <code>
   *     null</code> otherwise.
   */
  public static List<CardModel> checkForFiveConsecutiveCards(final Collection<CardModel> cards) {
    assert sharedPreCondition(cards);

    if (cards.size() < 5) {
      return null;
    }

    final List<CardModel> lowAceCards =
        cards.stream().map(CardModel::new).sorted(lowAceValueSorter()).collect(Collectors.toList());
    final List<CardModel> highAceCards =
        cards.stream().map(CardModel::new).sorted(valueSorter()).collect(Collectors.toList());

    int lowIndex1 = -1, lowIndex2 = -1;

    for (int i = 0; i < lowAceCards.size() - 4; i++) {
      if (lowAceVal(lowAceCards.get(i)) + 4 == lowAceVal(lowAceCards.get(i + 4))) {
        lowIndex1 = i;
      }
      if (val(highAceCards.get(i)) + 4 == val(highAceCards.get(i + 4))) {
        lowIndex2 = i;
      }
    }

    if (lowIndex1 == -1 && lowIndex2 == -1) {
      return null;
    } else if (lowIndex2 == -1) {
      return lowAceCards.subList(lowIndex1, lowIndex1 + 5);
    } else {
      return highAceCards.subList(lowIndex2, lowIndex2 + 5);
    }
  }

  /**
   * Checks if there is a straight flush, returns the highest straight flush is one is found,
   * otherwise returns <code>null</code>. If cards are returned, the cards will be in sorted order,
   * with the highest card being found at index 4. When using this method to evaluate a hand, in
   * order to ensure accurate and predictable results, this particular method should be called
   * before any of the other evaluation methods.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>cards != null</code>
   *   <li>All elements in <code>cards</code> are <code>non-null</code>
   *   <li>No elements have a suit or value of <code>Back</code>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Returns <code>null</code> if no straight flush is detected.
   *   <li>Returns the best straight flush found if a straight flush is detected.
   * </ol>
   *
   * @param cards List of 7 cards.
   * @return The cards that make up the straight flush, or <code>null</code> if there is no straight
   *     flush.
   */
  public static List<CardModel> checkForStraightFlush(final Collection<CardModel> cards) {
    assert sharedPreCondition(cards);

    List<CardModel> result = null;
    final Map<CardSuit, List<CardModel>> suitedCards = splitBySuit(cards);
    for (final List<CardModel> suited : suitedCards.values()) {
      final List<CardModel> temp = checkForFiveConsecutiveCards(suited);
      result = temp == null ? result : temp;
    }
    return result;
  }

  /**
   * Checks if there is a 5-card hand with 4-of-a-kind. Returns a list with the form [X, X, X, X,
   * kicker], or <code>null</code> if there is no 4-of-a-kind. This allows for easy ranking since
   * the high-card is in a predictable index. When using this method to evaluate a hand, to ensure
   * accurate and predictable results, this method should only be called after the <code>
   * checkForStraightFlush</code> method has been called and has returned <code>null</code>.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>cards != null</code>
   *   <li>All elements in <code>cards</code> are <code>non-null</code>
   *   <li>No elements have a suit or value of <code>Back</code>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Returns <code>null</code> if no 4-of-a-kind is detected.
   *   <li>Returns the best hand with 4-of-a-kind if 4-of-a-kind is detected.
   *   <li>Kicker will be in index 4 of retarded cards if 4-of-a-kind is found.
   * </ol>
   *
   * @param cards Cards.
   * @return The best 5-card hand that includes 4-of-a-kind if there are four of the same card in
   *     <code>cards</code>, otherwise returns <code>null</code>.
   */
  public static List<CardModel> checkForFourOfAKind(final List<CardModel> cards) {
    assert sharedPreCondition(cards);

    final Map<CardValue, List<CardModel>> values = splitByValue(cards);
    List<CardModel> result = null;
    for (final CardValue v : CardValue.values()) {
      if (values.get(v).size() == 4) {
        result = values.get(v);
      }
    }
    if (result != null) {
      // Find the high card for the hand.
      cards.sort(valueSorter(DESCENDING));
      int i = 0;
      while (cards.get(i).getValue() == result.get(0).getValue()) {
        i++;
      }
      result.add(cards.get(i));
    }
    return result;
  }

  /**
   * Checks if there is a full house in the cards provided. If a full house is found, the best full
   * house will be returned with the three-of-a-kind occupying the first three indices of the
   * returned list and the pair occupying the last two indices. If no full house is found, <code>
   * null</code> is returned. When using this method to evaluate a hand, to ensure accurate and
   * predictable results, this method should only be called after the <code>checkForFourOfAKind
   * </code> method has been called and has returned <code>null</code>.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>cards != null</code>
   *   <li>All elements in <code>cards</code> are <code>non-null</code>
   *   <li>No elements have a suit or value of <code>Back</code>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Returns <code>null</code> if no full house is detected.
   *   <li>Returns the best full house found if a full house is detected.
   *   <li>Set occupies first 3 indices of returned cards if a full house is found.
   * </ol>
   *
   * @param cards Cards.
   * @return The best full house found in the provided cards, or <code>null</code> if no full house
   *     is found.
   */
  public static List<CardModel> checkForFullHouse(final Collection<CardModel> cards) {
    assert sharedPreCondition(cards);

    final Map<CardValue, List<CardModel>> values = splitByValue(cards);
    List<CardModel> set = findSet(values);
    if (set == null) {
      return null;
    }

    List<CardModel> pair = findSecondPair(values, set);
    if (pair == null) {
      return null;
    }
    set.sort(valueSorter());
    pair.sort(valueSorter());
    final List<CardModel> result = new ArrayList<>(set);
    result.addAll(pair);
    return result;
  }

  /**
   * Checks for a flush in the cards provided. If a flush is found, the best flush is returned,
   * otherwise <code>null</code> is returned. When using this method to evaluate a hand, to ensure
   * accurate and predictable results, this method should only be called after the <code>
   * checkForFullHouse</code> method has been called and has returned <code>null</code>.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>cards != null</code>
   *   <li>All elements in <code>cards</code> are <code>non-null</code>
   *   <li>No elements have a suit or value of <code>Back</code>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Returns <code>null</code> if no flush is detected.
   *   <li>Returns the best flush found if a flush is detected.
   *   <li>If a flush is found, the returned flush will be in sorted order.
   * </ol>
   *
   * @param cards Cards.
   * @return The best flush in the cards provided if a flush is found, otherwise <code>null</code>.
   */
  public static List<CardModel> checkForFlush(final Collection<CardModel> cards) {
    assert sharedPreCondition(cards);

    final Map<CardSuit, List<CardModel>> suitedCards = splitBySuit(cards);
    List<CardModel> result = null;
    for (final CardSuit suit : CardSuit.values()) {
      if (suitedCards.get(suit).size() >= 5) {
        result =
            suitedCards
                .get(suit)
                .subList(suitedCards.get(suit).size() - 5, suitedCards.get(suit).size());
      }
    }
    return result;
  }

  /**
   * Checks for a straight in the cards provided. If a straight is found, the best straight is
   * returned, otherwise <code>null</code> is returned. When using this method to evaluate a hand,
   * to ensure accurate and predictable results, this method should only be called after the <code>
   * checkForFlush</code> method has been called and has returned <code>null</code>.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>cards != null</code>
   *   <li>All elements in <code>cards</code> are <code>non-null</code>
   *   <li>No elements have a suit or value of <code>Back</code>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Returns <code>null</code> if no straight is detected.
   *   <li>Returns the best straight found if a straight is detected.
   * </ol>
   *
   * @param cards Cards
   * @return The best straight if a straight is found, otherwise <code>null</code>.
   */
  public static List<CardModel> checkForStraight(final Collection<CardModel> cards) {
    assert sharedPreCondition(cards);

    final Map<CardValue, List<CardModel>> values = splitByValue(cards);
    final Collection<CardModel> result = new ArrayList<>();

    // Keep cards with distinct values only.
    for (final List<CardModel> sameValuedCards : values.values()) {
      if (!sameValuedCards.isEmpty()) {
        result.add(sameValuedCards.get(0));
      }
    }
    if (result.size() < 5) {
      return null;
    }
    return checkForFiveConsecutiveCards(result);
  }

  /**
   * Takes in a mapping of card lists, keyed by the card value associated with the cards in each
   * list and returns the highest set found in the map. Used in the checkForFullHouse and
   * checkForSet methods. If no set is found, <code>null</code> is returned.
   *
   * @param values Mapping of lists of cards, keyed by the value associated with the cards in each
   *     list.
   * @return The highest set found in the map.
   */
  public static List<CardModel> findSet(final Map<CardValue, List<CardModel>> values) {
    List<CardModel> set = null;
    for (final CardValue v : CardValue.values()) {
      if (values.get(v).size() >= 3) {
        set = set == null ? values.get(v) : set;
        set = val(values.get(v).get(0)) > val(set.get(0)) ? values.get(v) : set;
      }
    }
    return set;
  }

  /**
   * Checks for a set in the cards provided. If a set is found, the best set is returned, otherwise
   * <code>null</code> is returned. When using this method to evaluate a hand, to ensure accurate
   * and predictable results, this method should only be called after the <code>checkForStraight
   * </code> method has been called and has returned <code>null</code>.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>cards != null</code>
   *   <li>All elements in <code>cards</code> are <code>non-null</code>
   *   <li>No elements have a suit or value of <code>Back</code>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Returns <code>null</code> if no set is detected.
   *   <li>Returns the best set found if a set is detected.
   *   <li>If a set is found, the returned set will be in sorted order.
   * </ol>
   *
   * @param cards Cards.
   * @return The best flush in the cards provided if a flush is found, otherwise <code>null</code>.
   */
  public static List<CardModel> checkForSet(final List<CardModel> cards) {
    assert sharedPreCondition(cards);

    final Map<CardValue, List<CardModel>> values = splitByValue(cards);
    List<CardModel> set = findSet(values);
    if (set == null) {
      return null;
    }
    return addKickers(cards, set);
  }

  /**
   * Takes in a mapping of card lists, keyed by the card value associated with the cards in each
   * list and returns the highest pair found in the map. Used in the checkForPair and
   * checkForTwoPair methods. If no pair is found, <code>null</code> is returned.
   *
   * @param values Mapping of lists of cards, keyed by the value associated with the cards in each
   *     list.
   * @return The highest pair found in the map.
   */
  public static List<CardModel> findFirstPair(final Map<CardValue, List<CardModel>> values) {
    List<CardModel> pair = null;
    for (final CardValue v : CardValue.values()) {
      if (values.get(v).size() >= 2) {
        pair = pair == null ? values.get(v) : pair;
        pair = val(values.get(v).get(0)) > val(pair.get(0)) ? values.get(v) : pair;
      }
    }
    return pair;
  }

  /**
   * Takes in a mapping of card lists, keyed by the card value associated with the cards in each
   * list and returns the highest pair found in the map, that isn't the pair in <code>first</code>.
   * Used in the checkForPair and checkForTwoPair methods. If there is no other pair, <code>null
   * </code> is returned.
   *
   * @param values Mapping of lists of cards, keyed by the value associated with the cards in each
   *     list.
   * @param first The first pair that was found (needed to avoid returning the same pair).
   * @return The best pair that isn't the pair in the <code>first</code> argument.
   */
  public static List<CardModel> findSecondPair(
      final Map<CardValue, List<CardModel>> values, final List<CardModel> first) {
    List<CardModel> pair = null;
    for (final CardValue v : CardValue.values()) {
      if (values.get(v).size() >= 2 && !values.get(v).equals(first)) {
        pair = pair == null ? values.get(v) : pair;
        pair = val(values.get(v).get(0)) > val(pair.get(0)) ? values.get(v) : pair;
      }
    }
    return pair;
  }

  /**
   * Checks for a two pairs in the cards provided. If two pairs are found, the best two pairs is
   * returned, otherwise <code>null</code> is returned. The returned hand will have the highest pair
   * in the first two indices, the lower pair will be in indices 2 and 3 and the kicker will be in
   * index 4, of the returned list. When using this method to evaluate a hand, to ensure accurate
   * and predictable results, this method should only be called after the <code>checkForSet</code>
   * method has been called and has returned <code>null</code>.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>cards != null</code>
   *   <li>All elements in <code>cards</code> are <code>non-null</code>
   *   <li>No elements have a suit or value of <code>Back</code>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Returns <code>null</code> if two pairs aren't detected.
   *   <li>Returns the best two pairs found if two pairs are detected.
   *   <li>If two pairs are found, the highest pair will be in the first 2 indices, followed by the
   *       lower pair and the kicker will be in the highest index of the returned list.
   * </ol>
   *
   * @param cards Cards.
   * @return The best two pairs with the best kicker in the cards provided if two pairs are found,
   *     otherwise <code>null</code>.
   */
  public static List<CardModel> checkForTwoPair(final List<CardModel> cards) {
    assert sharedPreCondition(cards);

    final Map<CardValue, List<CardModel>> values = splitByValue(cards);
    List<CardModel> pair1 = findFirstPair(values);
    if (pair1 == null) {
      return null;
    }

    List<CardModel> pair2 = findSecondPair(values, pair1);
    if (pair2 == null) {
      return null;
    }
    pair1.addAll(pair2);

    return addKickers(cards, pair1);
  }

  /**
   * Checks for a pair in the cards provided. If a pair is found, the best pair is returned, along
   * with the three best kickers, otherwise <code>null</code> is returned. If a pair is found, the
   * pair will be in the first two indices, followed by the three best kickers in sorted order, in
   * the returned list of cards. When using this method to evaluate a hand, to ensure accurate and
   * predictable results, this method should only be called after the <code>checkForTwoPair</code>
   * method has been called and has returned <code>null</code>.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>cards != null</code>
   *   <li>All elements in <code>cards</code> are <code>non-null</code>
   *   <li>No elements have a suit or value of <code>Back</code>
   * </ol>
   *
   * <ol>
   *   <b>Post-Conditions:</b>
   *   <li>Returns <code>null</code> if no pair is detected.
   *   <li>Returns the best pair found if a pair is detected.
   *   <li>If a pair is found, the pair will be in the first two indices of the returned list,
   *       followed by the three kickers in sorted order.
   * </ol>
   *
   * @param cards Cards.
   * @return The best pair in the cards provided if a pair is found, otherwise <code>null</code>.
   */
  public static List<CardModel> checkForPair(final List<CardModel> cards) {
    assert sharedPreCondition(cards);

    List<CardModel> pair = findFirstPair(splitByValue(cards));
    if (pair == null) {
      return null;
    }
    return addKickers(cards, pair);
  }

  /**
   * Returns the five highest cards in the hand in sorted order (ascending). When using this method
   * to evaluate a hand, to ensure accurate and predictable results, this method should only be
   * called after the <code>checkForPair</code> method has been called and has returned <code>null
   * </code>.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>cards != null</code>
   *   <li>All elements in <code>cards</code> are <code>non-null</code>
   *   <li>No elements have a suit or value of <code>Back</code>
   * </ol>
   *
   * @param cards Cards.
   * @return The five highest cards in sorted order.
   */
  public static List<CardModel> checkForHighCard(final List<CardModel> cards) {
    assert sharedPreCondition(cards);

    cards.sort(valueSorter());
    return cards.subList(cards.size() - 5, cards.size());
  }

  /**
   * Helper to pad certain hand types with the best possible kickers. For example, if <code>result
   * </code> contains a pair, this method will fill indices 2, 3 and 4 with the three highest cards
   * from <code>cards</code> which are not in <code>result</code>.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>cards != null</code>
   *   <li>All elements in <code>cards</code> are <code>non-null</code>
   *   <li>No elements have a suit or value of <code>Back</code>
   *   <li><code>result != null</code>
   *   <li>
   * </ol>
   *
   * @param cards Cards.
   * @param result Part of a 5 card hand that needs to be padded with kickers.
   * @return A 5 card hand with the best possible kickers.
   */
  private static List<CardModel> addKickers(
      final List<CardModel> cards, final List<CardModel> result) {
    assert sharedPreCondition(cards);

    cards.sort(valueSorter(DESCENDING));
    final List<CardModel> kickers = new ArrayList<>();
    for (final CardModel card : cards) {
      if (!result.contains(card)) {
        kickers.add(card);
      }
      if (result.size() + kickers.size() == 5) {
        break;
      }
    }
    kickers.sort(valueSorter());
    result.addAll(kickers);
    return result;
  }

  /**
   * Takes a list of 7 cards, finds the best 5 card hand that can be made and gives it a numerical
   * ranking. The object returned contains the best 5 card hand and the numerical rank of that hand.
   *
   * <ol>
   *   <b>Pre-Conditions:</b>
   *   <li><code>cards != null</code>
   *   <li>All elements in <code>cards</code> are <code>non-null</code>
   *   <li>No elements have a suit or value of <code>Back</code>
   *   <li>cards.size() == 7
   * </ol>
   *
   * @param cards Cards.
   * @return The best 5 card hand that can be made with the list of 7 cards provided, along with the
   *     numerical ranking of the best hand.
   */
  public static HandRankModel rankHand(final List<CardModel> cards) {
    assert sharedPreCondition(cards);
    assert cards.size() == 7;

    int rank;
    List<CardModel> bestHand = null;
    HandType handType = HighCard;

    for (final Evaluator evaluator : evaluators) {
      bestHand = evaluator.evaluate(cards);
      if (bestHand != null) {
        handType = evaluator.getType();
        break;
      }
    }
    assert bestHand != null;
    rank = (int) (handTypeValues.get(handType) * Math.pow(RANK_BASE, 5));
    switch (handType) {
      case StraightFlush:
      case Straight:
        rank += val(bestHand.get(4));
        break;
      case FourOfAKind:
      case FullHouse:
        rank += val(bestHand.get(0)) * RANK_BASE + val(bestHand.get(4));
        break;
      case Flush:
      case HighCard:
        rank += val(bestHand.get(0));
        rank += val(bestHand.get(1)) * RANK_BASE;
        rank += val(bestHand.get(2)) * Math.pow(RANK_BASE, 2);
        rank += val(bestHand.get(3)) * Math.pow(RANK_BASE, 3);
        rank += val(bestHand.get(4)) * Math.pow(RANK_BASE, 4);
        break;
      case Set:
      case TwoPair:
        rank += val(bestHand.get(0)) * Math.pow(RANK_BASE, 2);
        rank += val(bestHand.get(3)) * RANK_BASE;
        rank += val(bestHand.get(4));
        break;
      case Pair:
        rank += val(bestHand.get(0)) * Math.pow(RANK_BASE, 3);
        rank += val(bestHand.get(2)) * Math.pow(RANK_BASE, 2);
        rank += val(bestHand.get(3)) * RANK_BASE;
        rank += val(bestHand.get(4));
        break;
    }

    return new HandRankModel(rank, bestHand);
  }

  /** Wrapper for hand evaluators. */
  @Data
  private static final class Evaluator {

    private final HandType type;
    private final Function<List<CardModel>, List<CardModel>> evaluator;

    public final List<CardModel> evaluate(final List<CardModel> cards) {
      return evaluator.apply(cards);
    }
  }
}
