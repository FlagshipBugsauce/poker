package com.poker.poker.controllers;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.documents.GameDocument;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.models.game.GetGameModel;
import com.poker.poker.repositories.UserRepository;
import com.poker.poker.services.GameService;
import com.poker.poker.services.JwtService;
import com.poker.poker.services.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/game")
public class GameController {
  private GameService gameService;
  private UserService userService;
  private AppConstants appConstants;
  private JwtService jwtService;
  private UserRepository userRepository;

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public ResponseEntity<GameDocument> createGame(
      @RequestHeader("Authorization") String jwt, @RequestBody CreateGameModel createGameModel) {
    userService.validate(jwt, appConstants.getClientGroup());
    // Find UserDocument of this user to pass ID into createGame
    UserDocument userDocument =
        userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt));
    return ResponseEntity.ok(gameService.createGame(createGameModel, userDocument.getId()));
  }

  @RequestMapping(value = "/get", method = RequestMethod.POST)
  public ResponseEntity<List<GetGameModel>> getGame(@RequestHeader("Authorization") String jwt) {
    userService.validate(jwt, appConstants.getClientGroup());
    return ResponseEntity.ok(gameService.getGame());
  }
}
