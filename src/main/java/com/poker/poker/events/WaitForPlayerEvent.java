package com.poker.poker.events;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class WaitForPlayerEvent extends ApplicationEvent {

  private UUID userId;

  public WaitForPlayerEvent(Object source, UUID userId) {
    super(source);
    this.userId = userId;
  }
}
