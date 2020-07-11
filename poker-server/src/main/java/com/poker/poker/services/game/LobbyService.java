package com.poker.poker.services.game;

import com.poker.poker.config.AppConfig;
import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.events.JoinGameEvent;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.enums.MessageType;
import com.poker.poker.models.game.GameListModel;
import com.poker.poker.models.game.GameModel;
import com.poker.poker.models.game.GameParameterModel;
import com.poker.poker.models.game.GamePlayerModel;
import com.poker.poker.models.game.LobbyModel;
import com.poker.poker.models.game.LobbyPlayerModel;
import com.poker.poker.models.websocket.SocketContainerModel;
import com.poker.poker.repositories.LobbyRepository;
import com.poker.poker.services.WebSocketService;
import com.poker.poker.validation.exceptions.BadRequestException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LobbyService {

  private final AppConfig appConfig;

  /** A map of active games, keyed by the games ID. */
  private final Map<UUID, LobbyModel> lobbys;

  /**
   * A map of game UUID's, keyed by user UUID's, to identify which game a user is currently in, and
   * also to identify whether a user is currently in a game, in order to impose a one game limit at
   * any given time.
   */
  private final Map<UUID, UUID> userIdToLobbyIdMap;

  private final GameConstants gameConstants;

  private final LobbyRepository lobbyRepository;

  private final WebSocketService webSocketService;

  private boolean useCachedGameList = false;

  private List<GameListModel> gameList;

  public LobbyService(
      final AppConfig appConfig,
      final GameConstants gameConstants,
      final LobbyRepository lobbyRepository,
      final WebSocketService webSocketService) {
    this.appConfig = appConfig;
    this.lobbys = new HashMap<>();
    this.userIdToLobbyIdMap = new HashMap<>();
    this.gameConstants = gameConstants;
    this.lobbyRepository = lobbyRepository;
    this.webSocketService = webSocketService;
  }

  /**
   * Sends out a game document to all players in the game associated with the game document
   * argument.
   *
   * @param lobbyModel GameDocument representing the game that is being updated.
   */
  private void broadcastLobbyModel(final LobbyModel lobbyModel) {
    // Broadcast lobby document to lobby topic.
    webSocketService.sendPublicMessage(
        appConfig.getGameTopic() + lobbyModel.getId(),
        new SocketContainerModel(MessageType.Lobby, lobbyModel));
  }

  /**
   * Every 10 seconds, server will broadcast a complete lobby model to all players subscribed to the
   * lobby topic.
   */
  @Scheduled(cron = "0/10 * * * * ?")
  public void broadcastLobbyUpdate() {
    lobbys.values().forEach(this::broadcastLobbyModel);
  }

  /** Every 3 seconds, broadcasts the list of joinable games to the game list topic. */
  @Scheduled(cron = "0/3 * * * * ?")
  public void broadcastGameList() {
    webSocketService.sendPublicMessage(
        appConfig.getGameListTopic(),
        new SocketContainerModel(
            MessageType.GameList, useCachedGameList ? gameList : getLobbyList()));
  }

  /**
   * Performs necessary actions to start the game.
   *
   * @param gameModel ID of the game which is starting.
   * @throws BadRequestException If there is no lobby associated with the specified game ID.
   */
  public void startGame(GameModel gameModel) throws BadRequestException {
    final LobbyModel lobbyModel = lobbys.remove(gameModel.getId());
    if (lobbyModel == null) {
      throw gameConstants.getGameNotFoundException();
    }

    // Check if all players are ready.
    for (LobbyPlayerModel player : lobbyModel.getPlayers()) {
      if (!player.isReady()) {
        log.error("Host attempted to start the game, but all players were not ready.");
        lobbys.put(gameModel.getId(), lobbyModel); // Add the mapping back.
        throw gameConstants.getPlayerNotReadyException();
      }
    }

    // Remove players from userIdToLobbyIdMap
    lobbyModel.getPlayers().forEach(player -> userIdToLobbyIdMap.remove(player.getId()));

    // Add players to game document, then shuffle the players.
    gameModel.setPlayers(
        lobbyModel.getPlayers().stream().map(GamePlayerModel::new).collect(Collectors.toList()));
    Collections.shuffle(gameModel.getPlayers());
    gameModel.getPlayers().get(0).setActing(true);

    // Send to game list topic
    useCachedGameList = false;
    webSocketService.sendGameToast(lobbyModel.getId(), "The game has started!", "lg");

    lobbyRepository.save(lobbyModel);
  }

  /**
   * Helper to retrieve a lobby document. Throws if there is no document with the ID provided.
   *
   * @param lobbyId The ID of the lobby document being sought.
   * @return LobbyDocument associated with the ID provided if it exists, otherwise, throws.
   */
  public LobbyModel getLobbyModel(UUID lobbyId) {
    if (lobbys.get(lobbyId) == null) {
      throw gameConstants.getLobbyNotFoundException();
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
  public LobbyModel getUsersLobbyModel(UUID userId) {
    if (!isUserInLobby(userId)) {
      throw gameConstants.getNoUserIdToLobbyIdMappingFound();
    }
    return getLobbyModel(userIdToLobbyIdMap.get(userId));
  }

  /**
   * Helper to determine whether a specified user is listed as one of the players in a game.
   *
   * @param userId ID of the user.
   */
  public void checkUserIsPlayerInLobby(final UUID userId) {
    final LobbyModel lobbyModel = getUsersLobbyModel(userId);
    if (lobbyModel == null) {
      log.error("There is no game associated with user {}.", userId);
      throw gameConstants.getInvalidUuidException();
    }
    if (lobbyModel.getPlayers().stream().noneMatch(p -> p.getId().equals(userId))) {
      log.error("User {} is not listed as a player of game {}.", lobbyModel.getId(), userId);
      throw gameConstants.getUserNotInGameException();
    }
  }

  /**
   * Toggles a players ready status. If the player is currently not ready, their status will be set
   * to ready. If the player is currently ready, their status will be set to not ready.
   *
   * @param user The user.
   * @return ApiSuccessModel indicating the request was successful.
   */
  public ApiSuccessModel ready(final UserDocument user) throws InterruptedException {
    // TODO: Investigate whether this can be removed, issue doesn't seem to be occurring any more.
    if (!isUserInLobby(user.getId()) || getUsersLobbyModel(user.getId()) == null) {
      log.error(
          "Failed to set player's status to ready (user ID: {}), trying again.", user.getId());
      Thread.sleep(1000);
      if (!isUserInLobby(user.getId()) || getUsersLobbyModel(user.getId()) == null) {
        log.error(
            "Failed to set player's status to ready (user ID: {}) after second attempt.",
            user.getId());
        log.error("!isUserInLobby(user.getId()): {}", !isUserInLobby(user.getId()));
        log.error(
            "getUsersLobbyDocument(user.getId()) == null): {}",
            getUsersLobbyModel(user.getId()) == null);
        throw gameConstants.getReadyStatusUpdateFailException();
      }
    }

    // Get the game document for the game now that we're sure it exists.
    final LobbyModel lobbyModel = getUsersLobbyModel(user.getId());

    // Find the playerModel, throw if not found, otherwise, remove player from game.
    final Optional<LobbyPlayerModel> player =
        lobbyModel.getPlayers().stream()
            .filter(playerModel -> playerModel.getId().equals(user.getId()))
            .findFirst();
    if (!player.isPresent()) {
      log.error("Failed to set player's status to ready (user ID: {}).", user.getId().toString());
      throw gameConstants.getReadyStatusUpdateFailException();
    }

    // Toggle the players ready status
    player.get().setReady(!player.get().isReady());
    final String message =
        String.format(
            "%s %s %s ready.",
            player.get().getFirstName(),
            player.get().getLastName(),
            player.get().isReady() ? "is" : "is not");

    webSocketService.sendGameToast(lobbyModel.getId(), message, "md");
    log.debug("Player status set to ready (ID: {}).", user.getId().toString());
    webSocketService.sendPublicMessage(
        "/topic/game/" + lobbyModel.getId(),
        new SocketContainerModel(MessageType.ReadyToggled, player.get()));
    return new ApiSuccessModel("Player ready status was toggled.");
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
   * @param gameParameterModel A model containing: name, maximum players, and buy in.
   * @param user the user document of the player creating the game.
   */
  public void createLobby(
      final GameParameterModel gameParameterModel, final UserDocument user, final UUID id) {
    checkWhetherUserIsInLobbyAndThrow(user.getId(), false);
    final LobbyPlayerModel host = new LobbyPlayerModel(user, false, true);
    final List<LobbyPlayerModel> players =
        Collections.synchronizedList(new ArrayList<>(Collections.singletonList(host)));
    final LobbyModel lobbyModel = new LobbyModel(id, host, gameParameterModel, players);
    log.info("User: {} created a game.", user.getId());
    lobbys.put(lobbyModel.getId(), lobbyModel);
    userIdToLobbyIdMap.put(user.getId(), lobbyModel.getId());
    useCachedGameList = false;
  }

  /**
   * Gets the list of active game models that are currently in the PreGame state.
   *
   * @return An ActiveGameModel which is a subset of a game document.
   */
  public List<GameListModel> getLobbyList() {
    gameList = new ArrayList<>();
    for (final LobbyModel lobbyModel : lobbys.values()) {
      gameList.add(
          new GameListModel(
              lobbyModel.getId(),
              lobbyModel.getParameters(),
              lobbyModel.getHost(),
              lobbyModel.getPlayers().size()));
    }
    useCachedGameList = true;
    return gameList;
  }

  /**
   * Listener that joins a game lobby.
   *
   * @param joinGameEvent Event that triggers joining a lobby.
   */
  @EventListener
  public void joinLobby(final JoinGameEvent joinGameEvent) {

    final UUID gameId = joinGameEvent.getGameId();
    final UserDocument user = joinGameEvent.getUser();
    final LobbyModel lobbyModel = lobbys.get(gameId);
    final LobbyPlayerModel player = new LobbyPlayerModel(user, false, false);
    lobbyModel.getPlayers().add(player);
    final String toastMessage =
        String.format("%s %s has joined the game.", player.getFirstName(), player.getLastName());

    log.debug("Adding user {} to userIdToLobbyMap.", user.getId());
    userIdToLobbyIdMap.put(user.getId(), lobbyModel.getId());

    // Send toast, tell client which player joined and indicate that cached game list is outdated.
    webSocketService.sendGameToast(lobbyModel.getId(), toastMessage, "md"); // Also broadcast toast.
    webSocketService.sendPublicMessage(
        "/topic/game/" + lobbyModel.getId(),
        new SocketContainerModel(MessageType.PlayerJoinedLobby, player));
    useCachedGameList = false;
  }

  /**
   * Removes a player from a game. This will be called when a player leaves, or when the host kicks
   * a player from the game.
   *
   * @param user The user to be removed.
   * @return ApiSuccessModel indicating that the request was completed successfully.
   */
  public ApiSuccessModel removePlayerFromLobby(final UserDocument user) {
    UUID gameId = userIdToLobbyIdMap.get(user.getId());
    if (gameId == null) {
      throw gameConstants.getLeaveGameException();
    }

    final LobbyModel lobby = lobbys.get(gameId);
    if (lobby == null) {
      throw gameConstants.getLeaveGameException();
    }

    // Find the playerModel, throw if not found, otherwise, remove player from lobby.
    final Optional<LobbyPlayerModel> player =
        lobby.getPlayers().stream()
            .filter(playerModel -> playerModel.getId().equals(user.getId()))
            .findFirst();
    if (!player.isPresent()) {
      throw gameConstants.getLeaveGameException();
    }
    lobby.getPlayers().removeIf(playerModel -> playerModel.getId().equals(user.getId()));

    if (lobby.getPlayers().size() > 0) {
      // Select another host.
      lobby.setHost(lobby.getPlayers().get(0));
      lobby.getPlayers().get(0).setHost(true);
      log.debug("Changing host from: {}, to: {}.", user.getId(), lobby.getHost());
    } else {
      // The lobby is empty, so remove it.
      lobbys.remove(lobby.getId());
      log.debug("No players left in lobby: {}, removing lobby.", lobby.getId());
    }

    // Remove user from mapping of user ID to lobby ID.
    userIdToLobbyIdMap.remove(user.getId());
    log.debug("Player with ID: {}, has left lobby with ID: {}.", user.getId(), lobby.getId());

    final String toastMessage =
        String.format(
            "%s %s has left the lobby.", player.get().getFirstName(), player.get().getLastName());

    // Send game toast, tell client which player left, and indicate cached game list is outdated.
    webSocketService.sendGameToast(lobby.getId(), toastMessage, "md");
    webSocketService.sendPublicMessage(
        "/topic/game/" + lobby.getId(),
        new SocketContainerModel(MessageType.PlayerLeftLobby, player));
    useCachedGameList = false;

    return new ApiSuccessModel("Player has left the lobby.");
  }
}
