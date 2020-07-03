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
    //    log.debug("{}", data.toString());
    webSocketService.sendPublicMessage(
        updateModel.getTopic(), new SocketContainerModel(updateModel.getType(), data));
  }
  //
  //  @SubscribeMapping("/test01")
  //  public List<ApiSuccessModel> webSocketTest002() {
  //    return data;
  //  }
  //
  //  @MessageMapping("/test02")
  //  @SendTo("/topic/test03")
  //  public ApiSuccessModel webSocketTest003(final ApiSuccessModel newData) {
  //    data.add(newData);
  //    log.debug("Data was sent: {}", newData.getMessage());
  //    return newData;
  //  }
  //
  //  @SendTo("/topic/{user}")
  //  public ApiSuccessModel webSocketTest004(@DestinationVariable final String jwt) {
  //    final UserDocument user = jwtService.getUserDocument(jwt);
  //    return new ApiSuccessModel("This is a custom message for " + user.getEmail());
  //  }
  //
  //  @MessageMapping("/test03")
  //  public void webSocketTest005(final ApiSuccessModel newData) {
  ////    webSocketTest004(newData.getMessage());
  //    testFunction001(newData.getMessage());
  //  }
  //
  //  private void testFunction001(final String jwt) {
  //    final UserDocument user = jwtService.getUserDocument(jwt);
  //    log.debug("Sending message to client with EMAIL: {}", user.getEmail());
  //    template.convertAndSend("/topic/" + jwt, new ApiSuccessModel("This is a custom message for "
  // + user.getEmail()));
  //  }

  /*
     So, currently, we can send data to a custom topic with JWT in it (or some UUID associated with
     a user) to send a message to only one user.

     Should be able to refactor methods like broadcastGameDocument and the like to use this
     "convertAndSend" method to send updated gameDocuments to people who are subscribed to a topic
     with the game's UUID.

     Should be able to wrap GameDocument's, LobbyDocument's, etc... in some kind of container with
     an enum field that will indicate what type of data it is. The client can have a similar enum,
     to determine what type of data is being sent.

     We can have topics for the game list and for the game itself.

  */

  //
  //  @RequestMapping(value = "/socket/private-topic", method = RequestMethod.GET)
  //  public ResponseEntity<ApiSuccessModel> getPrivateTopic(
  //      @Parameter(hidden = true) @RequestHeader("Authorization") final String jwt) {
  //    return ResponseEntity.ok(
  //        new ApiSuccessModel(
  //            webSocketService.requestPrivateTopic(jwtService.getUserDocument(jwt)).toString()));
  //  }
}
