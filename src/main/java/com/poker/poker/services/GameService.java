package com.poker.poker.services;

import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.documents.GameDocument;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.enums.EmitterType;
import com.poker.poker.models.enums.GameAction;
import com.poker.poker.models.enums.GameState;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.models.game.GameActionModel;
import com.poker.poker.models.game.GetGameModel;
import com.poker.poker.models.game.PlayerModel;
import com.poker.poker.validation.exceptions.BadRequestException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

  private SseService sseService;

  /** A map of active games, keyed by the games ID. */
  private Map<UUID, GameDocument> activeGames;

  /**
   * A map of game UUID's, keyed by user UUID's, to identify which game a user is currently in, and
   * also to identify whether a user is currently in a game, in order to impose a one game limit at
   * any given time.
   */
  private Map<UUID, UUID> userIdToGameIdMap;

  private GameConstants gameConstants;

  private UuidService uuidService;

  /**
   * Sends out a game document to all players in the game associated with the game document
   * argument.
   *
   * @param gameDocument GameDocument representing the game that is being updated.
   */
  private void sendGameDocumentToAllPlayers(GameDocument gameDocument) {
    for (PlayerModel player : gameDocument.getPlayers()) {
      try {
        sseService.sendUpdate(EmitterType.Lobby, player.getId(), gameDocument);
      } catch (BadRequestException ignored) { // Exception already logged.
      }
    }
  }

  /**
   * Helper to do determine whether a user is in a game.
   * @param userId ID of the user.
   * @return True if user is in some game, false otherwise.
   */
  public boolean isUserInGame(UUID userId) {
    return userIdToGameIdMap.get(userId) != null;
  }

  /**
   * Helper to retrieve the game document associated with the game a specified user is in.
   * @param userId ID of the user.
   * @return GameDocument of the game the user is in.
   */
  public GameDocument getUsersGameDocument(UUID userId) {
    return activeGames.get(userIdToGameIdMap.get(userId));
  }

  /**
   * Helper to determine whether a specified user is listed as one of the players in a game.
   * @param userId ID of the user.
   */
  public void checkUserIsPlayerInGame(UUID userId) {
    GameDocument gameDocument = getUsersGameDocument(userId);
    if (gameDocument == null) {
      log.error("There is no game associated with user {}.", userId);
      throw gameConstants.getInvalidUuidException();
    }
    if (gameDocument.getPlayers().stream().noneMatch(p -> p.getId().equals(userId))) {
      log.error("User {} is not listed as a player of game {}.", gameDocument.getId(), userId);
      throw gameConstants.getUserNotInGameException();
    }
  }

  /**
   * Updates the game document associated with the game a player is in, to indicate that the player
   * is ready for the game to start.
   *
   * @param user The user who is ready.
   * @return ApiSuccessModel indicating the request was successful.
   */
  public ApiSuccessModel ready(UserDocument user) {
    if (!isUserInGame(user.getId()) || getUsersGameDocument(user.getId()) == null) {
      log.error("Failed to set player's status to ready (user ID: {}).", user.getId());
      throw gameConstants.getReadyStatusUpdateFailException();
    }

    // Get the game document for the game now that we're sure it exists.
    GameDocument gameDocument = getUsersGameDocument(user.getId());

    // Find the playerModel, throw if not found, otherwise, remove player from game.
    final Optional<PlayerModel> player =
        gameDocument.getPlayers().stream()
            .filter(playerModel -> playerModel.getId().equals(user.getId()))
            .findFirst();
    if (!player.isPresent()) {
      log.error("Failed to set player's status to ready (user ID: {}).", user.getId().toString());
      throw gameConstants.getReadyStatusUpdateFailException();
    }

    // Set players status to ready.
    player.get().setReady(true);

    // Add the appropriate game action model.
    gameDocument
        .getGameActions()
        .add(
            new GameActionModel(
                UUID.randomUUID(),
                player.get(),
                GameAction.Ready,
                String.format(
                    "%s %s is ready.", player.get().getFirstName(), player.get().getLastName())));

    log.debug("Player status set to ready (ID: {}).", user.getId().toString());

    sendGameDocumentToAllPlayers(gameDocument);
    return new ApiSuccessModel("Player status set to ready.");
  }

  /**
   * Checks whether a user is in/not in a game and throws. If the boolean argument in is true,
   * then this method will throw if the user IS NOT in a game. If the boolean argument is false,
   * then this method will throw if the user IS in a game.
   * @param userId ID of the user being checked.
   * @param in Flag to determine whether the user should or should not, be in a game.
   */
  public void checkWhetherUserIsInGameAndThrow(final UUID userId, final boolean in) {
    if (userIdToGameIdMap.get(userId) == null && in) {
      throw gameConstants.getUserNotInGameException();
    } else if (userIdToGameIdMap.get(userId) != null && !in) {
      throw gameConstants.getJoinGamePlayerAlreadyJoinedException();
    }
  }

  /**
   * Sometimes a client will want to manually request an updated game document. In cases such as
   * these, this service will send an update to the client.
   *
   * @param userId ID of the user in need of the updated game document.
   * @return ApiSuccessModel indicating that the request was successful.
   */
  public ApiSuccessModel getGameDocumentUpdate(UUID userId) {
    sseService
        .sendUpdate(EmitterType.Lobby, userId, activeGames.get(userIdToGameIdMap.get(userId)));
    return new ApiSuccessModel("Updated game document was sent successfully.");
  }

  /**
   * Creates a new game document based on attributes given in createGameModel.
   *
   * @param createGameModel A model containing: name, maximum players, and buy in.
   * @param user the user document of the player creating the game.
   * @return a UUID, the unique id for the game document created in this method.
   */
  public ApiSuccessModel createGame(CreateGameModel createGameModel, UserDocument user) {
    checkWhetherUserIsInGameAndThrow(user.getId(), false);
    GameDocument gameDocument =
        new GameDocument(
            UUID.randomUUID(),
            user.getId(),
            createGameModel.getName(),
            createGameModel.getMaxPlayers(),
            createGameModel.getBuyIn(),
            new ArrayList<>(
                Arrays.asList(
                    new PlayerModel(
                        user.getId(),
                        user.getEmail(),
                        user.getGroup(),
                        user.getFirstName(),
                        user.getLastName(),
                        false,
                        true))),
            new ArrayList<>(),
            GameState.PreGame);
    log.info("User: {} created a game.", user.getId());
    activeGames.put(gameDocument.getId(), gameDocument);
    userIdToGameIdMap.put(user.getId(), gameDocument.getId());

    sseService.sendToAll(EmitterType.GameList, getGameList());
    return new ApiSuccessModel(gameDocument.getId().toString());
  }

  /**
   * Gets the list of active game models that are currently in the PreGame state.
   *
   * @return An ActiveGameModel which is a subset of a game document.
   */
  public List<GetGameModel> getGameList() {
    List<GetGameModel> activeGameModels = new ArrayList<>();
    for (GameDocument gd : activeGames.values()) {
      // Games are only join-able in the PreGame game state
      if (gd.getCurrentGameState() == GameState.PreGame) {
        activeGameModels.add(
            new GetGameModel(
                gd.getId(),
                gd.getName(),
                gd.getPlayers().stream()
                    .filter(p -> p.getId().equals(gd.getHost()))
                    .findFirst()
                    .get(),
                gd.getPlayers().size(),
                gd.getMaxPlayers(),
                gd.getBuyIn()));
      }
    }
    return activeGameModels;
  }

  /**
   * Join a game by adding the clients UUID to the list of player ids and updating all the other
   * players game documents in the game.
   *
   * @param gameId The UUID of the game the player wishes to join.
   * @param user The UserDocument associated with the player attempting to join.
   * @return An ApiSuccessModel containing a message which indicates the attempt to join was
   *     successful.
   */
  public ApiSuccessModel joinGame(String gameId, UserDocument user) {
    // Make sure the game ID is a valid UUID.
    uuidService.checkIfValidAndThrowBadRequest(gameId);

    // Check if user is already in the current game.
    if (isUserInGame(user.getId()) &&
        userIdToGameIdMap.get(user.getId()).equals(UUID.fromString(gameId))) {
      return new ApiSuccessModel("Player is already in the game.");
    }
    // If user isn't in the game with ID = gameId, then throw.
    checkWhetherUserIsInGameAndThrow(user.getId(), false);

    // Find the active game you wish to join
    GameDocument gameDocument = activeGames.get(UUID.fromString(gameId));

    // Return a bad request status if there is no game with ID provided.
    if (gameDocument == null) {
      throw gameConstants.getInvalidUuidException();
    }

    // Add new player to list of players in currently in the game.
    final PlayerModel playerModel = new PlayerModel(user, false, false);
    gameDocument.getPlayers().add(playerModel);

    // Add the appropriate game action model.
    gameDocument
        .getGameActions()
        .add(
            new GameActionModel(
                UUID.randomUUID(),
                playerModel,
                GameAction.Join,
                String.format(
                    "%s %s has joined the game.",
                    playerModel.getFirstName(), playerModel.getLastName())));
    userIdToGameIdMap.put(user.getId(), gameDocument.getId());

    // Update all players copy of gameDocument who are in the game via SSE
    for (PlayerModel player : gameDocument.getPlayers()) {
      if (!player.getId().equals(user.getId())) {
        try {
          sseService.sendUpdate(EmitterType.Lobby, player.getId(), gameDocument);
        } catch (BadRequestException ignored) { // Exception is already logged.
        }
      }
    }

    sseService.sendToAll(EmitterType.GameList, getGameList());
    return new ApiSuccessModel("User joined the game successfully.");
  }

  /**
   * Removes a player from a game. This will be called when a player leaves, or when the host kicks
   * a player from the game.
   *
   * @param user The user to be removed.
   * @return ApiSuccessModel indicating that the request was completed successfully.
   */
  public ApiSuccessModel removePlayerFromGame(UserDocument user) {
    UUID gameId = userIdToGameIdMap.get(user.getId());
    if (gameId == null) {
      throw gameConstants.getLeaveGameException();
    }

    GameDocument game = activeGames.get(gameId);
    if (game == null) {
      throw gameConstants.getLeaveGameException();
    }

    // Find the playerModel, throw if not found, otherwise, remove player from game.
    final Optional<PlayerModel> player =
        game.getPlayers().stream()
            .filter(playerModel -> playerModel.getId().equals(user.getId()))
            .findFirst();
    if (!player.isPresent()) {
      throw gameConstants.getLeaveGameException();
    }
    game.getPlayers().removeIf(playerModel -> playerModel.getId().equals(user.getId()));

    if (game.getPlayers().size() > 0) {
      // Select another host.
      game.setHost(game.getPlayers().get(0).getId());
      log.debug("Changing host from: {}, to: {}.", user.getId(), game.getHost());
    } else {
      // The game is empty, so remove it.
      activeGames.remove(game.getId());
      log.debug("No players left in game: {}, removing game.", game.getId());
    }

    // Remove user from mapping of user ID to game ID.
    userIdToGameIdMap.remove(user.getId());
    log.debug("Player with ID: {}, has left game with ID: {}.", user.getId(), game.getId());

    // Destroy the emitter sending this user updates.
    try {
      sseService.completeEmitter(EmitterType.Lobby, user.getId());
    } catch (Exception e) {
      log.error("Issue removing the game emitter when player with ID: {}.", user.getId());
    }

    // Add GameActionModel with the appropriate action.
    game.getGameActions()
        .add(
            new GameActionModel(
                UUID.randomUUID(),
                player.get(),
                GameAction.Leave,
                String.format(
                    "%s %s has left the game.",
                    player.get().getFirstName(), player.get().getLastName())));

    // Update the players in the game, and players who are on the join game page.
    sendGameDocumentToAllPlayers(game);

    sseService.sendToAll(EmitterType.GameList, getGameList());
    return new ApiSuccessModel("Player has left the game.");
  }
}
