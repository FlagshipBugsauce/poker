package com.poker.poker.models.websocket;

import com.poker.poker.models.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericServerMessage<T> {

  private MessageType type;
  private T data;
}
