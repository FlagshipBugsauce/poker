package com.poker.poker.events;

import com.poker.poker.models.game.GameParameterModel;
import com.poker.poker.models.user.UserModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateGameEvent extends ApplicationEvent {

  /** Game parameters. */
  private final GameParameterModel gameParameterModel;

  /** Host of the game. */
  private final UserModel host;

  public CreateGameEvent(
      final Object source, final GameParameterModel gameParameterModel, final UserModel host) {
    super(source);
    this.gameParameterModel = gameParameterModel;
    this.host = host;
  }
}
