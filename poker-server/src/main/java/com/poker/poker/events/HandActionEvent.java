package com.poker.poker.events;

import com.poker.poker.models.enums.HandAction;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class HandActionEvent extends ApplicationEvent {

  private final UUID gameId;
  private final UUID handId;
  private final HandAction type;

  public HandActionEvent(
      Object source, final UUID gameId, final UUID handId, final HandAction type) {
    super(source);
    this.gameId = gameId;
    this.handId = handId;
    this.type = type;
  }
}
