package com.poker.poker.events;

import com.poker.poker.models.game.CurrentGame;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event that is triggered when the game a player is currently in begins, ends or when the player
 * requests an update.
 */
@Getter
public class CurrentGameEvent extends ApplicationEvent {

  /**
   * User ID.
   */
  private final UUID userId;

  /**
   * Information about the game a player is currently in.
   */
  private final CurrentGame currentGame;

  public CurrentGameEvent(
      Object source, final UUID userId, final CurrentGame currentGame) {
    super(source);
    this.userId = userId;
    this.currentGame = currentGame;
  }
}
