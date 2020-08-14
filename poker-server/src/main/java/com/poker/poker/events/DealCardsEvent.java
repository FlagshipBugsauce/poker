package com.poker.poker.events;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DealCardsEvent extends ApplicationEvent {

  /** ID of the game. */
  private final UUID id;

  public DealCardsEvent(final Object source, final UUID id) {
    super(source);
    this.id = id;
  }
}
