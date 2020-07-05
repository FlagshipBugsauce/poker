package com.poker.poker.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class EmitterModel {

  /**
   * The emitter.
   */
  private SseEmitter emitter;

  /**
   * Timestamp when the emitter was created.
   */
  private DateTime created;

  /**
   * The last data sent using the emitter.
   */
  private Object lastSent;

  /**
   * Timestamp when the emitter last sent data.
   */
  private DateTime lastSendTime;
}
