package com.poker.poker.validation.exceptions;

import com.poker.poker.models.ApiError;
import java.util.Date;
import lombok.Getter;

public class BadRequestException extends RuntimeException {

  @Getter
  private final ApiError apiError;

  /**
   * Creates an BadRequestException with an error object.
   *
   * @param errorType   The type of the error.
   * @param description Description of the error;
   */
  public BadRequestException(final String errorType, final String description) {
    apiError = new ApiError(errorType, description, new Date());
  }
}
