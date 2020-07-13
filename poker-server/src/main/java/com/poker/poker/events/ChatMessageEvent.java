package com.poker.poker.events;

import com.poker.poker.documents.UserDocument;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ChatMessageEvent extends ApplicationEvent {

  private final UserDocument user;
  private final String message;
  private final UUID gameId;

  public ChatMessageEvent(
      final Object source, final UserDocument user, final String message, final UUID gameId) {
    super(source);
    this.user = user;
    this.message = message;
    this.gameId = gameId;
  }
}
