package com.poker.poker.events;

import com.poker.poker.models.user.UserModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LeaveLobbyEvent extends ApplicationEvent {

  /**
   * User leaving the lobby.
   */
  private final UserModel user;

  public LeaveLobbyEvent(final Object source, final UserModel user) {
    super(source);
    this.user = user;
  }
}
