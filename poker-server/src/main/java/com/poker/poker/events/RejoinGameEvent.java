package com.poker.poker.events;

import com.poker.poker.models.user.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RejoinGameEvent extends ApplicationEvent {

  /** User who is rejoining a game. */
  private final User user;

  public RejoinGameEvent(final Object source, final User user) {
    super(source);
    this.user = user;
  }
}
