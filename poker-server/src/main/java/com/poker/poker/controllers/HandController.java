package com.poker.poker.controllers;

import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.events.DrawCardEvent;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.repositories.HandRepository;
import com.poker.poker.services.JwtService;
import com.poker.poker.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/game/hand")
@Tag(name = "hand", description = "Hand API handles all game requests after the game has started.")
public class HandController {

  private final UserService userService;
  private final GameConstants constants;
  private final JwtService jwtService;
  private final HandRepository handRepository;
  private final ApplicationEventPublisher publisher;

  @Operation(
      summary = "Draws a card.",
      description = "Draws a card from the top of the deck.",
      tags = "game")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Draw was successful.",
            content =
                @Content(
                    schema = @Schema(implementation = ApiSuccessModel.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @RequestMapping(value = "/draw", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccessModel> draw(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt) {
    userService.validate(jwt, constants.getClientGroups());
    final UUID id = jwtService.getUserDocument(jwt).getId();
    publisher.publishEvent(new DrawCardEvent(this, id));
    return ResponseEntity.ok(new ApiSuccessModel("Card drawn."));
  }

  //  @Operation(
  //      summary = "Determines winner of hand.",
  //      description = "Responds with the player model of the winner of a hand.",
  //      tags = "game")
  //  @ApiResponses(
  //      value = {
  //        @ApiResponse(
  //            responseCode = "200",
  //            description = "Draw was successful.",
  //            content =
  //                @Content(
  //                    schema = @Schema(implementation = PlayerModel.class),
  //                    mediaType = MediaType.APPLICATION_JSON_VALUE))
  //      })
  //  @RequestMapping(value = "/determine-winner/{handId}", method = RequestMethod.POST)
  //  public ResponseEntity<PlayerModel> determineWinner(
  //      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
  //      @PathVariable UUID handId) {
  //    userService.validate(jwt, constants.getClientGroups());
  //    return ResponseEntity.ok(
  //        handService.determineWinner(handRepository.findHandDocumentById(handId)));
  //  }
}
