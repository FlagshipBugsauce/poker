package com.poker.poker.events;

import com.poker.poker.models.enums.MessageType;
import com.poker.poker.models.websocket.GenericServerMessage;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class GameMessageEvent<T> extends ApplicationEvent {

  /** Game ID. */
  private final UUID id;

  /** Wrapper for the data being sent. */
  private final GenericServerMessage<T> message;

  public GameMessageEvent(
      final Object source, final MessageType type, final UUID id, final T data) {
    super(source);
    message = new GenericServerMessage<>(type, data);
    this.id = id;
  }
}
