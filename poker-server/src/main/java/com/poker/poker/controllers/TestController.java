package com.poker.poker.controllers;

import com.poker.poker.events.DealCardsEvent;
import com.poker.poker.events.PrivateMessageEvent;
import com.poker.poker.models.ModelModel;
import com.poker.poker.models.enums.MessageType;
import com.poker.poker.models.user.UserModel;
import com.poker.poker.services.JwtService;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

  private final ApplicationEventPublisher publisher;
  private final JwtService jwtService;

  /*
     Endpoint so that schemas are generated which are used to generate client models.
  */
  @GetMapping("/models")
  public ResponseEntity<ModelModel> models() {
    return ResponseEntity.ok(new ModelModel());
  }

  @GetMapping("/deal")
  public void dealCards(@RequestBody final UUID gameId) {
    publisher.publishEvent(new DealCardsEvent(this, gameId));
  }

  @GetMapping("/send-private-message")
  public void sendPrivateMessage(
      @Parameter(hidden = true) @RequestHeader("Authorization") final String jwt,
      @RequestBody final String message) {
    final UserModel user = jwtService.getUserDocument(jwt);
    publisher.publishEvent(new PrivateMessageEvent<>(
        this, MessageType.Debug, user.getId(), message));
  }
}
