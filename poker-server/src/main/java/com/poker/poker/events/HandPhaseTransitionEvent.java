package com.poker.poker.events;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class HandPhaseTransitionEvent extends ApplicationEvent {

  /** Game ID. */
  private final UUID id;

  public HandPhaseTransitionEvent(final Object source, final UUID id) {
    super(source);
    this.id = id;
  }
}
