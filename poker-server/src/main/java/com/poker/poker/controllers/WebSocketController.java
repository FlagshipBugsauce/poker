package com.poker.poker.controllers;

import com.poker.poker.config.AppConfig;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.events.ChatMessageEvent;
import com.poker.poker.events.CreateGameEvent;
import com.poker.poker.events.CurrentGameEvent;
import com.poker.poker.events.JoinGameEvent;
import com.poker.poker.events.LeaveGameEvent;
import com.poker.poker.events.RejoinGameEvent;
import com.poker.poker.models.WebSocketUpdateModel;
import com.poker.poker.models.game.GameParameterModel;
import com.poker.poker.models.websocket.ActionModel;
import com.poker.poker.models.websocket.ClientMessageModel;
import com.poker.poker.models.websocket.GenericServerMessage;
import com.poker.poker.services.JwtService;
import com.poker.poker.services.UserService;
import com.poker.poker.services.WebSocketService;
import com.poker.poker.services.game.GameService;
import com.poker.poker.services.game.HandService;
import com.poker.poker.services.game.LobbyService;
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
  private final ApplicationEventPublisher applicationEventPublisher;
  private final WebSocketService webSocketService;
  private final LobbyService lobbyService;
  private final GameService gameService;
  private final HandService handService;

  @MessageMapping("/game/update")
  public void getUpdate(final WebSocketUpdateModel updateModel) {
    log.debug(
        "Sending update of type: {} to topic: {}.", updateModel.getType(), updateModel.getTopic());
    final Object data;
    switch (updateModel.getType()) {
      case GameList:
        data = lobbyService.getLobbyList();
        break;
      case Lobby:
        data = lobbyService.getLobbyModel(updateModel.getId());
        break;
      case Game:
        data = gameService.getGameModel(updateModel.getId());
        break;
      case Hand:
        data = handService.getHand(gameService.getGameModel(updateModel.getId()));
        break;
      case GameData:
        data = gameService.getGameData(updateModel.getId());
        break;
      case PlayerData:
        data = gameService.getPlayerData(updateModel.getId());
        break;
      default:
        data = null;
    }
    webSocketService.sendPublicMessage(
        updateModel.getTopic(), new GenericServerMessage<>(updateModel.getType(), data));
  }

  @MessageMapping("/chat/send")
  public void chatMessage(final ClientMessageModel<String> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    applicationEventPublisher.publishEvent(new ChatMessageEvent(
        this,
        jwtService.getUserDocument(messageModel.getJwt()),
        messageModel.getData(),
        messageModel.getGameId()));
  }

  @MessageMapping("/game/leave")
  public void leaveGame(final ClientMessageModel<Void> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    applicationEventPublisher.publishEvent(
        new LeaveGameEvent(this, jwtService.getUserDocument(messageModel.getJwt())));
  }

  @MessageMapping("/game/rejoin")
  public void rejoinGame(final ClientMessageModel<Void> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    applicationEventPublisher.publishEvent(
        new RejoinGameEvent(this, jwtService.getUserDocument(messageModel.getJwt())));
  }

  @MessageMapping("/game/current/update")
  public void requestCurrentGameUpdate(final ActionModel action) {
    log.debug("User {} requesting update.", action.getUserId());
    applicationEventPublisher.publishEvent(
        new CurrentGameEvent(
            this, action.getUserId(), gameService.getCurrentGameModel(action.getUserId())));
  }

  @MessageMapping("/game/create")
  public void createGame(final ClientMessageModel<GameParameterModel> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    final UserDocument user = jwtService.getUserDocument(messageModel.getJwt());
    log.debug("User {} attempting to create a game.", user.getId());
    applicationEventPublisher.publishEvent(new CreateGameEvent(this, messageModel.getData(), user));
  }

  @MessageMapping("/game/join")
  public void joinGame(final ClientMessageModel<Void> messageModel) {
    userService.validate(messageModel.getJwt(), appConfig.getGeneralGroups());
    final UserDocument user = jwtService.getUserDocument(messageModel.getJwt());
    log.debug("User {} attempting to join a game.", user.getId());

    gameService.checkIfGameExists(messageModel.getGameId());
    gameService.checkIfGameIsInLobbyState(messageModel.getGameId());
    if (gameService.isUserInSpecifiedGame(messageModel.getGameId(), user.getId())) {
      return;
    }
    gameService.checkIfUserIsInGame(user.getId());

    applicationEventPublisher.publishEvent(new JoinGameEvent(this, messageModel.getGameId(), user));
  }
}
