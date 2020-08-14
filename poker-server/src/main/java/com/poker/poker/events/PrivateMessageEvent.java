package com.poker.poker.events;

import com.poker.poker.models.enums.MessageType;
import com.poker.poker.models.websocket.GenericServerMessage;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PrivateMessageEvent<T> extends ApplicationEvent {

  /**
   * User ID.
   */
  private final UUID id;

  /**
   * Wrapper for the data being sent.
   */
  private final GenericServerMessage<T> message;

  public PrivateMessageEvent(
      final Object source, final MessageType type, final UUID id, final T data) {
    super(source);
    this.id = id;
    message = new GenericServerMessage<>(type, data);
  }
}
