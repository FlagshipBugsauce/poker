package com.poker.poker.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PublishMessageEvent <T> extends ApplicationEvent {

  private final String topic;
  private final T data;

  public PublishMessageEvent(final Object source, final String topic, final T data) {
    super(source);
    this.topic = topic;
    this.data = data;
  }
}
