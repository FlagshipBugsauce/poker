package com.poker.poker.controllers;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.models.ApiSuccess;
import com.poker.poker.models.user.AuthRequest;
import com.poker.poker.models.user.AuthResponse;
import com.poker.poker.models.user.ClientUser;
import com.poker.poker.models.user.JwtAuthRequest;
import com.poker.poker.models.user.NewAccount;
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

  private final UserService userService;
  private final AppConstants appConstants;

  /**
   * Authenticates using a users email and password, returns a token that can be used to identify
   * the client and access secure resources.
   *
   * @param authRequest Model containing a user's email and password.
   * @return AuthResponse containing data needed by the client.
   */
  @Operation(
      summary = "Authenticate",
      description =
          "The client must call this endpoint in order to obtain a JWT, which must be passed in "
              + "the header of most requests.",
      tags = "users")
  @ApiResponses(
      @ApiResponse(
          responseCode = "200",
          description =
              "Authorization was successful. A JWT should be returned, which can be used "
                  + "to access secured endpoints.",
          content =
              @Content(
                  schema = @Schema(implementation = AuthResponse.class),
                  mediaType = MediaType.APPLICATION_JSON_VALUE)))
  @RequestMapping(value = "/auth", method = RequestMethod.POST)
  public ResponseEntity<AuthResponse> authorize(@RequestBody AuthRequest authRequest) {
    return ResponseEntity.ok(userService.authenticate(authRequest));
  }

  /**
   * Authenticates using a JWT stored in a cookie.
   *
   * @param jwtAuthRequest JwtAuthRequest which stores a JWT.
   * @return AuthResponse containing data needed by the client after successful authentication.
   */
  @Operation(
      summary = "Authenticate With JWT",
      description =
          "If the client has a JWT stored in a cookie, it can call this endpoint to authenticate "
              + "using the JWT stored in the cookie.",
      tags = "users")
  @ApiResponses(
      @ApiResponse(
          responseCode = "200",
          description = "Authorization was successful.",
          content =
              @Content(
                  schema = @Schema(implementation = AuthResponse.class),
                  mediaType = MediaType.APPLICATION_JSON_VALUE)))
  @RequestMapping(value = "/auth-with-jwt", method = RequestMethod.POST)
  public ResponseEntity<AuthResponse> authorizeWithJwt(@RequestBody JwtAuthRequest jwtAuthRequest) {
    return ResponseEntity.ok(userService.authenticateWithJwt(jwtAuthRequest.getJwt()));
  }

  @Operation(summary = "Register", description = "Create an account.", tags = "register")
  @ApiResponses(
      @ApiResponse(
          responseCode = "200",
          description = "Account creation was successful.",
          content =
              @Content(
                  schema = @Schema(implementation = ApiSuccess.class),
                  mediaType = MediaType.APPLICATION_JSON_VALUE)))
  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccess> register(@RequestBody NewAccount newAccount) {
    return ResponseEntity.ok(userService.register(newAccount));
  }

  @Operation(
      summary = "Get User Info",
      description = "Retrieve user information for user with provided ID.",
      tags = "getUserInfo")
  @ApiResponses(
      @ApiResponse(
          responseCode = "200",
          description = "User information retrieved successfully.",
          content =
              @Content(
                  schema = @Schema(implementation = ClientUser.class),
                  mediaType = MediaType.APPLICATION_JSON_VALUE)))
  @GetMapping("/getUserInfo/{userId}")
  public ResponseEntity<ClientUser> getUserInfo(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
      @PathVariable String userId) {
    userService.validate(jwt, appConstants.getAllUsers());
    return ResponseEntity.ok(userService.getUserInfo(userId));
  }
}
