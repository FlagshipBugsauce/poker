package com.poker.poker.events;

import com.poker.poker.models.user.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LeaveGameEvent extends ApplicationEvent {

  /**
   * User leaving the game.
   */
  private final User user;

  public LeaveGameEvent(final Object source, final User user) {
    super(source);
    this.user = user;
  }
}
