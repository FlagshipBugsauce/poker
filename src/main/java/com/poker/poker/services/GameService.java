package com.poker.poker.services;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.documents.GameDocument;
import com.poker.poker.models.enums.GameState;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.models.game.GameActionModel;
import com.poker.poker.models.game.GetGameModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GameService {
  List<GameDocument> activeGames = new ArrayList<GameDocument>();
  private AppConstants appConstants;

  public GameDocument createGame(CreateGameModel createGameModel, UUID creator) {
    GameDocument gameDocument =
        new GameDocument(
            UUID.randomUUID(),
            creator,
            createGameModel.getName(),
            new ArrayList<UUID>(Arrays.asList(creator)),
            new ArrayList<GameActionModel>(),
            GameState.PreGame);
    log.info(appConstants.getGameCreation(), creator);
    activeGames.add(gameDocument);
    return gameDocument;
  }

  public List<GetGameModel> getGame() {
    List<GetGameModel> activeGameModels = new ArrayList<GetGameModel>();
    for (GameDocument gd : activeGames) {
      GetGameModel getGameModel =
          new GetGameModel(gd.getGameName(), gd.getUserIDs().size(), gd.getCurrentGameState());
      activeGameModels.add(getGameModel);
    }
    return activeGameModels;
  }
}
