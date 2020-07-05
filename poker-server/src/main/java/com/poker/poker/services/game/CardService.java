package com.poker.poker.services.game;

import com.poker.poker.models.enums.CardSuit;
import com.poker.poker.models.enums.CardValue;
import com.poker.poker.models.game.CardModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Service that can perform useful operations for a card game.
 */
@Slf4j
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CardService implements Comparator<CardModel> {

  private final Map<CardValue, Integer> numericalCardValues;
  private final Map<Integer, CardValue> enumCardValues;
  private final Map<CardSuit, Integer> numericalSuitValues;
  private final Map<Integer, CardSuit> enumSuitValues;

  public CardService() {
    numericalCardValues = new HashMap<>();
    enumCardValues = new HashMap<>();
    numericalSuitValues = new HashMap<>();
    enumSuitValues = new HashMap<>();
    fillMaps();
  }

  /**
   * Returns the numerical value of a particular suit.
   *
   * @param suit The suit.
   * @return The numerical value of the suit.
   */
  public int suitValue(final CardSuit suit) {
    return numericalSuitValues.get(suit);
  }

  /**
   * Returns the numerical value of a particular card (ignoring suit).
   *
   * @param value The value of the card.
   * @return The numerical value of the card.
   */
  public int faceValue(final CardValue value) {
    return numericalCardValues.get(value);
  }

  @Override
  public int compare(CardModel a, CardModel b) {
    int difference = faceValue(a.getValue()) - faceValue(b.getValue());
    return difference == 0 ? suitValue(a.getSuit()) - suitValue(b.getSuit()) : difference;
  }

  /**
   * Creates a list of CardModels in the order of a fresh deck.
   *
   * @return List of CardModels in the order of a fresh deck.
   */
  public List<CardModel> getFreshDeck() {
    final List<CardModel> deck = new ArrayList<>();
    for (int i = 0; i < enumSuitValues.size(); i++) {
      for (int j = 0; j < enumCardValues.size(); i++) {
        deck.add(new CardModel(enumSuitValues.get(i), enumCardValues.get(j)));
      }
    }
    return deck;
  }

  private void fillMaps() {
    numericalCardValues.put(CardValue.Ace, 13);
    numericalCardValues.put(CardValue.King, 12);
    numericalCardValues.put(CardValue.Queen, 11);
    numericalCardValues.put(CardValue.Jack, 10);
    numericalCardValues.put(CardValue.Ten, 9);
    numericalCardValues.put(CardValue.Nine, 8);
    numericalCardValues.put(CardValue.Eight, 7);
    numericalCardValues.put(CardValue.Seven, 6);
    numericalCardValues.put(CardValue.Six, 5);
    numericalCardValues.put(CardValue.Five, 4);
    numericalCardValues.put(CardValue.Four, 3);
    numericalCardValues.put(CardValue.Three, 2);
    numericalCardValues.put(CardValue.Two, 1);

    enumCardValues.put(13, CardValue.Ace);
    enumCardValues.put(12, CardValue.King);
    enumCardValues.put(11, CardValue.Queen);
    enumCardValues.put(10, CardValue.Jack);
    enumCardValues.put(9, CardValue.Ten);
    enumCardValues.put(8, CardValue.Nine);
    enumCardValues.put(7, CardValue.Eight);
    enumCardValues.put(6, CardValue.Seven);
    enumCardValues.put(5, CardValue.Six);
    enumCardValues.put(4, CardValue.Five);
    enumCardValues.put(3, CardValue.Four);
    enumCardValues.put(2, CardValue.Three);
    enumCardValues.put(1, CardValue.Two);

    numericalSuitValues.put(CardSuit.Spades, 4);
    numericalSuitValues.put(CardSuit.Hearts, 3);
    numericalSuitValues.put(CardSuit.Clubs, 2);
    numericalSuitValues.put(CardSuit.Diamonds, 1);

    enumSuitValues.put(4, CardSuit.Spades);
    enumSuitValues.put(3, CardSuit.Hearts);
    enumSuitValues.put(2, CardSuit.Clubs);
    enumSuitValues.put(1, CardSuit.Diamonds);
  }
}
