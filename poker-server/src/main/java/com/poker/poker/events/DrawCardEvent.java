package com.poker.poker.events;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DrawCardEvent extends ApplicationEvent {

  /** ID of the player who drew the card. */
  private final UUID id;

  public DrawCardEvent(final Object source, final UUID id) {
    super(source);
    this.id = id;
  }
}
