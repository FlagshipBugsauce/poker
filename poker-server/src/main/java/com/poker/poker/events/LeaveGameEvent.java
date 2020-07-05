package com.poker.poker.events;

import com.poker.poker.documents.UserDocument;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LeaveGameEvent extends ApplicationEvent {

  final UserDocument user;

  public LeaveGameEvent(final Object source, final UserDocument user) {
    super(source);
    this.user = user;
  }
}
