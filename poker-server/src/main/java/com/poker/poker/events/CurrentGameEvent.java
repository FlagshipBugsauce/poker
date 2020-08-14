package com.poker.poker.events;

import com.poker.poker.models.game.CurrentGameModel;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event that is triggered when the game a player is currently in begins, ends or when the player
 * requests an update.
 */
@Getter
public class CurrentGameEvent extends ApplicationEvent {

  /** User ID. */
  private final UUID userId;

  /** Information about the game a player is currently in. */
  private final CurrentGameModel currentGameModel;

  public CurrentGameEvent(
      Object source, final UUID userId, final CurrentGameModel currentGameModel) {
    super(source);
    this.userId = userId;
    this.currentGameModel = currentGameModel;
  }
}
