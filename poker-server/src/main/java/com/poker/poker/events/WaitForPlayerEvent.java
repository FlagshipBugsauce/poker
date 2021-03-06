package com.poker.poker.events;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class WaitForPlayerEvent extends ApplicationEvent {

  /** Player's ID. */
  private final UUID id;

  public WaitForPlayerEvent(final Object source, final UUID id) {
    super(source);
    this.id = id;
  }
}
