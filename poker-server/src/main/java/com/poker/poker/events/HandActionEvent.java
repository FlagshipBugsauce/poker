package com.poker.poker.events;

import com.poker.poker.models.enums.HandAction;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class HandActionEvent extends ApplicationEvent {

  private UUID gameId;
  private UUID handId;
  private HandAction type;

  public HandActionEvent(Object source, UUID gameId, UUID handId, HandAction type) {
    super(source);
    this.gameId = gameId;
    this.handId = handId;
    this.type = type;
  }
}
