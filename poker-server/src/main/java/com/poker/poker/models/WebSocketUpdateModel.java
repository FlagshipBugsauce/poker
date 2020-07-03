package com.poker.poker.models;

import com.poker.poker.models.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
    description =
        "Contains fields needed to determine what topic to broadcast to and what "
            + "data to broadcast.")
public class WebSocketUpdateModel {
  @Schema(description = "ID component of the topic.")
  private UUID id;

  @Schema(implementation = MessageType.class, description = "Type of message.")
  private MessageType type;

  @Schema(description = "Topic to broadcast to.", example = "/topic/game/xxx")
  private String topic;
}
