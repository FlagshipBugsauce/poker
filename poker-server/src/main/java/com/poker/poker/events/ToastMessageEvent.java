package com.poker.poker.events;

import com.poker.poker.models.websocket.ToastClassModel;
import com.poker.poker.models.websocket.ToastModel;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ToastMessageEvent extends ApplicationEvent {

  /**
   * Game ID.
   */
  private final UUID id;

  /**
   * Toast.
   */
  private final ToastModel toast;

  public ToastMessageEvent(
      final Object source, final UUID id, final String message, final String size) {
    super(source);
    this.id = id;
    toast = new ToastModel(message, new ToastClassModel("bg-light toast-" + size, 5000));
  }
}
