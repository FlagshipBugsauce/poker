package com.poker.poker.events;

import com.poker.poker.documents.UserDocument;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RejoinGameEvent extends ApplicationEvent {

  private final UserDocument user;

  public RejoinGameEvent(final Object source, final UserDocument user) {
    super(source);
    this.user = user;
  }
}
