package com.poker.poker.events;

import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.game.GameParameterModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateGameEvent extends ApplicationEvent {

  private final GameParameterModel gameParameterModel;
  private final UserDocument host;

  public CreateGameEvent(
      final Object source, final GameParameterModel gameParameterModel, final UserDocument host) {
    super(source);
    this.gameParameterModel = gameParameterModel;
    this.host = host;
  }
}
