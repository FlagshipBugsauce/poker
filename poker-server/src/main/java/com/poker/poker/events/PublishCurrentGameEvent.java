package com.poker.poker.events;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PublishCurrentGameEvent extends ApplicationEvent {

  /**
   * User's ID.
   */
  private final UUID id;

  public PublishCurrentGameEvent(final Object source, final UUID id) {
    super(source);
    this.id = id;
  }
}
