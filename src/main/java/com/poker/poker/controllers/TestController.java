package com.poker.poker.controllers;

import com.poker.poker.documents.GameDocument;
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

  /**
   * Basic test endpoint. Returns an ApiSuccessModel.
   *
   * @return 200 status with an ApiSuccessModel.
   */
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiSuccessModel.class)))
  })
  @RequestMapping(value = "test", method = RequestMethod.POST)
  public ResponseEntity<?> test001() {
    return ResponseEntity.ok(new ApiSuccessModel("Success!"));
  }

  /**
   * Another test, this one takes in a model.
   *
   * @return 200 status with an ApiSuccessModel.
   */
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiSuccessModel.class)))
  })
  @RequestMapping(value = "get", method = RequestMethod.GET)
  public ResponseEntity<?> test002() {
    return ResponseEntity.ok(new ApiSuccessModel("TESTING"));
  }

  /*
   * The two endpoints below are an example of how we can handle sending out updates in the game state to clients,
   * without the clients having to constantly poll the backend.
   *
   * Each "game" (represented by a game model) will have a UUID associated with it and will have a collection of
   * user UUID's representing the players currently in the game.
   *
   * Players will join a game by calling an endpoint which has a parameter equal to the UUID of the game they're
   * attempting to join and their user UUID will be added to the game model's collection of user UUID's.
   *
   * When a player joins a game, the player's client will be assigned a unique SseEmitter, which will be stored in a
   * HashMap, keyed by the players UUID, giving us O(1) exp. access to their unique emitter, which will send data
   * only to their specific client. This means we can send them high security information such as the cards they're
   * dealt.
   *
   * In order to give each players client updates when critical events occur in the game (i.e. someone folds,
   * checks, etc...), we simply iterate over the game's collection of user UUID's, lookup each SseEmitter in the
   * hash table of emitters, and we "send" the updated game model to each client. This will allow the clients to
   * modify the GUI appropriately, to reflect the current  game state.
   *
   * Note: Security is currently disabled for these endpoints.
   */

  /** HashMap of SseEmitters */
  private Map<String, SseEmitter> emitters;

  /**
   * Testing SseEmitters
   *
   * @return An SseEmitter that can send the client information.
   */
  @GetMapping("/sse/test01/{x}")
  public SseEmitter test003(@PathVariable String x) {
    SseEmitter emitter = emitters.get(x);
    if (emitter == null) {
      emitter = new SseEmitter(1000 * 60 * 60 * 24 * 14L);
      emitter.onCompletion(() -> emitters.remove(x));

      SseEmitter finalEmitter = emitter;
      emitter.onTimeout(
          () -> {
            finalEmitter.complete();
            emitters.remove(x);
          });
      emitters.put(x, emitter);
      log.info("Creating emitter for user with ID: {}", x);
    }
    return emitter;
  }

  /**
   * Send a TestModel containing the string y, to user associated to emitter with ID = x.
   *
   * @param x ID of an emitter.
   * @param y Message to send to user.
   */
  @GetMapping("/sse/test02/{x}/{y}")
  public void test004(@PathVariable String x, @PathVariable String y) {
    SseEmitter emitter = emitters.get(x);
    if (emitter != null) {
      try {
        log.info("Sending data to client with ID: {}, message: {}", x, y);
        emitter.send(new TestModel(x, y));
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      log.error("Attempted to send data to client with ID: {}, but this client doesn't exist.", x);
    }
  }

  @GetMapping("sse/test03/{userID}")
  public ResponseEntity<?> test005(@PathVariable String userID) {
    return ResponseEntity.ok(userID);
  }

  @GetMapping("/test/gameDoc")
  public ResponseEntity<GameDocument> test006() {
    return ResponseEntity.ok(new GameDocument());
  }
}

/** Test model for test controllers. */
@Data
@AllArgsConstructor
class TestModel {
  @Schema(description = "An ID.", example = "17")
  private String id;

  @Schema(description = "A message.", example = "Hello world!")
  private String message;
}
