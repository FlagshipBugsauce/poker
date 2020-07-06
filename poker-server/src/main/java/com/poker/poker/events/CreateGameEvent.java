package com.poker.poker.events;

import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.game.CreateGameModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateGameEvent extends ApplicationEvent {

  private final CreateGameModel createGameModel;
  private final UserDocument host;

  public CreateGameEvent(
      final Object source, final CreateGameModel createGameModel, final UserDocument host) {
    super(source);
    this.createGameModel = createGameModel;
    this.host = host;
  }
}
