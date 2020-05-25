package com.poker.poker.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class ApiErrorModel implements Serializable {
  @Schema(description = "The type of the error.", example = "Bad Request")
  private final String errorType;

  @Schema(
      description = "A description of the error.",
      example = "The credentials provided are invalid.")
  private final String description;

  @Schema(description = "The time the error occurred.", example = "2020-04-25T21:14:49.700+0000")
  private final Date timestamp;
}
