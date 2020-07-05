package com.poker.poker.controllers;

import com.poker.poker.config.AppConfig;
import com.poker.poker.events.CurrentGameEvent;
import com.poker.poker.events.LeaveGameEvent;
import com.poker.poker.events.RejoinGameEvent;
import com.poker.poker.models.SocketContainerModel;
import com.poker.poker.models.WebSocketUpdateModel;
import com.poker.poker.models.websocket.ActionModel;
import com.poker.poker.services.JwtService;
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
        data = lobbyService.getLobbyDocument(updateModel.getId());
        break;
      case Game:
        data = gameService.getGameDocument(updateModel.getId());
        break;
      case Hand:
        data = handService.getHand(gameService.getGameDocument(updateModel.getId()));
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
        updateModel.getTopic(), new SocketContainerModel(updateModel.getType(), data));
  }

  @MessageMapping("/game/leave")
  public void leaveGame(final ActionModel action) {
    //    gameService.leaveGame(jwtService.getUserDocument(action.getJwt()));
    applicationEventPublisher.publishEvent(
        new LeaveGameEvent(this, jwtService.getUserDocument(action.getJwt())));
  }

  //  @RequestMapping("${spring.data.rest.base-path}/_ping")
  @MessageMapping("/game/rejoin")
  public void rejoinGame(final ActionModel action) {
    //    gameService.rejoinGame(jwtService.getUserDocument(action.getJwt()));
    applicationEventPublisher.publishEvent(
        new RejoinGameEvent(this, jwtService.getUserDocument(action.getJwt())));
  }

  @MessageMapping("/game/current/update")
  public void requestCurrentGameUpdate(final ActionModel action) {
    log.debug("User {} requesting update.", action.getUserId());
    applicationEventPublisher.publishEvent(
        new CurrentGameEvent(
            this, action.getUserId(), gameService.getCurrentGameModel(action.getUserId())));
  }
}
