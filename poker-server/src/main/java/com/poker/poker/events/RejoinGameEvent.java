package com.poker.poker.events;

import com.poker.poker.models.user.UserModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RejoinGameEvent extends ApplicationEvent {

  /**
   * User who is rejoining a game.
   */
  private final UserModel user;

  public RejoinGameEvent(final Object source, final UserModel user) {
    super(source);
    this.user = user;
  }
}
