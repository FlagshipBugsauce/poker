package com.poker.poker.events;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SystemChatMessageEvent extends ApplicationEvent {

  /** Game ID. */
  private final UUID gameId;

  /** Chat message. */
  private final String message;

  public SystemChatMessageEvent(final Object source, final UUID gameId, final String message) {
    super(source);
    this.gameId = gameId;
    this.message = message;
  }
}
