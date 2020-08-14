package com.poker.poker.events;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AwayStatusEvent extends ApplicationEvent {

  /** User ID. */
  private final UUID id;

  /** Away status */
  private final boolean away;

  public AwayStatusEvent(final Object source, final UUID id, final boolean away) {
    super(source);
    this.id = id;
    this.away = away;
  }
}
