package com.poker.poker.controllers;

import com.poker.poker.documents.GameDocument;
import com.poker.poker.documents.HandDocument;
import com.poker.poker.documents.LobbyDocument;
import com.poker.poker.models.enums.EmitterType;
import com.poker.poker.models.game.DrawGameDataContainerModel;
import com.poker.poker.models.game.DrawGameDataModel;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is a class where we can test various things. At some point we can get rid of it, but for
 * now, it will be useful to test some design ideas, etc...
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
  public ResponseEntity<LobbyDocument> lobbyDocument() {
    return ResponseEntity.ok(new LobbyDocument());
  }

  @GetMapping("/test/handDoc")
  public ResponseEntity<HandDocument> handDocument() {
    return ResponseEntity.ok(new HandDocument());
  }

  @GetMapping("/test/gameDoc")
  public ResponseEntity<GameDocument> gameDocument() {
    return ResponseEntity.ok(new GameDocument());
  }

  @GetMapping("/test/gameData")
  public ResponseEntity<List<DrawGameDataModel>> gameData() {
    return ResponseEntity.ok(new ArrayList<>());
  }

  @GetMapping("/test/gameDataContainer")
  public ResponseEntity<DrawGameDataContainerModel> gameDataContainer() {
    return ResponseEntity.ok(new DrawGameDataContainerModel());
  }
}
