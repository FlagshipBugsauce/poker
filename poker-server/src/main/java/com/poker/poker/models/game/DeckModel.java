package com.poker.poker.models.game;

import com.poker.poker.models.enums.CardSuit;
import com.poker.poker.models.enums.CardValue;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class DeckModel {

  // TODO: Refactor to use a stack, since this is more appropriate.
  @ArraySchema(schema = @Schema(implementation = CardModel.class))
  private List<CardModel> cards;

  @ArraySchema(schema = @Schema(implementation = CardModel.class))
  private List<CardModel> usedCards;

  public DeckModel() {
    freshDeck();
    usedCards = new ArrayList<>(52);
  }

  public DeckModel(List<CardModel> cards) {
    this.cards = cards;
    usedCards = new ArrayList<>();
  }

  /** Randomizes the cards in the deck (shuffle). */
  public void shuffle() {
    Collections.shuffle(cards);
  }

  /**
   * Creates a list of CardModel lists, simulating dealing a hand of cards.
   *
   * @param numPlayers The number of players in the hand.
   * @param numCardsPerHand The number of cards to give each player.
   * @return A list of CardModel lists for each player.
   */
  public List<List<CardModel>> deal(int numPlayers, int numCardsPerHand) {
    restoreAndShuffle();
    shuffle();
    final List<List<CardModel>> hands = new ArrayList<>();
    for (int i = 0; i < numPlayers; i++) {
      hands.add(new ArrayList<>());
    }
    for (int i = 0; i < numCardsPerHand; i++) {
      for (int j = 0; j < numPlayers; j++) {
        hands.get(j).add(draw());
      }
    }
    return hands;
  }

  /** Adds all used cards back to the deck. */
  public void restoreDeck() {
    cards.addAll(usedCards);
    usedCards = new ArrayList<>();
  }

  public void restoreAndShuffle() {
    restoreDeck();
    shuffle();
  }

  /**
   * Removes and returns one card from the deck.
   *
   * @return The card which was drawn.
   */
  public CardModel draw() {
    usedCards.add(cards.remove(cards.size() - 1));
    return usedCards.get(usedCards.size() - 1);
  }

  /** Removes a card from the deck (burns a card). */
  public void burn() {
    usedCards.add(cards.remove(cards.size() - 1));
  }

  public void freshDeck() {
    cards = new ArrayList<>(52);
    for (final CardSuit cardSuit : CardSuit.values()) {
      for (final CardValue cardValue : CardValue.values()) {
        cards.add(new CardModel(cardSuit, cardValue));
      }
    }
  }
}
