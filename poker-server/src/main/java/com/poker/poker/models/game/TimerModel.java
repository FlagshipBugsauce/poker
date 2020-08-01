package com.poker.poker.models.game;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimerModel {

  /** ID of the timer. */
  @Schema(description = "ID of the timer.", implementation = UUID.class)
  private UUID id;

  /** Duration of the timer. */
  @Schema(description = "Duration of the timer", example = "7")
  private BigDecimal duration;
}
