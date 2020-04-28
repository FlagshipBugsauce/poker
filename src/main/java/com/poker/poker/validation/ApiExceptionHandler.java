package com.poker.poker.validation;

import com.poker.poker.controllers.TestController;
import com.poker.poker.controllers.UserController;
import com.poker.poker.models.ApiErrorModel;
import com.poker.poker.models.AuthResponseModel;
import com.poker.poker.validation.exceptions.BadRequestException;
import com.poker.poker.validation.exceptions.ForbiddenException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Handles exceptions thrown when a controller is called and will return the appropriate HTTP
 * response.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(assignableTypes = { UserController.class, TestController.class })
public class ApiExceptionHandler {

  /**
   * Handles exceptions caused by bad requests.
   *
   * @param e Exception that was thrown.
   * @return Response with bad request status and any relevant information.
   */
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "400",
              description = "Bad request.",
              content =
              @Content(
                  schema = @Schema(implementation = ApiErrorModel.class),
                  mediaType = "application/json"))
      })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(BadRequestException.class)
  protected ResponseEntity<ApiErrorModel> handleBadRequestExceptions(final BadRequestException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getApiErrorModel());
  }

  /**
   * Handles exceptions caused by forbidden requests.
   *
   * @param e Exception that was thrown.
   * @return Response with forbidden status and any relevant information.
   */
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "403",
              description = "Forbidden.",
              content =
              @Content(
                  schema = @Schema(implementation = ApiErrorModel.class),
                  mediaType = "application/json"))
      })
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(ForbiddenException.class)
  protected ResponseEntity<ApiErrorModel> handleForbiddenExceptions(final ForbiddenException e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getApiErrorModel());
  }
}
