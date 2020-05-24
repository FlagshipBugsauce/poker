package com.poker.poker.services.game;

import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.documents.GameDocument;
import com.poker.poker.documents.LobbyDocument;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.enums.EmitterType;
import com.poker.poker.models.enums.GameAction;
import com.poker.poker.models.enums.GameState;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.models.game.GameActionModel;
import com.poker.poker.models.game.GamePlayerModel;
import com.poker.poker.models.game.GetGameModel;
import com.poker.poker.models.game.LobbyPlayerModel;
import com.poker.poker.repositories.LobbyRepository;
import com.poker.poker.services.SseService;
import com.poker.poker.validation.exceptions.BadRequestException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LobbyService {

  private SseService sseService;

  /** A map of active games, keyed by the games ID. */
  private Map<UUID, LobbyDocument> lobbys;

  /**
   * A map of game UUID's, keyed by user UUID's, to identify which game a user is currently in, and
   * also to identify whether a user is currently in a game, in order to impose a one game limit at
   * any given time.
   */
  private Map<UUID, UUID> userIdToLobbyIdMap;

  private GameConstants gameConstants;

  private LobbyRepository lobbyRepository;

  public Runnable getEmitterValidator(UUID userId) {
    return () -> {
      log.debug("Performing validation to ensure {} should receive an emitter.", userId);
      checkWhetherUserIsInLobbyAndThrow(userId, true);
      checkUserIsPlayerInLobby(userId);
    };
  }

  /**
   * Sends out a game document to all players in the game associated with the game document
   * argument.
   *
   * @param lobbyDocument GameDocument representing the game that is being updated.
   */
  private void sendLobbyDocumentToAllPlayers(LobbyDocument lobbyDocument) {
    for (LobbyPlayerModel player : lobbyDocument.getPlayers()) {
      try {
        sseService.sendUpdate(EmitterType.Lobby, player.getId(), lobbyDocument);
      } catch (BadRequestException ignored) { // Exception already logged.
      }
    }
  }

  /**
   * Performs necessary actions to start the game.
   *
   * @param gameDocument ID of the game which is starting.
   * @throws BadRequestException If there is no lobby associated with the specified game ID.
   */
  public void startGame(GameDocument gameDocument) throws BadRequestException {
    LobbyDocument lobbyDocument = lobbys.remove(gameDocument.getId());
    if (lobbyDocument == null) {
      throw gameConstants.getGameNotFoundException();
    }
    // Remove players from userIdToLobbyIdMap
    lobbyDocument.getPlayers().forEach(player -> userIdToLobbyIdMap.remove(player.getId()));

    // Add players to game document, then shuffle the players.
    gameDocument.setPlayers(lobbyDocument
        .getPlayers()
        .stream()
        .map(GamePlayerModel::new)
        .collect(Collectors.toList()));
    Collections.shuffle(gameDocument.getPlayers());

    lobbyRepository.save(lobbyDocument);
  }

  /**
   * Helper to retrieve a lobby document. Throws if there is no document with the ID provided.
   *
   * @param lobbyId The ID of the lobby document being sought.
   * @return LobbyDocument associated with the ID provided if it exists, otherwise, throws.
   */
  public LobbyDocument getLobbyDocument(UUID lobbyId) {
    if (lobbys.get(lobbyId) == null) {
      throw gameConstants.getGameNotFoundException();
    }
    return lobbys.get(lobbyId);
  }

  /**
   * Helper to do determine whether a user is in a game.
   *
   * @param userId ID of the user.
   * @return True if user is in some game, false otherwise.
   */
  public boolean isUserInLobby(UUID userId) {
    return userIdToLobbyIdMap.get(userId) != null;
  }

  /**
   * Helper to retrieve the game document associated with the game a specified user is in.
   *
   * @param userId ID of the user.
   * @return GameDocument of the game the user is in.
   */
  public LobbyDocument getUsersLobbyDocument(UUID userId) {
    // TODO: May need some validation here.
    return lobbys.get(userIdToLobbyIdMap.get(userId));
  }

  /**
   * Helper to determine whether a specified user is listed as one of the players in a game.
   *
   * @param userId ID of the user.
   */
  public void checkUserIsPlayerInLobby(UUID userId) {
    LobbyDocument lobbyDocument = getUsersLobbyDocument(userId);
    if (lobbyDocument == null) {
      log.error("There is no game associated with user {}.", userId);
      throw gameConstants.getInvalidUuidException();
    }
    if (lobbyDocument.getPlayers().stream().noneMatch(p -> p.getId().equals(userId))) {
      log.error("User {} is not listed as a player of game {}.", lobbyDocument.getId(), userId);
      throw gameConstants.getUserNotInGameException();
    }
  }

  /**
   * Updates the lobby document associated with the lobby a player is in, to indicate that the
   * player is ready for the game to start.
   *
   * @param user The user who is ready.
   * @return ApiSuccessModel indicating the request was successful.
   */
  public ApiSuccessModel ready(UserDocument user) {
    if (!isUserInLobby(user.getId()) || getUsersLobbyDocument(user.getId()) == null) {
      log.error("Failed to set player's status to ready (user ID: {}).", user.getId());
      throw gameConstants.getReadyStatusUpdateFailException();
    }

    // Get the game document for the game now that we're sure it exists.
    LobbyDocument lobbyDocument = getUsersLobbyDocument(user.getId());

    // Find the playerModel, throw if not found, otherwise, remove player from game.
    final Optional<LobbyPlayerModel> player =
        lobbyDocument.getPlayers().stream()
            .filter(playerModel -> playerModel.getId().equals(user.getId()))
            .findFirst();
    if (!player.isPresent()) {
      log.error("Failed to set player's status to ready (user ID: {}).", user.getId().toString());
      throw gameConstants.getReadyStatusUpdateFailException();
    }

    // Set players status to ready.
    player.get().setReady(true);

    // Add the appropriate game action model.
    lobbyDocument
        .getGameActions()
        .add(
            new GameActionModel(
                UUID.randomUUID(),
                player.get(),
                GameAction.Ready,
                String.format(
                    "%s %s is ready.", player.get().getFirstName(), player.get().getLastName())));

    log.debug("Player status set to ready (ID: {}).", user.getId().toString());

    sendLobbyDocumentToAllPlayers(lobbyDocument);
    return new ApiSuccessModel("Player status set to ready.");
  }

  /**
   * Checks whether a user is in/not in a game and throws. If the boolean argument in is true, then
   * this method will throw if the user IS NOT in a game. If the boolean argument is false, then
   * this method will throw if the user IS in a game.
   *
   * @param userId ID of the user being checked.
   * @param in Flag to determine whether the user should or should not, be in a game.
   */
  public void checkWhetherUserIsInLobbyAndThrow(final UUID userId, final boolean in) {
    if (userIdToLobbyIdMap.get(userId) == null && in) {
      throw gameConstants.getUserNotInGameException();
    } else if (userIdToLobbyIdMap.get(userId) != null && !in) {
      throw gameConstants.getJoinGamePlayerAlreadyJoinedException();
    }
  }

  /**
   * Creates a new game document based on attributes given in createGameModel.
   *
   * @param createGameModel A model containing: name, maximum players, and buy in.
   * @param user the user document of the player creating the game.
   * @return a UUID, the unique id for the game document created in this method.
   */
  public void createLobby(CreateGameModel createGameModel, UserDocument user, UUID id) {
    checkWhetherUserIsInLobbyAndThrow(user.getId(), false);
    LobbyDocument lobbyDocument =
        new LobbyDocument(
            id,
            user.getId(),
            createGameModel.getName(),
            createGameModel.getMaxPlayers(),
            createGameModel.getBuyIn(),
            new ArrayList<>(Arrays.asList(new LobbyPlayerModel(user, false, true))),
            new ArrayList<>());
    log.info("User: {} created a game.", user.getId());
    lobbys.put(lobbyDocument.getId(), lobbyDocument);
    userIdToLobbyIdMap.put(user.getId(), lobbyDocument.getId());

    sseService.sendToAll(EmitterType.GameList, getLobbyList());
  }

  /**
   * Gets the list of active game models that are currently in the PreGame state.
   *
   * @return An ActiveGameModel which is a subset of a game document.
   */
  public List<GetGameModel> getLobbyList() {
    List<GetGameModel> lobbyListModels = new ArrayList<>();
    for (LobbyDocument lobbyDocument : lobbys.values()) {
      lobbyListModels.add(
          new GetGameModel(
              lobbyDocument.getId(),
              lobbyDocument.getName(),
              lobbyDocument.getPlayers().stream()
                  .filter(p -> p.getId().equals(lobbyDocument.getHost()))
                  .findFirst()
                  .get(),
              lobbyDocument.getPlayers().size(),
              lobbyDocument.getMaxPlayers(),
              lobbyDocument.getBuyIn()));
    }
    return lobbyListModels;
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
  public ApiSuccessModel joinLobby(UUID gameId, UserDocument user) {
    // Find the active game you wish to join
    LobbyDocument lobbyDocument = lobbys.get(gameId);

    // Add new player to list of players in currently in the game.
    final LobbyPlayerModel lobbyPlayerModel = new LobbyPlayerModel(user, false, false);
    lobbyDocument.getPlayers().add(lobbyPlayerModel);

    // Add the appropriate game action model.
    lobbyDocument
        .getGameActions()
        .add(
            new GameActionModel(
                UUID.randomUUID(),
                lobbyPlayerModel,
                GameAction.Join,
                String.format(
                    "%s %s has joined the game.",
                    lobbyPlayerModel.getFirstName(), lobbyPlayerModel.getLastName())));
    userIdToLobbyIdMap.put(user.getId(), lobbyDocument.getId());

    // TODO: When players join at the same time, this iterator gets confused and throws a ConcurrentModificationException
    // Update all players copy of gameDocument who are in the game via SSE
    try {
      for (LobbyPlayerModel player : lobbyDocument.getPlayers()) {
        if (!player.getId().equals(user.getId())) {
            sseService.sendUpdate(EmitterType.Lobby, player.getId(), lobbyDocument);
        }
      }
    } catch (Exception ignored) { // Exception is already logged.
      // TODO: This will be catching the ConcurrentModificationException... This needs to be fixed.
    }

    sseService.sendToAll(EmitterType.GameList, getLobbyList());
    return new ApiSuccessModel("User joined the game successfully.");
  }

  /**
   * Removes a player from a game. This will be called when a player leaves, or when the host kicks
   * a player from the game.
   *
   * @param user The user to be removed.
   * @return ApiSuccessModel indicating that the request was completed successfully.
   */
  public ApiSuccessModel removePlayerFromLobby(UserDocument user) {
    UUID gameId = userIdToLobbyIdMap.get(user.getId());
    if (gameId == null) {
      throw gameConstants.getLeaveGameException();
    }

    LobbyDocument game = lobbys.get(gameId);
    if (game == null) {
      throw gameConstants.getLeaveGameException();
    }

    // Find the playerModel, throw if not found, otherwise, remove player from game.
    final Optional<LobbyPlayerModel> player =
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
      lobbys.remove(game.getId());
      log.debug("No players left in game: {}, removing game.", game.getId());
    }

    // Remove user from mapping of user ID to game ID.
    userIdToLobbyIdMap.remove(user.getId());
    log.debug("Player with ID: {}, has left game with ID: {}.", user.getId(), game.getId());

    // Destroy the emitter sending this user updates.
    try {
      sseService.completeEmitter(EmitterType.Lobby, user.getId());
    } catch (Exception e) {
      log.error("Issue removing the lobby emitter when player with ID: {}.", user.getId());
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
    sendLobbyDocumentToAllPlayers(game);

    sseService.sendToAll(EmitterType.GameList, getLobbyList());
    return new ApiSuccessModel("Player has left the game.");
  }
}
