package com.poker.poker.events;

import com.poker.poker.models.user.UserModel;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ChatMessageEvent extends ApplicationEvent {

  private final UserModel user;
  private final String message;
  private final UUID gameId;

  public ChatMessageEvent(
      final Object source, final UserModel user, final String message, final UUID gameId) {
    super(source);
    this.user = user;
    this.message = message;
    this.gameId = gameId;
  }
}
