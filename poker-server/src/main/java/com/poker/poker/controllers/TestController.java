package com.poker.poker.controllers;

import com.poker.poker.models.WebSocketUpdateModel;
import com.poker.poker.models.game.DrawGameDataContainerModel;
import com.poker.poker.models.game.DrawGameDataModel;
import com.poker.poker.models.game.GameModel;
import com.poker.poker.models.game.HandModel;
import com.poker.poker.models.game.LobbyModel;
import com.poker.poker.models.websocket.ActionModel;
import com.poker.poker.models.websocket.ClientMessageModel;
import com.poker.poker.models.websocket.CurrentGameModel;
import com.poker.poker.models.websocket.ToastModel;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is serving as a place to return models that aren't returned anywhere else, so that the
 * client model generation will generate client models automatically.
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {
  /*
     Temporary endpoints so that schemas are generated for these models.
  */

  @GetMapping("/test/lobbyDoc")
  public ResponseEntity<LobbyModel> lobbyDocument() {
    return ResponseEntity.ok(new LobbyModel());
  }

  @GetMapping("/test/handDoc")
  public ResponseEntity<HandModel> handDocument() {
    return ResponseEntity.ok(new HandModel());
  }

  @GetMapping("/test/gameDoc")
  public ResponseEntity<GameModel> gameDocument() {
    return ResponseEntity.ok(new GameModel());
  }

  @GetMapping("/test/gameData")
  public ResponseEntity<List<DrawGameDataModel>> gameData() {
    return ResponseEntity.ok(new ArrayList<>());
  }

  @GetMapping("/test/gameDataContainer")
  public ResponseEntity<DrawGameDataContainerModel> gameDataContainer() {
    return ResponseEntity.ok(new DrawGameDataContainerModel());
  }

  @GetMapping("/test/websocketUpdateModel")
  public ResponseEntity<WebSocketUpdateModel> webSocketUpdateModel() {
    return ResponseEntity.ok(new WebSocketUpdateModel());
  }

  @GetMapping("/test/websocket/models/action-model")
  public ResponseEntity<ActionModel> actionModel() {
    return ResponseEntity.ok(new ActionModel());
  }

  @GetMapping("/test/websocket/models/toast-model")
  public ResponseEntity<ToastModel> toastModel() {
    return ResponseEntity.ok(new ToastModel());
  }

  @GetMapping("/test/websocket/models/current-game-model")
  public ResponseEntity<CurrentGameModel> currentGameModel() {
    return ResponseEntity.ok(new CurrentGameModel());
  }

  @GetMapping("/test/websocket/models/client-message-model")
  public ResponseEntity<ClientMessageModel> clientMessageModel() {
    return ResponseEntity.ok(new ClientMessageModel());
  }
}
