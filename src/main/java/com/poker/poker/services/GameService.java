package com.poker.poker.services;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.documents.GameDocument;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.enums.GameState;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.models.game.GameActionModel;
import com.poker.poker.models.game.GetGameModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@AllArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GameService {
  HashMap<UUID, GameDocument> activeGames;
  HashMap<UUID, SseEmitter> gameEmitters;
  private AppConstants appConstants;

  /**
   * Creates a new game document based on attributes given in createGameModel.
   *
   * @param createGameModel A model containing: name, maximum players, and buy in.
   * @param myid the UUID of the client.
   * @return a UUID, the unique id for the game document created in this method.
   */
  public UUID createGame(CreateGameModel createGameModel, UUID myid) {
    GameDocument gameDocument =
        new GameDocument(
            UUID.randomUUID(),
            myid,
            createGameModel.getName(),
            createGameModel.getMaxPlayers(),
            createGameModel.getBuyIn(),
            new ArrayList<UUID>(Arrays.asList(myid)),
            new ArrayList<GameActionModel>(),
            GameState.PreGame);
    log.info(appConstants.getGameCreation(), myid);
    activeGames.put(myid, gameDocument);
    return gameDocument.getId();
  }

  /**
   * Gets the list of active game models that are currently in the PreGame state.
   *
   * @return An ActiveGameModel which is a subset of a game document.
   */
  public List<GetGameModel> getGameList() {
    List<GetGameModel> activeGameModels = new ArrayList<GetGameModel>();
    for (GameDocument gd : activeGames.values()) {
      //Games are only join-able in the PreGame game state
      if (gd.getCurrentGameState() == GameState.PreGame) {
        GetGameModel getGameModel =
            new GetGameModel(
                gd.getName(),
                gd.getHost(),
                gd.getPlayerIds().size(),
                gd.getMaxPlayers(),
                gd.getBuyIn());
        activeGameModels.add(getGameModel);
      }
    }
    return activeGameModels;
  }

  /**
   * Join a game by adding the clients UUID to the list of player ids and updating all the other
   * players game documents in the game.
   *
   * @param gameid the UUID of the game document the client wishes to join.
   * @param myid the UUID of the client.
   * @return An ApiSuccessModel that contains a success message.
   */
  public ApiSuccessModel joinGame(String gameid, UUID myid) {
    //Find the active game you wish to join
    GameDocument game = new GameDocument();
    for (UUID id : activeGames.keySet()) {
      if (id.toString().equals(gameid)) {
        game = activeGames.get(id);
      }
    }
    //Add self to list of players in game
    game.getPlayerIds().add(myid);
    //Update all players copy of gameDocument who are in the game via SSE
    for (UUID id : game.getPlayerIds()) {
      SseEmitter emitter = gameEmitters.get(id);
      if (id != null && id != myid) {
        try {
          log.info(appConstants.getJoinGameSuccess(), id);
          emitter.send(game);
        }
        catch (IOException e) {
          log.error(appConstants.getJoinGameFail(), id);
        }
      }
    }
    return new ApiSuccessModel(appConstants.getJoinWorking());
  }
}
