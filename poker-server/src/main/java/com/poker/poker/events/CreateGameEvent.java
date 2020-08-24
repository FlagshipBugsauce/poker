package com.poker.poker.events;

import com.poker.poker.models.game.GameParameter;
import com.poker.poker.models.user.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateGameEvent extends ApplicationEvent {

  /** Game parameters. */
  private final GameParameter gameParameter;

  /** Host of the game. */
  private final User host;

  public CreateGameEvent(final Object source, final GameParameter gameParameter, final User host) {
    super(source);
    this.gameParameter = gameParameter;
    this.host = host;
  }
}
