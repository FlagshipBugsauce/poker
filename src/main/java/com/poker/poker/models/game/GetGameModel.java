package com.poker.poker.models.game;

import com.poker.poker.models.enums.GameState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetGameModel {
  // subset of fields from Game Document
  private String name;
  private int totalUsers;
  private GameState currentGameState;
}
