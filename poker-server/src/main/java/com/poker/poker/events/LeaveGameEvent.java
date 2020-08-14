package com.poker.poker.events;

import com.poker.poker.models.user.UserModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LeaveGameEvent extends ApplicationEvent {

  /**
   * User leaving the game.
   */
  private final UserModel user;

  public LeaveGameEvent(final Object source, final UserModel user) {
    super(source);
    this.user = user;
  }
}
