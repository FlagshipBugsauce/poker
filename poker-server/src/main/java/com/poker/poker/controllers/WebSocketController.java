package com.poker.poker.controllers;

import com.poker.poker.config.AppConfig;
import com.poker.poker.events.ChatMessageEvent;
import com.poker.poker.events.CreateGameEvent;
import com.poker.poker.events.GameActionEvent;
import com.poker.poker.events.JoinGameEvent;
import com.poker.poker.events.LeaveGameEvent;
import com.poker.poker.events.PublishCurrentGameEvent;
import com.poker.poker.events.RejoinGameEvent;
import com.poker.poker.models.game.GameActionData;
import com.poker.poker.models.game.GameParameter;
import com.poker.poker.models.game.PokerTable;
import com.poker.poker.models.user.User;
import com.poker.poker.models.websocket.ClientMessage;
import com.poker.poker.models.websocket.GenericServerMessage;
import com.poker.poker.models.websocket.PrivateTopic;
import com.poker.poker.models.websocket.WebSocketUpdate;
import com.poker.poker.services.JwtService;
import com.poker.poker.services.UserService;
import com.poker.poker.services.WebSocketService;
import com.poker.poker.services.game.GameDataService;
import com.poker.poker.utilities.PokerTableUtilities;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Controller
@AllArgsConstructor
@RestController // TODO: Need to investigate whether having this messes up anything socket-related
@Tag(name = "websocket", description = "WebSocket controller.")
public class WebSocketController {

  private final AppConfig appConfig;
  private final UserService userService;
  private final JwtService jwtService;
  private final ApplicationEventPublisher publisher;
  private final WebSocketService webSocketService;

  private final GameDataService data;

  @MessageMapping("/game/update")
  public void getUpdate(final WebSocketUpdate updateModel) {
    log.debug(
        "Sending update of type: {} to topic: {}.", updateModel.getType(), updateModel.getTopic());
    try {
      final Object data;
      switch (updateModel.getType()) {
        case GameList:
          data = this.data.getLobbyList();
          break;
        case Lobby:
          data = this.data.getLobby(updateModel.getId());
          break;
        case Game:
          data = this.data.getGame(updateModel.getId());
          break;
        case GameData:
          data = this.data.getGameSummary(updateModel.getId());
          break;
        case PlayerData:
          data = this.data.getPlayer(updateModel.getId());
          break;
        case PokerTable:
          PokerTable table = this.data.getPokerTable(updateModel.getId());
          table = table.isBetting() ? PokerTableUtilities.hideCards(table) : table;
          data = table;
          break;
        default:
          data = null;
      }
      webSocketService.sendPublicMessage(
          updateModel.getTopic(), new GenericServerMessage<>(updateModel.getType(), data));
    } catch (Exception e) {
      log.error("Error retrieving data on {} update request.", updateModel.getType());
    }
  }

  @Operation(
      summary = "Request a private topic.",
      description =
          "Creates a private topic so that the backend can communicate securely to one client.",
      tags = "websocket")
  @ApiResponses(
      @ApiResponse(
          responseCode = "200",
          description = "Private topic successfully created.",
          content =
          @Content(
              schema = @Schema(implementation = PrivateTopic.class),
              mediaType = MediaType.APPLICATION_JSON_VALUE)))
  @GetMapping("/private-topic")
  public ResponseEntity<PrivateTopic> getPrivateTopic(
      @Parameter(hidden = true) @RequestHeader("Authorization") final String jwt) {
    userService.validate(jwt, appConfig.getGeneralGroups());
    final User user = jwtService.getUserDocument(jwt);
    return ResponseEntity.ok(webSocketService.requestPrivateTopic(user));
  }

  @MessageMapping("/chat/send")
  public void chatMessage(final ClientMessage<String> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    publisher.publishEvent(
        new ChatMessageEvent(
            this,
            jwtService.getUserDocument(messageModel.getJwt()),
            messageModel.getData(),
            messageModel.getGameId()));
  }

  @MessageMapping("/game/leave")
  public void leaveGame(final ClientMessage<Void> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    publisher.publishEvent(
        new LeaveGameEvent(this, jwtService.getUserDocument(messageModel.getJwt())));
  }

  @MessageMapping("/game/rejoin")
  public void rejoinGame(final ClientMessage<Void> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    publisher.publishEvent(
        new RejoinGameEvent(this, jwtService.getUserDocument(messageModel.getJwt())));
  }

  @MessageMapping("/game/current/update")
  public void requestCurrentGameUpdate(final ClientMessage<Void> messageModel) {
    log.debug("User {} requesting update.", messageModel.getUserId());
    publisher.publishEvent(new PublishCurrentGameEvent(this, messageModel.getUserId()));
  }

  @MessageMapping("/game/create")
  public void createGame(final ClientMessage<GameParameter> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    final User user = jwtService.getUserDocument(messageModel.getJwt());
    log.debug("User {} attempting to create a game.", user.getId());
    publisher.publishEvent(new CreateGameEvent(this, messageModel.getData(), user));
  }

  @MessageMapping("/game/join")
  public void joinGame(final ClientMessage<Void> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    final User user = jwtService.getUserDocument(messageModel.getJwt());
    log.debug("User {} attempting to join a game.", user.getId());
    publisher.publishEvent(new JoinGameEvent(this, messageModel.getGameId(), user));
  }

  @MessageMapping("/game/act")
  public void performGameAction(final ClientMessage<GameActionData> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    log.debug("Player performed {} action.", messageModel.getData().getActionType());
    publisher.publishEvent(new GameActionEvent(this, messageModel.getData()));
  }
}
