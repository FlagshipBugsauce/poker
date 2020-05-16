package com.poker.poker.controllers;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.AuthRequestModel;
import com.poker.poker.models.AuthResponseModel;
import com.poker.poker.models.user.NewAccountModel;
import com.poker.poker.models.user.UserModel;
import com.poker.poker.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(
    name = "users",
    description =
        "Users API handles all user account related requests, such as authentication, "
            + "registration, etc...")
public class UserController {
  private UserService userService;
  private AppConstants appConstants;

  @Operation(
      summary = "Authenticate",
      description =
          "The client must call this endpoint in order to obtain a JWT, which must be passed in "
              + "the header of most requests.",
      tags = "users")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Authorization was successful. A JWT should be returned, which can be used "
                    + "to access secured endpoints.",
            content =
                @Content(
                    schema = @Schema(implementation = AuthResponseModel.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @RequestMapping(value = "/auth", method = RequestMethod.POST)
  public ResponseEntity<AuthResponseModel> authorize(
      @RequestBody AuthRequestModel authRequestModel) {
    return ResponseEntity.ok(userService.authenticate(authRequestModel));
  }

  @Operation(summary = "Register", description = "Create an account.", tags = "register")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Account creation was successful.",
            content =
                @Content(
                    schema = @Schema(implementation = ApiSuccessModel.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccessModel> register(@RequestBody NewAccountModel newAccountModel) {
    return ResponseEntity.ok(userService.register(newAccountModel));
  }

  @Operation(
      summary = "Get User Info",
      description = "Retrieve user information for user with provided ID.",
      tags = "getUserInfo")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "User information retrieved successfully.",
              content =
              @Content(
                  schema = @Schema(implementation = UserModel.class),
                  mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @GetMapping("/getUserInfo/{userId}")
  public ResponseEntity<UserModel> getUserInfo(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
      @PathVariable String userId) {
    userService.validate(jwt, appConstants.getAllUsers());
    return ResponseEntity.ok(userService.getUserInfo(userId));
  }
}
