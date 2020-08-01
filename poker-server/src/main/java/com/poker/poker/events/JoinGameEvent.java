package com.poker.poker.events;

import com.poker.poker.models.user.UserModel;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class JoinGameEvent extends ApplicationEvent {

  private final UUID gameId;
  private final UserModel user;

  public JoinGameEvent(final Object source, final UUID gameId, final UserModel user) {
    super(source);
    this.gameId = gameId;
    this.user = user;
  }
}
