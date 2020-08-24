package com.poker.poker.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiSuccess {

  @Schema(
      description = "Returned to indicate API call was successful",
      example = "Account created successfully.")
  private String message;
}
