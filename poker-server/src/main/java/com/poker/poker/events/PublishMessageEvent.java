package com.poker.poker.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PublishMessageEvent<T> extends ApplicationEvent {

  /**
   * Topic the data is being published to.
   */
  private final String topic;

  /**
   * Data being published.
   */
  private final T data;

  public PublishMessageEvent(final Object source, final String topic, final T data) {
    super(source);
    this.topic = topic;
    this.data = data;
  }
}
