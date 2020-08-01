package com.poker.poker.controllers;

import com.poker.poker.config.AppConfig;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.events.ChatMessageEvent;
import com.poker.poker.events.CreateGameEvent;
import com.poker.poker.events.JoinGameEvent;
import com.poker.poker.events.LeaveGameEvent;
import com.poker.poker.events.PublishCurrentGameEvent;
import com.poker.poker.events.RejoinGameEvent;
import com.poker.poker.models.game.GameParameterModel;
import com.poker.poker.models.websocket.ClientMessageModel;
import com.poker.poker.models.websocket.GenericServerMessage;
import com.poker.poker.models.websocket.WebSocketUpdateModel;
import com.poker.poker.services.JwtService;
import com.poker.poker.services.UserService;
import com.poker.poker.services.WebSocketService;
import com.poker.poker.services.game.GameDataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@AllArgsConstructor
@Tag(name = "websocket", description = "WebSocket controller.")
public class WebSocketController {

  private final AppConfig appConfig;
  private final UserService userService;
  private final JwtService jwtService;
  private final ApplicationEventPublisher publisher;
  private final WebSocketService webSocketService;

  private final GameDataService data;

  @MessageMapping("/game/update")
  public void getUpdate(final WebSocketUpdateModel updateModel) {
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
          data = this.data.getPlayerData(updateModel.getId());
          break;
        case PokerTable:
          data = this.data.getPokerTable(updateModel.getId());
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

  @MessageMapping("/chat/send")
  public void chatMessage(final ClientMessageModel<String> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    publisher.publishEvent(
        new ChatMessageEvent(
            this,
            jwtService.getUserDocument(messageModel.getJwt()),
            messageModel.getData(),
            messageModel.getGameId()));
  }

  @MessageMapping("/game/leave")
  public void leaveGame(final ClientMessageModel<Void> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    publisher.publishEvent(
        new LeaveGameEvent(this, jwtService.getUserDocument(messageModel.getJwt())));
  }

  @MessageMapping("/game/rejoin")
  public void rejoinGame(final ClientMessageModel<Void> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    publisher.publishEvent(
        new RejoinGameEvent(this, jwtService.getUserDocument(messageModel.getJwt())));
  }

  @MessageMapping("/game/current/update")
  public void requestCurrentGameUpdate(final ClientMessageModel<Void> messageModel) {
    log.debug("User {} requesting update.", messageModel.getUserId());
    publisher.publishEvent(new PublishCurrentGameEvent(this, messageModel.getUserId()));
  }

  @MessageMapping("/game/create")
  public void createGame(final ClientMessageModel<GameParameterModel> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    final UserDocument user = jwtService.getUserDocument(messageModel.getJwt());
    log.debug("User {} attempting to create a game.", user.getId());
    publisher.publishEvent(new CreateGameEvent(this, messageModel.getData(), user));
  }

  @MessageMapping("/game/join")
  public void joinGame(final ClientMessageModel<Void> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    final UserDocument user = jwtService.getUserDocument(messageModel.getJwt());
    log.debug("User {} attempting to join a game.", user.getId());
    publisher.publishEvent(new JoinGameEvent(this, messageModel.getGameId(), user));
  }
}
