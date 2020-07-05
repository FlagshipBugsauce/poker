package com.poker.poker.models.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Contains the message to display and the duration of the toast.")
public class ToastModel {

  @Schema(description = "The message to display.", example = "Login Successful!")
  private String message;

  @Schema(implementation = ToastClassModel.class)
  private ToastClassModel options;
}
