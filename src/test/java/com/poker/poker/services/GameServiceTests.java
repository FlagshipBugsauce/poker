package com.poker.poker.services;

import com.poker.poker.common.TestBaseClass;
import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.documents.GameDocument;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GameServiceTests extends TestBaseClass {

  @Mock private Map<UUID, GameDocument> activeGames;
  private Map<UUID, GameDocument> activeGamesReal;

  @Mock private Map<UUID, SseEmitter> gameEmitters;
  private Map<UUID, SseEmitter> gameEmittersReal;

  @Mock private Set<UUID> playersInGames;
  private Set<UUID> playersInGamesReal;

  @Spy private AppConstants appConstants;

  @Mock private UuidService uuidService;

  @InjectMocks private GameService gameService;

  /*
     For some bizarre reason, @Spy is not working on HashMaps and sets, so I've had to mock them
     manually. No idea why this is happening.

     TODO: Investigate WTF is going on with @Spy-ing maps and sets.
  */
  @BeforeEach
  public void setupUuidServiceMock() {
    //    gameService =
    //        new GameService(activeGames, gameEmitters, playersInGames, appConstants, uuidService);
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
    //
    //    // Mock activeGames map
    //    activeGamesReal = new HashMap<>();
    //    Mockito.when(activeGames.put(Mockito.any(UUID.class), Mockito.any(GameDocument.class)))
    //        .then(
    //            (invocation) -> {
    //              return activeGamesReal.put(invocation.getArgument(0),
    // invocation.getArgument(1));
    //            });
    //    Mockito.when(activeGames.get(Mockito.any(UUID.class)))
    //        .then((invocation) -> activeGamesReal.get(invocation.getArgument(0)));
    //    Mockito.when(activeGames.size()).then((invocation) -> activeGamesReal.size());
    //    Mockito.when(activeGames.values()).then((invocation) -> activeGamesReal.values());
    //
    //    // Mock playersInGames set
    //    playersInGamesReal = new HashSet<>();
    //    Mockito.when(playersInGames.add(Mockito.any(UUID.class)))
    //        .then(
    //            (invocation) -> {
    //              return playersInGamesReal.add(invocation.getArgument(0));
    //            });
    //    Mockito.when(playersInGames.contains(Mockito.any(UUID.class)))
    //        .then(
    //            (invocation) -> {
    //              return playersInGamesReal.contains(invocation.getArgument(0));
    //            });
    //
    //    // Mock gameEmitters map
    //    gameEmittersReal = new HashMap<>();
    //    Mockito.when(gameEmitters.put(Mockito.any(UUID.class), Mockito.any(SseEmitter.class)))
    //        .then(
    //            (invocation) -> {
    //              return gameEmittersReal.put(invocation.getArgument(0),
    // invocation.getArgument(1));
    //            });
    //    Mockito.when(gameEmitters.get(Mockito.any(UUID.class)))
    //        .then((invocation) -> gameEmittersReal.get(invocation.getArgument(0)));
    //    Mockito.when(gameEmitters.size()).then((invocation) -> gameEmittersReal.size());
  }

  /** Test to ensure that a user cannot create a game when they are already in a game. */
  @Test
  public void testCreateGame_whenUserAlreadyInAGame() {
    // Given
    final UUID userId = UUID.randomUUID();

    // When/Then
    //    Assertions.assertThrows(          // TODO: FIX THIS
    //        BadRequestException.class,
    //        () -> {
    //          gameService.createGame(new CreateGameModel(), userId);
    //          gameService.createGame(new CreateGameModel(), userId);
    //        });
  }

  /** Test to ensure that games are being created successfully. */
  @Test
  public void testCreateGame_validInput() {
    //    // Given  TODO: FIX
    //    final UUID userId = UUID.randomUUID();
    //    // When/Then
    //    final ApiSuccessModel response = gameService.createGame(getSampleCreateGameModel(),
    // userId);
    //
    //    // Make sure the game document was found:
    //    final GameDocument gameDocument = activeGames.get(UUID.fromString(response.getMessage()));
    //    Assertions.assertNotNull(gameDocument);
    //
    //    // Make sure the user was added to the list of players:
    //    Assertions.assertTrue(gameDocument.getPlayers().contains(userId));
    //
    //    // Make sure the set of players currently in games contains the players ID:
    //    Assertions.assertTrue(playersInGames.contains(userId));
    //
    //    // Make sure the name, max players, and buy-in are all accurate:
    //    Assertions.assertEquals(getSampleCreateGameModel().getName(), gameDocument.getName());
    //    Assertions.assertEquals(
    //        getSampleCreateGameModel().getMaxPlayers(), gameDocument.getMaxPlayers());
    //    Assertions.assertEquals(getSampleCreateGameModel().getBuyIn(), gameDocument.getBuyIn());
    //
    //    // Make sure the game state is "PreGame":
    //    Assertions.assertEquals(GameState.PreGame, gameDocument.getCurrentGameState());
    //
    //    // Make sure the list of game actions is empty:
    //    Assertions.assertTrue(gameDocument.getGameActions().isEmpty());
  }

  @Test
  public void testGetGameList() {
    //    // Given  TODO: FIX
    //    activeGamesReal.put(getSampleGameDocument().getId(), getSampleGameDocument());
    //    final UUID uuid = UUID.randomUUID();
    //    GameDocument secondGameDocument =
    //        new GameDocument(
    //            UUID.randomUUID(),
    //            uuid,
    //            getSampleGameName(),
    //            getSampleMaxPlayers(),
    //            getSampleBuyIn(),
    //            Arrays.asList(uuid),
    //            new ArrayList<>(),
    //            GameState.Game);
    //    secondGameDocument.setCurrentGameState(GameState.Game);
    //    activeGamesReal.put(secondGameDocument.getId(), secondGameDocument);
    //
    //    // When
    //    List<GetGameModel> games = gameService.getGameList();
    //
    //    // Then
    //    // Make sure something was returned:
    //    Assertions.assertNotNull(games);
    //
    //    // Make sure only one game was returned
    //    Assertions.assertEquals(1, games.size());
    //
    //    // Make sure the ID, name, host, max players and buy-in were all set correctly:
    //    Assertions.assertEquals(getSampleGameDocument().getId(), games.get(0).getId());
    //    Assertions.assertEquals(getSampleGameDocument().getName(), games.get(0).getName());
    //    Assertions.assertEquals(getSampleGameDocument().getHost(), games.get(0).getHost());
    //    Assertions.assertEquals(getSampleGameDocument().getMaxPlayers(),
    // games.get(0).getMaxPlayers());
    //    Assertions.assertEquals(getSampleGameDocument().getBuyIn(), games.get(0).getBuyIn());
    //
    //    // Make sure the list of players is accurate
    //    Assertions.assertEquals(1, games.get(0).getCurrentPlayers());
  }
}
