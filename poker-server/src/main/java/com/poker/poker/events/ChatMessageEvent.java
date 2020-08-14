package com.poker.poker.events;

import com.poker.poker.models.user.UserModel;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ChatMessageEvent extends ApplicationEvent {

  /** User that sent the message. */
  private final UserModel user;

  /** Message that was sent. */
  private final String message;

  /** ID of the game where the chat is taking place. */
  private final UUID gameId;

  public ChatMessageEvent(
      final Object source, final UserModel user, final String message, final UUID gameId) {
    super(source);
    this.user = user;
    this.message = message;
    this.gameId = gameId;
  }
}
