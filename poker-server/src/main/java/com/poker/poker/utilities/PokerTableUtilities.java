package com.poker.poker.utilities;

import com.poker.poker.models.game.PokerTableModel;

public class PokerTableUtilities {

  /**
   * Creates a clone of the argument poker table which has new player objects with hidden cards.
   * When publishing a poker table update before cards are flipped, the table will be passed to this
   * method, which will ensure that players cannot see other players cards.
   *
   * @param table Poker table.
   * @return A clone of the table with hidden cards.
   */
  public static PokerTableModel hideCards(final PokerTableModel table) {
    return null;
  }

  /**
   * Updates table control models after a player performs an action, to ensure the UI is displaying
   * the correct information.
   *
   * @param table Poker table.
   */
  public static void updateTableControlModels(final PokerTableModel table) {

  }
}
