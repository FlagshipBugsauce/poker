package com.poker.poker.controllers;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.enums.EmitterType;
import com.poker.poker.services.JwtService;
import com.poker.poker.services.SseService;
import com.poker.poker.services.UserService;
import com.poker.poker.services.game.GameService;
import com.poker.poker.services.game.HandService;
import com.poker.poker.services.game.LobbyService;
import com.poker.poker.validation.exceptions.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/emitters")
@Tag(name = "emitters", description = "Handles all requests related to SSE emitters")
public class SseController {

  private final AppConstants appConstants;
  private final SseService sseService;
  private final JwtService jwtService;
  private final LobbyService lobbyService;
  private final GameService gameService;
  private final HandService handService;
  private final UserService userService;

  @Operation(
      summary = "Request SSE Emitter",
      description = "Request an SSE emitter of the specified type.",
      tags = "request")
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
  @RequestMapping(value = "/request/{type}/{jwt}", method = RequestMethod.GET)
  public SseEmitter requestEmitter(@PathVariable String jwt, @PathVariable EmitterType type) {
    userService.validate(jwt, appConstants.getClientGroups());
    log.debug("User {} requested {} emitter.", jwtService.getUserId(jwt), type);
    final UUID userId = jwtService.getUserId(jwt);
    final Runnable validator;
    switch (type) { // TODO: Add validators for the other emitters
      case GameList:
      case Game:
      case Hand:
      case GameData:
        validator = null;
        break;
      case Lobby:
        validator = lobbyService.getEmitterValidator(userId);
        break;
      default:
        throw new BadRequestException("", "");
    }
    return sseService.createEmitter(type, jwtService.getUserId(jwt), validator);
  }

  @Operation(
      summary = "Request Update",
      description = "Requests an update from the SSE emitter specified.",
      tags = "update")
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
  @RequestMapping(value = "/update/{type}", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccessModel> requestUpdate(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
      @PathVariable EmitterType type) {
    userService.validate(jwt, appConstants.getClientGroups());
    final UUID userId = jwtService.getUserId(jwt);
    Object data = null;
    switch (type) {
      case GameList:
        data = lobbyService.getLobbyList();
        break;
      case Lobby:
        data = lobbyService.getUsersLobbyDocument(userId);
        break;
      case Game:
        data = gameService.getUsersGameDocument(userId);
        break;
      case Hand:
        data = handService.getHandForUserId(userId);
        break;
      case GameData:
        data = gameService.getGameData(gameService.getUsersGameDocument(userId));
    }
    return ResponseEntity.ok(sseService.sendUpdate(type, userId, data));
  }

  @Operation(
      summary = "Destroy Emitter",
      description = "Destroy the emitter that is sending updated game lists.",
      tags = "destroy")
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
  @RequestMapping(value = "/destroy/{type}", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccessModel> destroyEmitter(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
      @PathVariable EmitterType type) {
    userService.validate(jwt, appConstants.getClientGroups());
    return ResponseEntity.ok(sseService.completeEmitter(type, jwtService.getUserId(jwt)));
  }
}
