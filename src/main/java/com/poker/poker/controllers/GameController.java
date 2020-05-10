package com.poker.poker.controllers;

import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.enums.EmitterType;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.models.game.GetGameModel;
import com.poker.poker.repositories.UserRepository;
import com.poker.poker.services.GameService;
import com.poker.poker.services.JwtService;
import com.poker.poker.services.SseService;
import com.poker.poker.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/game")
@Tag(
    name = "game",
    description =
        "Games API handles all game requests, like creating a game, joining a game, etc...")
public class GameController {

  private GameService gameService;
  private SseService sseService;
  private UserService userService;
  private GameConstants constants;
  private JwtService jwtService;
  private UserRepository userRepository;

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
      @RequestHeader("Authorization") String jwt,
      @Valid @RequestBody CreateGameModel createGameModel) {
    userService.validate(jwt, constants.getClientGroups());
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
  @RequestMapping(value = "/getAll", method = RequestMethod.GET)
  public ResponseEntity<List<GetGameModel>> getGameList(
      @RequestHeader("Authorization") String jwt) {
    userService.validate(jwt, constants.getClientGroups());
    return ResponseEntity.ok(gameService.getGameList());
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
      @RequestHeader("Authorization") String jwt, @PathVariable String gameId) {
    userService.validate(jwt, constants.getClientGroups());
    return ResponseEntity.ok(
        gameService.joinGame(
            gameId, userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt))));
  }

  @Operation(
      summary = "Request SSE Emitter",
      description = "Request an SSE emitter to be sent updates regarding the game state.",
      tags = "game")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Emitter was created successfully.",
            content =
                @Content(
                    schema = @Schema(implementation = SseEmitter.class),
                    mediaType = MediaType.TEXT_EVENT_STREAM_VALUE))
      })
  @RequestMapping(value = "/emitter/game/{jwt}", method = RequestMethod.GET)
  public SseEmitter getGameEmitter(@PathVariable String jwt) {
    // TODO: Add faux-security here, i.e. validate the JWT manually since security is disabled.
    UUID userId = userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt)).getId();

    return sseService.createEmitter(
        EmitterType.Lobby,
        userId,
        () -> {
          log.debug("Performing validation to ensure {} should receive an emitter.", userId);
          gameService.checkWhetherUserIsInGameAndThrow(userId, true);
          gameService.checkUserIsPlayerInGame(userId);
        });
  }

  @Operation(
      summary = "Request Game Document Update",
      description = "Request an updated game document.",
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
  @RequestMapping(value = "/refresh-game-doc", method = RequestMethod.GET)
  public ResponseEntity<ApiSuccessModel> getGameDocumentUpdate(
      @RequestHeader("Authorization") String jwt) {
    return ResponseEntity.ok(
        gameService.getGameDocumentUpdate(
            userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt)).getId()));
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
  public ResponseEntity<ApiSuccessModel> ready(@RequestHeader("Authorization") String jwt) {
    return ResponseEntity.ok(
        gameService.ready(userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt))));
  }

  @Operation(
      summary = "Request SSE Emitter",
      description = "Request an SSE emitter to be sent updates to the list of games.",
      tags = "game")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Emitter was created successfully.",
            content =
                @Content(
                    schema = @Schema(implementation = SseEmitter.class),
                    mediaType = MediaType.TEXT_EVENT_STREAM_VALUE))
      })
  @RequestMapping(value = "/emitter/join/{jwt}", method = RequestMethod.GET)
  public SseEmitter getJoinGameEmitter(@PathVariable String jwt) {
    // TODO: Add faux-security here, i.e. validate the JWT manually since security is disabled.
    return sseService.createEmitter(
        EmitterType.GameList,
        userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt)).getId(),
        () -> {});
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
  public ResponseEntity<ApiSuccessModel> leaveLobby(@RequestHeader("Authorization") String jwt) {
    return ResponseEntity.ok(
        gameService.removePlayerFromGame(
            userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt))));
  }

  @Operation(
      summary = "Destroy Join Game Emitter",
      description = "Destroy the emitter that is sending updated game lists.",
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
  @RequestMapping(value = "/destroy-join-game-emitter", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccessModel> destroyJoinGameEmitter(
      @RequestHeader("Authorization") String jwt) {
    return ResponseEntity.ok(
        sseService.completeEmitter(
            EmitterType.GameList,
            userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt)).getId()));
  }

  @Operation(
      summary = "Refresh Game List",
      description = "Requests updated list of games.",
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
  @RequestMapping(value = "/refresh-game-list", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccessModel> refreshGameList(
      @RequestHeader("Authorization") String jwt) {
    return ResponseEntity.ok(
        sseService.sendUpdate(
            EmitterType.GameList,
            userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt)).getId(),
            gameService.getGameList()));
  }
}
