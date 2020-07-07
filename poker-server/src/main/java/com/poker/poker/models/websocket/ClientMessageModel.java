package com.poker.poker.models.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Generic model used to send data to the server.")
public class ClientMessageModel<T> {

  @Schema(
      description = "Optional field when user's identity needs to be verified in a secure fashion.",
      example =
          "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBkb21haW4uY29tIiwiZXhwIjoxNTg4OTAzODg5LCJpYXQiO"
              + "jE1ODc2OTQyODl9.u9Uo7Al_a6vu_Rydt1zkhOYWFsIDPR5BgMxJjWRty9w")
  private String jwt;

  @Schema(
      description = "Optional field to identify a user when security is not important.",
      implementation = UUID.class)
  private UUID userId;

  @Schema(
      description = "Optional field to identify the game a user is in.",
      implementation = UUID.class)
  private UUID gameId;

  @Schema(description = "Data being sent by the client.")
  private T data;
}
