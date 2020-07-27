package com.poker.poker.events;

import com.poker.poker.models.enums.HandAction;
import com.poker.poker.models.game.CardModel;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class HandActionEvent extends ApplicationEvent {

  private final UUID gameId;
  private final UUID handId;
  private final HandAction type;
  private final CardModel cardDrawn;

  public HandActionEvent(
      final Object source,
      final UUID gameId,
      final UUID handId,
      final HandAction type,
      final CardModel cardDrawn) {
    super(source);
    this.gameId = gameId;
    this.handId = handId;
    this.type = type;
    this.cardDrawn = cardDrawn;
  }
}
