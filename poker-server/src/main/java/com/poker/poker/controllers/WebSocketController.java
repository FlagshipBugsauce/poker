package com.poker.poker.controllers;

import com.poker.poker.models.SocketContainerModel;
import com.poker.poker.models.WebSocketUpdateModel;
import com.poker.poker.services.WebSocketService;
import com.poker.poker.services.game.GameService;
import com.poker.poker.services.game.HandService;
import com.poker.poker.services.game.LobbyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@AllArgsConstructor
@Tag(name = "websocket", description = "WebSocket controller.")
public class WebSocketController {

  //  private final List<ApiSuccessModel> data = new ArrayList<>();
  //  private final UserRepository userRepository;
  //  private final JwtService jwtService;
  //  private final SimpMessagingTemplate template;
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
}
