package com.poker.poker.events;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReadyEvent extends ApplicationEvent {

  private final UUID id;

  public ReadyEvent(final Object source, final UUID id) {
    super(source);
    this.id = id;
  }
}
