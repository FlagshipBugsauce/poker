package com.poker.poker.models.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Specifies the ID of a topic used to communicate securely with one user.")
public class PrivateTopic {

  /**
   * Secure topic ID.
   */
  @Schema(description = "Secure topic ID.", implementation = UUID.class)
  private UUID id;
}
