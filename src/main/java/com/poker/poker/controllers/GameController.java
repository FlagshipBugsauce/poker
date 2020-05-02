package com.poker.poker.controllers;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.documents.GameDocument;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.AuthResponseModel;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.models.game.GetGameModel;
import com.poker.poker.repositories.UserRepository;
import com.poker.poker.services.GameService;
import com.poker.poker.services.JwtService;
import com.poker.poker.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/game")
@Tag(
    name = "games",
    description =
        "Games API handles all game logistics like creating a game, joining a game, etc...")
public class GameController {
  private GameService gameService;
  private UserService userService;
  private AppConstants appConstants;
  private JwtService jwtService;
  private UserRepository userRepository;

  @Operation(
      summary = "Create a new game",
      description = "Create a new game document and return a UUID of said game document",
      tags = "games")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "Creation of game was successful. A UUID should be returned",
              content =
              @Content(
                  schema = @Schema(implementation = UUID.class),
                  mediaType = "application/json"))
      })
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public ResponseEntity<UUID> createGame(
      @RequestHeader("Authorization") String jwt, @RequestBody CreateGameModel createGameModel) {
    userService.validate(jwt, appConstants.getClientGroup());
    // Find UserDocument of this user to pass ID into createGame
    UserDocument userDocument =
        userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt));
    return ResponseEntity.ok(gameService.createGame(createGameModel, userDocument.getId()));
  }

  @Operation(
      summary = "Get game list",
      description = "Get the list of games in the active game hashmap which are in PreGame state."
          + " A GetGameModel should be returned",
      tags = "games")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "Getting game active list was successful. A GetGameModel should be returned",
              content =
              @Content(
                  schema = @Schema(implementation = GetGameModel.class),
                  mediaType = "application/json"))
      })
  @RequestMapping(value = "/get", method = RequestMethod.POST)
  public ResponseEntity<List<GetGameModel>> getGameList(@RequestHeader("Authorization") String jwt) {
    userService.validate(jwt, appConstants.getClientGroup());
    return ResponseEntity.ok(gameService.getGameList());
  }

  @Operation(
      summary = "Join a game",
      description = "Join a game using the game UUID",
      tags = "games")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "Join game was successful",
              content =
              @Content(
                  schema = @Schema(implementation = ApiSuccessModel.class),
                  mediaType = "application/json"))
      })
  @RequestMapping(value = "/join/{gameid}", method = RequestMethod.GET)
  public ResponseEntity<ApiSuccessModel> joinGame(@RequestHeader("Authorization") String jwt, @PathVariable String gameid) {
    userService.validate(jwt, appConstants.getClientGroup());
    UserDocument userDocument =
        userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt));
    return ResponseEntity.ok(gameService.joinGame(gameid, userDocument.getId()));
  }
}
