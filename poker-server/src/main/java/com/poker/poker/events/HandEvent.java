package com.poker.poker.events;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class HandEvent extends ApplicationEvent {

  private final UUID id;

  public HandEvent(final Object source, final UUID id) {
    super(source);
    this.id = id;
  }
}
