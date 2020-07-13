package com.poker.poker.events;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SystemChatMessageEvent extends ApplicationEvent {

  private final UUID gameId;
  private final String message;

  public SystemChatMessageEvent(final Object source, final UUID gameId, final String message) {
    super(source);
    this.gameId = gameId;
    this.message = message;
  }
}
