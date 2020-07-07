package com.poker.poker.events;

import com.poker.poker.documents.UserDocument;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class JoinGameEvent extends ApplicationEvent {

  private final UUID gameId;
  private final UserDocument user;

  public JoinGameEvent(final Object source, final UUID gameId, final UserDocument user) {
    super(source);
    this.gameId = gameId;
    this.user = user;
  }
}
