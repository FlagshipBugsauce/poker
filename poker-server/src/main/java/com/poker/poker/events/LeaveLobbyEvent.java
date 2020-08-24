package com.poker.poker.events;

import com.poker.poker.models.user.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LeaveLobbyEvent extends ApplicationEvent {

  /** User leaving the lobby. */
  private final User user;

  public LeaveLobbyEvent(final Object source, final User user) {
    super(source);
    this.user = user;
  }
}
