package com.poker.poker.models.game;

import com.poker.poker.models.enums.GameAction;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameActionModel {

  /** Player ID. */
  private UUID playerId;

  /** Type of action. */
  private GameAction actionType;

  /** Raise amount (if applicable). */
  private BigDecimal raise;
}
