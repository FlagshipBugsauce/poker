package com.poker.poker.events;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class JoinGameEvent extends ApplicationEvent {

  private final UUID gameId;
  private final UUID userId;

  public JoinGameEvent(final Object source, final UUID gameId, final UUID userId) {
    super(source);
    this.gameId = gameId;
    this.userId = userId;
  }
}
