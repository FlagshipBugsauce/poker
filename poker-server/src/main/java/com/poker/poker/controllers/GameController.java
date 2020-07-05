package com.poker.poker.controllers;

import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.game.ActiveStatusModel;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.models.game.GetGameModel;
import com.poker.poker.repositories.UserRepository;
import com.poker.poker.services.JwtService;
import com.poker.poker.services.UserService;
import com.poker.poker.services.game.GameService;
import com.poker.poker.services.game.LobbyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  private final GameService gameService;
  private final LobbyService lobbyService;
  private final UserService userService;
  private final GameConstants gameConstants;
  private final JwtService jwtService;
  private final UserRepository userRepository;

  /**
   * Creates a game.
   *
   * @param jwt Authorization token.
   * @param createGameModel Model containing the information necessary to create a game.
   * @return An ApiSuccessModel containing the game's UUID in the message field if the request was
   *     successful, otherwise returns a 400 or 403.
   */
  @Operation(
      summary = "Create a new game",
      description = "Creates a new game and returns the UUID to the client.",
      tags = "game")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Creation of game was successful.",
            content =
                @Content(
                    schema = @Schema(implementation = ApiSuccessModel.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccessModel> createGame(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
      @Valid @RequestBody CreateGameModel createGameModel) {
    userService.validate(jwt, gameConstants.getClientGroups());
    return ResponseEntity.ok(
        gameService.createGame(
            createGameModel, userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt))));
  }

  /**
   * Retrieves a list of games which are not full and have not yet started.
   *
   * @param jwt Authorization token.
   * @return A list of games which are not full and have not yet started, provided the request is
   *     successful. Otherwise, will return a 400 or 403.
   */
  @Operation(
      summary = "Get game list",
      description = "Retrieves a list of games which are not full and have not yet started.",
      tags = "game")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Getting game active list was successful. A GetGameModel should be returned",
            content =
                @Content(
                    array = @ArraySchema(schema = @Schema(implementation = GetGameModel.class)),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @RequestMapping(value = "/get-list", method = RequestMethod.GET)
  public ResponseEntity<List<GetGameModel>> getGameList(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt) {
    userService.validate(jwt, gameConstants.getClientGroups());
    return ResponseEntity.ok(lobbyService.getLobbyList());
  }

  /**
   * Endpoint which allows players to join games.
   *
   * @param jwt Authorization token.
   * @param gameId The ID of the game the player wishes to join.
   * @return An ApiSuccessModel indicating the attempt to join was successful, otherwise returns a
   *     BAD REQUEST status.
   */
  @Operation(
      summary = "Joins a game",
      description = "Joins the game with the provided UUID, provided such a game exists.",
      tags = "game")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Game was joined successfully.",
            content =
                @Content(
                    schema = @Schema(implementation = ApiSuccessModel.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @RequestMapping(value = "/join/{gameId}", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccessModel> joinGame(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
      @PathVariable String gameId) {
    userService.validate(jwt, gameConstants.getClientGroups());
    return ResponseEntity.ok(
        gameService.joinLobby(
            gameId, userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt))));
  }

  @Operation(
      summary = "Ready to Start",
      description = "Request sent when a player is ready for the game to start.",
      tags = "game")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Request handled successfully.",
            content =
                @Content(
                    schema = @Schema(implementation = ApiSuccessModel.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @RequestMapping(value = "/ready", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccessModel> ready(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt) {
    userService.validate(jwt, gameConstants.getClientGroups());
    return ResponseEntity.ok(
        lobbyService.ready(userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt))));
  }

  @Operation(
      summary = "Leave Game Lobby",
      description = "Request sent when a player leaves a game lobby.",
      tags = "game")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Request handled successfully.",
            content =
                @Content(
                    schema = @Schema(implementation = ApiSuccessModel.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @RequestMapping(value = "/leave-lobby", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccessModel> leaveLobby(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt) {
    userService.validate(jwt, gameConstants.getClientGroups());
    return ResponseEntity.ok(
        gameService.removePlayerFromLobby(
            userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt))));
  }

  /**
   * Starts a game.
   *
   * @param jwt JWT.
   * @return ApiSuccessModel with 200 status if the request is successful, throws otherwise.
   */
  @Operation(
      summary = "Start Game",
      description = "Starts the game, provided all preconditions are satisfied..",
      tags = "game")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Game started successfully.",
            content =
                @Content(
                    schema = @Schema(implementation = ApiSuccessModel.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @RequestMapping(value = "/start", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccessModel> startGame(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt) {
    userService.validate(jwt, gameConstants.getClientGroups());
    return ResponseEntity.ok(
        gameService.startGame(
            userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt))));
  }

  /**
   * Sets the status that indicates whether a player is active or not.
   *
   * @param jwt JWT.
   * @param activeStatusModel Model containing the active status.
   * @return ApiSuccessModel with 200 status if the request is successful, throws otherwise.
   */
  @Operation(
      summary = "Set Active Status",
      description = "Sets the status that indicates whether a player is active or not.",
      tags = "game")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Creation of game was successful.",
            content =
                @Content(
                    schema = @Schema(implementation = ApiSuccessModel.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @RequestMapping(value = "/active", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccessModel> setActiveStatus(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
      @Valid @RequestBody ActiveStatusModel activeStatusModel) {
    userService.validate(jwt, gameConstants.getClientGroups());
    return ResponseEntity.ok(
        gameService.setPlayerActiveStatus(
            userRepository.findUserDocumentById(jwtService.getUserId(jwt)).getId(),
            activeStatusModel.isAway()));
  }
}
