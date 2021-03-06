package com.poker.poker.events;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class HideCardsEvent extends ApplicationEvent {

  /** ID of the game. */
  private final UUID id;

  public HideCardsEvent(final Object source, final UUID id) {
    super(source);
    this.id = id;
  }
}
