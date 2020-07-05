package com.poker.poker.models;

import com.poker.poker.models.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocketContainerModel {

  private MessageType type;
  private Object data;
}
