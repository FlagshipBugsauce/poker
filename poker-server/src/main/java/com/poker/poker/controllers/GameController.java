package com.poker.poker.controllers;

import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.events.AwayStatusEvent;
import com.poker.poker.events.CreateGameEvent;
import com.poker.poker.events.JoinGameEvent;
import com.poker.poker.events.LeaveLobbyEvent;
import com.poker.poker.events.ReadyEvent;
import com.poker.poker.events.StartGameEvent;
import com.poker.poker.models.ApiSuccess;
import com.poker.poker.models.game.ActiveStatus;
import com.poker.poker.models.game.GameParameter;
import com.poker.poker.models.user.User;
import com.poker.poker.repositories.UserRepository;
import com.poker.poker.services.JwtService;
import com.poker.poker.services.UserService;
import com.poker.poker.services.UuidService;
import com.poker.poker.services.game.GameDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/game")
@Tag(
    name = "game",
    description =
        "Games API handles all game requests, like creating a game, joining a game, etc...")
public class GameController {

  private final ApplicationEventPublisher publisher;
  private final UuidService uuidService;
  private final UserService userService;
  private final GameConstants gameConstants;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final GameDataService data;

  /**
   * Creates a game.
   *
   * @param jwt Authorization token.
   * @param gameParameter Model containing the information necessary to create a game.
   * @return An ApiSuccess containing the game's UUID in the message field if the request was
   *     successful, otherwise returns a 400 or 403.
   */
  @Operation(
      summary = "Create a new game",
      description = "Creates a new game and returns the UUID to the client.",
      tags = "game")
  @ApiResponses(
      @ApiResponse(
          responseCode = "200",
          description = "Creation of game was successful.",
          content =
              @Content(
                  schema = @Schema(implementation = ApiSuccess.class),
                  mediaType = MediaType.APPLICATION_JSON_VALUE)))
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccess> createGame(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
      @Valid @RequestBody GameParameter gameParameter) {
    final User host = jwtService.getUserDocument(jwt);
    userService.validate(jwt, gameConstants.getClientGroups());
    publisher.publishEvent(new CreateGameEvent(this, gameParameter, host));
    return ResponseEntity.ok(new ApiSuccess(data.getUsersGame(host.getId()).getId().toString()));
  }

  /**
   * Endpoint which allows players to join games.
   *
   * @param jwt Authorization token.
   * @param gameId The ID of the game the player wishes to join.
   * @return An ApiSuccess indicating the attempt to join was successful, otherwise returns a BAD
   *     REQUEST status.
   */
  @Operation(
      summary = "Joins a game",
      description = "Joins the game with the provided UUID, provided such a game exists.",
      tags = "game")
  @ApiResponses(
      @ApiResponse(
          responseCode = "200",
          description = "Game was joined successfully.",
          content =
              @Content(
                  schema = @Schema(implementation = ApiSuccess.class),
                  mediaType = MediaType.APPLICATION_JSON_VALUE)))
  @RequestMapping(value = "/join/{gameId}", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccess> joinGame(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
      @PathVariable String gameId) {
    userService.validate(jwt, gameConstants.getClientGroups());
    uuidService.checkIfValidAndThrowBadRequest(gameId);
    final User user = jwtService.getUserDocument(jwt);
    final UUID game = UUID.fromString(gameId);
    publisher.publishEvent(new JoinGameEvent(this, game, user));
    return ResponseEntity.ok(new ApiSuccess("Request Completed."));
  }

  @Operation(
      summary = "Ready to Start",
      description = "Request sent when a player is ready for the game to start.",
      tags = "game")
  @ApiResponses(
      @ApiResponse(
          responseCode = "200",
          description = "Request handled successfully.",
          content =
              @Content(
                  schema = @Schema(implementation = ApiSuccess.class),
                  mediaType = MediaType.APPLICATION_JSON_VALUE)))
  @RequestMapping(value = "/ready", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccess> ready(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt) {
    userService.validate(jwt, gameConstants.getClientGroups());
    publisher.publishEvent(
        new ReadyEvent(
            this, userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt)).getId()));
    return ResponseEntity.ok(new ApiSuccess("Ready status toggled."));
  }

  @Operation(
      summary = "Leave Game Lobby",
      description = "Request sent when a player leaves a game lobby.",
      tags = "game")
  @ApiResponses(
      @ApiResponse(
          responseCode = "200",
          description = "Request handled successfully.",
          content =
              @Content(
                  schema = @Schema(implementation = ApiSuccess.class),
                  mediaType = MediaType.APPLICATION_JSON_VALUE)))
  @RequestMapping(value = "/leave-lobby", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccess> leaveLobby(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt) {
    userService.validate(jwt, gameConstants.getClientGroups());
    publisher.publishEvent(
        new LeaveLobbyEvent(
            this, userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt))));
    return ResponseEntity.ok(new ApiSuccess("Player left lobby."));
  }

  /**
   * Starts a game.
   *
   * @param jwt JWT.
   * @return ApiSuccess with 200 status if the request is successful, throws otherwise.
   */
  @Operation(
      summary = "Start Game",
      description = "Starts the game, provided all preconditions are satisfied..",
      tags = "game")
  @ApiResponses(
      @ApiResponse(
          responseCode = "200",
          description = "Game started successfully.",
          content =
              @Content(
                  schema = @Schema(implementation = ApiSuccess.class),
                  mediaType = MediaType.APPLICATION_JSON_VALUE)))
  @RequestMapping(value = "/start", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccess> startGame(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt) {
    userService.validate(jwt, gameConstants.getClientGroups());
    final User user = userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt));
    publisher.publishEvent(new StartGameEvent(this, user.getId()));
    return ResponseEntity.ok(new ApiSuccess("The game has been started successfully."));
  }

  /**
   * Sets the status that indicates whether a player is active or not.
   *
   * @param jwt JWT.
   * @param activeStatus Model containing the active status.
   * @return ApiSuccess with 200 status if the request is successful, throws otherwise.
   */
  @Operation(
      summary = "Set Active Status",
      description = "Sets the status that indicates whether a player is active or not.",
      tags = "game")
  @ApiResponses(
      @ApiResponse(
          responseCode = "200",
          description = "Creation of game was successful.",
          content =
              @Content(
                  schema = @Schema(implementation = ApiSuccess.class),
                  mediaType = MediaType.APPLICATION_JSON_VALUE)))
  @RequestMapping(value = "/active", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccess> setActiveStatus(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
      @Valid @RequestBody ActiveStatus activeStatus) {
    userService.validate(jwt, gameConstants.getClientGroups());
    final UUID userId = userRepository.findUserDocumentById(jwtService.getUserId(jwt)).getId();
    publisher.publishEvent(new AwayStatusEvent(this, userId, activeStatus.isAway()));

    return ResponseEntity.ok(new ApiSuccess("PLayers status changed successfully."));
  }
}
