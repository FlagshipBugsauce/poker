package com.poker.poker.controllers;

import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.services.JwtService;
import com.poker.poker.services.UserService;
import com.poker.poker.services.game.HandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Tag(
    name = "hand",
    description =
        "Hand API handles all game requests after the game has started.")
public class HandController {

  private UserService userService;
  private GameConstants constants;
  private JwtService jwtService;
  private HandService handService;

  @Operation(
      summary = "Roll a random number.",
      description = "Generates a random number.",
      tags = "game")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "Roll was successful.",
              content =
              @Content(
                  schema = @Schema(implementation = ApiSuccessModel.class),
                  mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @RequestMapping(value = "/roll", method = RequestMethod.POST)
  public ResponseEntity<ApiSuccessModel> roll(
      @Parameter(hidden = true) @RequestHeader("Authorization") String jwt) {
    userService.validate(jwt, constants.getClientGroups());
    return ResponseEntity.ok(handService.roll(jwtService.getUserDocument(jwt)));
  }
}
