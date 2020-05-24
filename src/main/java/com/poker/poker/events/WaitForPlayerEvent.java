package com.poker.poker.events;

import com.poker.poker.models.game.GamePlayerModel;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class WaitForPlayerEvent extends ApplicationEvent {

  private GamePlayerModel player;

  public WaitForPlayerEvent(Object source, GamePlayerModel player) {
    super(source);
    this.player = player;
  }
}
