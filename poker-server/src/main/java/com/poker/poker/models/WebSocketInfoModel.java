package com.poker.poker.models;

import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketInfoModel {

  private UUID secureTopicId;
  private Date lastActivity;
}
