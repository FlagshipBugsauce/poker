package com.poker.poker.controllers;

import com.poker.poker.documents.GameDocument;
import com.poker.poker.documents.HandDocument;
import com.poker.poker.documents.LobbyDocument;
import com.poker.poker.models.ApiSuccessModel;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
}
