package com.poker.poker.models.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Message sent to chat.")
public class ChatMessage {

  /**
   * Time the message was sent.
   */
  @Schema(description = "Time the message was sent.", implementation = Date.class)
  private Date timestamp;

  /**
   * Author of the message. If the message was sent by the system, author will be null.
   */
  @Schema(
      description = "Author of the message. Null if the message was sent by the system.",
      example = "Jackson McGee")
  private String author;

  /**
   * Message.
   */
  @Schema(description = "Message", example = "Jackson McGee drew the Ace of Spaces.")
  private String message;
}
