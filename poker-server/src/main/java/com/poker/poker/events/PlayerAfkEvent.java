package com.poker.poker.events;

import com.poker.poker.models.game.GamePlayerModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PlayerAfkEvent extends ApplicationEvent {

  private final GamePlayerModel player;

  public PlayerAfkEvent(Object source, final GamePlayerModel player) {
    super(source);
    this.player = player;
  }
}
