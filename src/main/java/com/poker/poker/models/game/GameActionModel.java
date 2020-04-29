package com.poker.poker.models.game;

import com.poker.poker.models.enums.GameAction;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameActionModel {
  private UUID userID;
  private GameAction gameAction;
}
