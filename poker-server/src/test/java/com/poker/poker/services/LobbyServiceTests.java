package com.poker.poker.services;

import com.poker.poker.common.TestBaseClass;
import com.poker.poker.config.AppConfig;
import com.poker.poker.config.constants.GameConstants;
import com.poker.poker.documents.LobbyDocument;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.enums.GameAction;
import com.poker.poker.models.enums.UserGroup;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.repositories.LobbyRepository;
import com.poker.poker.services.game.LobbyService;
import com.poker.poker.validation.exceptions.BadRequestException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LobbyServiceTests extends TestBaseClass {
  @Spy private AppConfig appConfig;
  @Mock private SseService sseService;
  @Spy private Map<UUID, LobbyDocument> activeGames;
  @Spy private Map<UUID, UUID> userIdToGameIdMap;
  @Spy private GameConstants gameConstants;
  @Mock private LobbyRepository lobbyRepository;
  @Mock private WebSocketService webSocketService;

  private LobbyService lobbyService;

  /**
   * Helper which will create a game with a random name and arbitrary host. It will then add the
   * specified number of players to the game. The players will be generated randomly.
   *
   * @param actions Lambda function that will be executed after the game is set up.
   * @param numPlayers The desired number of players which should be added to the game.
   */
  private void withRandomGameWithPlayers(Consumer<List<Object>> actions, int numPlayers) {
    withRandomGame(
        (args) -> {
          for (int i = 0; i < numPlayers; i++) {
            UserDocument newPlayer = randomUserDocument();
            lobbyService.joinLobby((UUID) args.get(2), newPlayer);
            if (args.get(3) instanceof List) {
              ((List<UserDocument>) args.get(3)).add(newPlayer);
            }
          }
          actions.accept(args);
        });
  }

  /**
   * Helper which will create a game with a random name and arbitrary host.
   *
   * @param actions Lambda function that will be executed after the game is created.
   */
  private void withRandomGame(Consumer<List<Object>> actions) {
    final CreateGameModel newGame =
        new CreateGameModel(
            "TestGame_" + randomLetterString(25),
            appConfig.getMaxNumberOfPlayers(),
            new BigDecimal(420));
    withSpecifiedGame(actions, newGame);
  }

  /**
   * Helper which will create a game with a specific game model and arbitrary host.
   *
   * <p>The arguments of the lambda will be:
   *
   * <p>0->CreateGameModel, 1->Host, 2->GameId, 3->List of UserDocuments
   *
   * @param actions Lambda function that will be executed after the game is created.
   * @param newGame CreateGameModel used when creating the game.
   */
  private void withSpecifiedGame(Consumer<List<Object>> actions, CreateGameModel newGame) {
    UserDocument host =
        new UserDocument(
            UUID.randomUUID(), "host@most.com", null, UserGroup.Administrator, "Billy", "Bob");
    withSpecifiedGameAndHost(actions, newGame, host);
  }

  /**
   * Helper which will create a game with a specific CreateGameModel and specific host.
   *
   * @param actions Lambda function that will be executed after the game is created.
   * @param newGame CreateGameModel used when creating the game.
   * @param host The model for the host of the game.
   */
  private void withSpecifiedGameAndHost(
      Consumer<List<Object>> actions, CreateGameModel newGame, UserDocument host) {
    UUID gameId = UUID.randomUUID();
    lobbyService.createLobby(newGame, host, gameId);
    List<Object> arguments =
        Arrays.asList(newGame, host, gameId, new ArrayList<>(Arrays.asList(host)));
    actions.accept(arguments);
  }

  @BeforeEach
  public void setupUuidServiceMock() {
    // Since GameService is a singleton, we need to create a fresh instance each time.
    activeGames = new HashMap<>();
    userIdToGameIdMap = new HashMap<>();

    lobbyService =
        new LobbyService(
            sseService,
            activeGames,
            userIdToGameIdMap,
            gameConstants,
            lobbyRepository,
            webSocketService);
    //    Mockito.when(uuidService.isValidUuidString(Mockito.anyString())).thenCallRealMethod();
    //    Mockito.doAnswer(
    //            (invocation) -> {
    //              if (!uuidService.isValidUuidString(invocation.getArgument(0))) {
    //                throw new BadRequestException("Invalid UUID", "Invalid UUID");
    //              }
    //              return null;
    //            })
    //        .when(uuidService)
    //        .checkIfValidAndThrowBadRequest(Mockito.anyString());
  }

  /* TODO: Update pre/post conditions here after refactoring.
   * Create Game:
   * Pre-conditions:
   *  1) CreateGameModel is valid.
   *  2) UserModel is valid.
   *  // Safe to assume due to validation that is in place.
   *
   * Post-conditions:
   *  1) GameDocument should be created
   *    - ID a random UUID
   *    - Host is UUID of the UserModel specified in argument.
   *    - Game name is what was specified in CreateGameModel.
   *    - Max player is what was specified in CreateGameModel.
   *    - Buy-in is what was specified in CreateGameModel.
   *    - List of players should consist of a PlayerModel representing the host (based on
   *      the UserModel argument that was specified).
   *      - The PlayerModel representing the host should have the host field set to true, ready
   *        field set to false.
   *    - List of game actions should be empty (this might change).
   *    - Game state should be "PreGame".
   *  2) activeGames map should contain the newly created GameDocument, keyed by the ID.
   *  3) userIdToGameIdMap should contain the game ID, keyed by the host's ID.
   */

  @Test
  public void testCreateGame_succeeds_whenParametersAreValid() {
    // Given
    final UserDocument host = getUserDocument();
    final CreateGameModel newGame = getSampleCreateGameModel();
    final UUID gameId = UUID.randomUUID();

    // When
    lobbyService.createLobby(newGame, host, gameId);

    // Should only be 1 user is a game and 1 active game.
    Assertions.assertEquals(1, userIdToGameIdMap.size());
    Assertions.assertEquals(1, activeGames.size());

    // Returned UUID should be the UUID of the game that was created.
    final LobbyDocument game = activeGames.get(gameId);
    Assertions.assertNotNull(game);

    // Verify GameDocument fields
    Assertions.assertEquals(host.getId(), game.getHost());
    Assertions.assertEquals(newGame.getName(), game.getName());
    Assertions.assertEquals(newGame.getMaxPlayers(), game.getMaxPlayers());
    Assertions.assertEquals(newGame.getBuyIn(), game.getBuyIn());
    Assertions.assertEquals(1, game.getPlayers().size());
    Assertions.assertEquals(host.getId(), game.getPlayers().get(0).getId());
    Assertions.assertTrue(game.getPlayers().get(0).isHost());
    Assertions.assertFalse(game.getPlayers().get(0).isReady());
    Assertions.assertTrue(game.getGameActions().isEmpty());

    // Player ID should be associated with the game UUID
    Assertions.assertNotNull(userIdToGameIdMap.get(host.getId()));
    Assertions.assertEquals(game.getId(), userIdToGameIdMap.get(host.getId()));
  }

  @Test
  public void testCreateGame_fails_whenHostIsAlreadyInGame() {
    // Given
    final UserDocument host = getUserDocument();
    final CreateGameModel newGame = getSampleCreateGameModel();
    final UUID gameId = UUID.randomUUID();
    lobbyService.createLobby(newGame, host, gameId);

    // When/Then
    Assertions.assertThrows(
        BadRequestException.class, () -> lobbyService.createLobby(newGame, host, gameId));
    // Verify that the game wasn't created and there are no userId->gameId mappings for the game.
    Assertions.assertEquals(1, activeGames.size());
    Assertions.assertEquals(1, userIdToGameIdMap.size());
    activeGames.values().forEach(game -> Assertions.assertEquals(gameId, game.getId()));
    userIdToGameIdMap.values().forEach(id -> Assertions.assertEquals(gameId, id));
  }

  /*
   * Join Game:
   * Pre-conditions:
   *  1) UserDocument refers to a valid user.
   *  2) Game ID is valid (validated in GameService).
   *
   * Post-conditions:
   *  1) BadRequestException is thrown if gameId is invalid UUID.
   *  2) ApiSuccessModel is returned if UserDocument refers to a user that is already in the game.
   *  3) BadRequestException is thrown if UserDocument refers to a user that is in another game.
   *  4) BadRequestException is thrown if there is no game with the gameId provided.
   *  5) PlayerModel is created and added to list of players in the GameDocument associated with
   *     the game.
   *  6) GameActionModel is created, specifying that a player joined the game and added to the list
   *     of game actions in the GameDocument associated with the game.
   *  7) userIdToGameMap is updated to reflect the fact that the user associated with the
   *     UserDocument argument has joined a game.
   *  8) Updated GameDocuments are sent out to clients who have requested a game update SSE emitter.
   *  9) Updated GetGameModels are sent out to client who have requested a game list SSE emitter.
   *  10) ApiSuccessModel is returned, letting the client know that the player joined successfully.
   */

  @Test
  public void testJoinGame_succeeds_whenArgumentsValid() {
    // Assuming that createGame function works correctly here.
    // Given
    final UUID gameId = UUID.randomUUID();
    lobbyService.createLobby(getSampleCreateGameModel(), getUserDocument(), gameId);

    final UserDocument user = new UserDocument();
    user.setId(UUID.randomUUID());

    // When
    ApiSuccessModel result = lobbyService.joinLobby(gameId, user);

    // Then
    LobbyDocument game = activeGames.get(gameId);
    Assertions.assertEquals(user.getId(), game.getPlayers().get(1).getId());
    Assertions.assertEquals(GameAction.Join, game.getGameActions().get(0).getGameAction());
    Assertions.assertEquals(user.getId(), game.getGameActions().get(0).getPlayer().getId());
    Assertions.assertEquals(2, userIdToGameIdMap.size());
    Assertions.assertEquals(game.getId(), userIdToGameIdMap.get(user.getId()));

    // Check that result is not null.
    Assertions.assertNotNull(result);
  }

  // TODO: Replace this test.
  //  @Test
  //  public void testJoinGame_fails_whenArgumentsInvalid() {
  //    // Given
  //    final UUID badId = UUID.randomUUID();
  //    final UUID realGameId = UUID.randomUUID();
  //    lobbyService.createLobby(getSampleCreateGameModel(), getUserDocument(), realGameId);
  //    final UserDocument user = new UserDocument();
  //    user.setId(UUID.randomUUID());
  //
  //    // When/Then
  //    // Check that join game throws a bad request exception when attempting to join with a bad
  // ID.
  //    Assertions.assertThrows(BadRequestException.class, () -> lobbyService.joinLobby(badId,
  // user));
  //    // Check that only the user that created the game is in a game.
  //    userIdToGameIdMap
  //        .keySet()
  //        .forEach(id -> Assertions.assertEquals(getUserDocument().getId(), id));
  //    Assertions.assertEquals(1, activeGames.get(realGameId).getPlayers().size());
  //  }

  @Test
  public void testReady_succeeds_whenPlayerIsInGame() {
    withRandomGameWithPlayers(
        (args) -> {
          lobbyService.ready((UserDocument) args.get(1));
          Assertions.assertTrue(activeGames.get(args.get(2)).getPlayers().get(0).isReady());
        },
        2);
  }

  @Test
  public void testReady_fails_whenPlayerIsNotInGame() {
    withRandomGameWithPlayers(
        (args) ->
            Assertions.assertThrows(
                BadRequestException.class, () -> lobbyService.ready(randomUserDocument())),
        2);
  }

  @Test
  public void testReady_fails_whenPlayerNotInPlayerList() {
    withRandomGameWithPlayers(
        (args) -> {
          LobbyDocument game = activeGames.get(args.get(2));
          List<UserDocument> users = (List<UserDocument>) args.get(3);
          UserDocument user = users.get(1);
          game.getPlayers().removeIf(player -> player.getId().equals(user.getId()));
          Assertions.assertThrows(BadRequestException.class, () -> lobbyService.ready(user));
        },
        2);
  }
}
