package com.poker.poker.events;

import com.poker.poker.models.enums.GameAction;
import com.poker.poker.models.game.GameActionModel;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class GameActionEvent extends ApplicationEvent {

  /**
   * Player ID.
   */
  private final UUID playerId;

  /**
   * Type of action.
   */
  private final GameAction type;

  /**
   * Raise amount (if applicable).
   */
  private final BigDecimal raise;

  public GameActionEvent(final Object source, final GameActionModel model) {
    super(source);
    playerId = model.getPlayerId();
    type = model.getActionType();
    raise = model.getRaise();
  }

  public GameActionEvent(
      final Object source,
      final UUID playerId,
      final GameAction type,
      final BigDecimal raise) {
    super(source);
    this.playerId = playerId;
    this.type = type;
    this.raise = raise;
  }
}
