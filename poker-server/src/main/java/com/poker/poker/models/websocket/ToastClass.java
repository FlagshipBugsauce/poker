package com.poker.poker.models.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Model containing fields necessary to configure the appearance of a toast.")
public class ToastClass {

  @Schema(description = "Class name string used by HTML elements.", example = "toast-md")
  private String classname;

  @Schema(description = "The duration the toast will be displayed for, in ms.", example = "5000")
  private int delay;
}
