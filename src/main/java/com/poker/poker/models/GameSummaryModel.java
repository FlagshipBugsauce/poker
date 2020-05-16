package com.poker.poker.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Model representing summary of a game.")
public class GameSummaryModel {

  @Schema(description = "Placeholder message.", example = "This is a message.")
  private String message;
}
