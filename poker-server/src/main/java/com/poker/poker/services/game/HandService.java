package com.poker.poker.services.game;

import com.poker.poker.config.AppConfig;
import com.poker.poker.config.constants.HandConstants;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.events.HandActionEvent;
import com.poker.poker.events.PlayerAfkEvent;
import com.poker.poker.events.PublishMessageEvent;
import com.poker.poker.events.SystemChatMessageEvent;
import com.poker.poker.events.WaitForPlayerEvent;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.enums.HandAction;
import com.poker.poker.models.enums.MessageType;
import com.poker.poker.models.game.CardModel;
import com.poker.poker.models.game.DeckModel;
import com.poker.poker.models.game.GameModel;
import com.poker.poker.models.game.GamePlayerModel;
import com.poker.poker.models.game.HandModel;
import com.poker.poker.models.game.PlayerModel;
import com.poker.poker.models.game.hand.HandActionModel;
import com.poker.poker.models.websocket.GenericServerMessage;
import com.poker.poker.repositories.HandRepository;
import com.poker.poker.repositories.UserRepository;
import com.poker.poker.services.WebSocketService;
import com.poker.poker.validation.exceptions.BadRequestException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class HandService {

  private final AppConfig appConfig;

  private final HandConstants handConstants;

  /** Mapping of hand Id to HandDocument of active hand. */
  private final Map<UUID, HandModel> hands;

  /** Mapping of game Id to Id of an active hand. */
  private final Map<UUID, UUID> gameIdToHandIdMap;

  /** Mapping of user Id to game Id. */
  private final Map<UUID, UUID> userIdToGameIdMap;

  /** Mapping of game Id to deck */
  private final Map<UUID, DeckModel> gameIdToDeckMap;

  private final UserRepository userRepository;

  private final ApplicationEventPublisher applicationEventPublisher;

  private final CardService cardService;

  private final HandRepository handRepository;

  private final WebSocketService webSocketService;

  /**
   * Checks if there is a hand associated with the ID provided, throws if not and returns the hand
   * if there is.
   *
   * @param handId The ID of the hand being sought.
   * @return The hand associated with the ID specified.
   * @throws BadRequestException If there is no hand associated with the ID provided.
   */
  public HandModel getHand(final UUID handId) throws BadRequestException {
    // TODO: Was getting exceptions because the hand couldn't be found. Now checking DB if hand
    // can't be found and only throwing if both attempts fail.
    final HandModel hand =
        hands.get(handId) != null ? hands.get(handId) : handRepository.findHandDocumentById(handId);
    if (hand == null) {
      throw handConstants.getHandNotFoundException();
    }
    return hand;
  }

  /**
   * Checks if there is an active hand associated with the game specified, throws if not and returns
   * the hand if there is.
   *
   * @param gameModel Model of the game associated with the hand being sought.
   * @return The active hand associated with the game specified.
   * @throws BadRequestException If there is no hand associated with the game specified.
   */
  public HandModel getHand(final GameModel gameModel) throws BadRequestException {
    if (gameIdToHandIdMap.get(gameModel.getId()) == null) {
      throw handConstants.getNoGameToHandMappingException();
    }
    return getHand(gameIdToHandIdMap.get(gameModel.getId()));
  }

  /**
   * Checks if there is an active hand associated with the specified user, throws if not and returns
   * the hand if there is.
   *
   * @param userDocument The specified user.
   * @return Hand associated with the specified user.
   * @throws BadRequestException If there is no hand associated with the specified user.
   */
  public HandModel getHand(final UserDocument userDocument) throws BadRequestException {
    return getHandForUserId(userDocument.getId());
  }

  /**
   * Checks if there is an active hand associated with the specified user, throws if not and returns
   * the hand if there is.
   *
   * @param userId The specified user.
   * @return The active hand associated with the user specified.
   * @throws BadRequestException If there is no hand associated with the specified user.
   */
  public HandModel getHandForUserId(final UUID userId) throws BadRequestException {
    if (userIdToGameIdMap.get(userId) == null) {
      throw handConstants.getNoUserIdToGameIdMappingFound();
    }
    if (gameIdToHandIdMap.get(userIdToGameIdMap.get(userId)) == null) {
      throw handConstants.getNoGameToHandMappingException();
    }
    return hands.get(gameIdToHandIdMap.get(userIdToGameIdMap.get(userId)));
  }

  /**
   * Creates the mapping from game Id to deck.
   *
   * @param gameId The specified game Id.
   * @param deck The deck.
   */
  public void setDeck(final UUID gameId, final DeckModel deck) {
    gameIdToDeckMap.put(gameId, deck);
  }

  /**
   * Creates the mapping from game Id to deck.
   *
   * @param game The specified game.
   * @param deck The deck.
   */
  public void setDeck(final GameModel game, final DeckModel deck) {
    setDeck(game.getId(), deck);
  }

  /**
   * Removes the deck from the mapping.
   *
   * @param game Game the deck is associated with.
   */
  public void removeDeck(final GameModel game) {
    getDeck(game); // Make sure deck exists.
    gameIdToDeckMap.remove(game.getId());
  }

  /**
   * Shuffles the game for the specified game, throws if there is no deck associated with this game.
   *
   * @param game The specified game.
   * @throws BadRequestException If there is no deck associated with the specified game.
   */
  public void restoreAndShuffle(final GameModel game) throws BadRequestException {
    restoreAndShuffle(game.getId());
  }

  /**
   * Shuffles the game for the specified game, throws if there is no deck associated with this game.
   *
   * @param gameId The specified game.
   * @throws BadRequestException If there is no deck associated with the specified game.
   */
  public void restoreAndShuffle(final UUID gameId) throws BadRequestException {
    getDeck(gameId).restoreAndShuffle();
  }

  /**
   * Retrieves the deck associated with the specified game. Throws if no such deck exists.
   *
   * @param game The specified game.
   * @return The deck associated with the specified game.
   * @throws BadRequestException If there is no deck associated with the specified game.
   */
  public DeckModel getDeck(final GameModel game) throws BadRequestException {
    return getDeck(game.getId());
  }

  /**
   * Retrieves the deck associated with the specified game. Throws if no such deck exists.
   *
   * @param gameId The specified game.
   * @return The deck associated with the specified game.
   * @throws BadRequestException If there is no deck associated with the specified game.
   */
  public DeckModel getDeck(final UUID gameId) throws BadRequestException {
    if (gameIdToDeckMap.get(gameId) == null) {
      throw handConstants.getDeckNotFoundException();
    }
    return gameIdToDeckMap.get(gameId);
  }

  /**
   * Creates a new hand for the specified game.
   *
   * @param game The game the new hand is being created for.
   */
  public void newHand(final GameModel game) {
    log.debug("Creating new hand for game {}.", game.getId());
    final HandModel hand = new HandModel(UUID.randomUUID(), game.getId(), new ArrayList<>(), null);

    // Clear cards list
    game.getPlayers().forEach(p -> p.setCards(new ArrayList<>()));

    // Adding hand to list of hands in game document.
    game.getHands().add(hand.getId());

    // Send hand ID to client.
    webSocketService.sendPublicMessage(
        appConfig.getGameTopic() + game.getId(),
        new GenericServerMessage<>(MessageType.HandStarted, hand.getId()));

    // Add required mappings.
    hands.put(hand.getId(), hand);
    gameIdToHandIdMap.put(game.getId(), hand.getId());
    game.getPlayers().forEach(p -> userIdToGameIdMap.put(p.getId(), game.getId()));

    hand.setActing(game.getPlayers().get(0)); // First in the list acts first.
    restoreAndShuffle(game); // Restore the deck and shuffle it.

    broadcastHandUpdate(game);
    applicationEventPublisher.publishEvent(new WaitForPlayerEvent(this, hand.getActing()));
  }

  /**
   * Broadcasts the active hand associated with the specified game, to all players in this game.
   *
   * @param gameModel The game whose players are being broadcast to.
   */
  public void broadcastHandUpdate(final GameModel gameModel) {
    try {
      final HandModel hand = getHand(gameModel);
      // Broadcast to game topic
      webSocketService.sendPublicMessage(
          appConfig.getGameTopic() + gameModel.getId(),
          new GenericServerMessage<>(MessageType.Hand, hand));
    } catch (Exception e) {
      log.error("Error broadcasting hand update: {}.", e.getMessage());
    }
  }

  /**
   * Asynchronous listener that listens for when a players turn to act begins. After the maximum
   * turn duration is over, checks whether the player acted. If the player has not acted, then a
   * default action is performed on the players behalf.
   *
   * @param waitForPlayerEvent Event containing which player is expected to act.
   */
  @Async
  @EventListener
  public void wait(final WaitForPlayerEvent waitForPlayerEvent) {
    final UUID userId = waitForPlayerEvent.getPlayer().getId();
    log.debug("Waiting for {} to act.", userId);
    final HandModel hand = getHandForUserId(userId);
    final int numActions = hand.getActions().size();

    try {
      // Wait for time defined in config if player's status is active, otherwise wait for 500 ms.
      Thread.sleep(
          waitForPlayerEvent.getPlayer().isAway() ? 100 : appConfig.getTimeToActInMillis());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (hand.getActions().size() > numActions) {
      return;
    }

    log.debug(
        "Waited for {} ms, did not detect any action from {}.",
        appConfig.getTimeToActInMillis(),
        userId);
    log.debug("Performing default action for {} and setting active status to 'false'.", userId);

    // Transition player's active status.
    if (!waitForPlayerEvent.getPlayer().isAway()) {
      applicationEventPublisher.publishEvent(
          new PlayerAfkEvent(this, waitForPlayerEvent.getPlayer()));
    } else {
      draw(userRepository.findUserDocumentById(userId)); // draw card
    }
  }

  /**
   * Helper to determine if it is the specified user's turn to act in the specified hand.
   *
   * @param hand The specified hand.
   * @param user The specified user.
   */
  public void checkIfItsPlayersTurn(final HandModel hand, final UserDocument user) {
    if (!hand.getActing().getId().equals(user.getId())) {
      log.error("Player {} attempted to act but it is not this players turn.", user.getId());
      // TODO: Maybe we don't actually need to throw here...
      throw handConstants.getNotPlayersTurnException();
    }
  }

  /**
   * Draws a card.
   *
   * @param user The player who is attempting to draw.
   * @return The card that was drawn.
   * @throws BadRequestException If something goes wrong.
   */
  public ApiSuccessModel draw(final UserDocument user) throws BadRequestException {
    log.debug("{} drew a card.", user.getId());
    final HandModel hand = getHand(user);
    checkIfItsPlayersTurn(hand, user); // Verify the player can act.

    final CardModel card = getDeck(userIdToGameIdMap.get(user.getId())).draw(); // Draw card.
    final String toastMessage =
        String.format(
            "%s %s drew the %s of %s.", // Bob Dole drew the Five of Diamonds.
            user.getFirstName(), user.getLastName(), card.getValue(), card.getSuit());

    final HandActionModel action = new HandActionModel(HandAction.Draw, hand.getActing(), card);
    hand.getActions().add(action);

    // Broadcast action.
    webSocketService.sendPublicMessage(
        appConfig.getGameTopic() + hand.getGameId(),
        new GenericServerMessage<>(MessageType.HandActionPerformed, action));

    applicationEventPublisher.publishEvent(
        new PublishMessageEvent<>(
            this, appConfig.getGameTopic() + hand.getGameId() + "/drawn-cards", card));
    applicationEventPublisher.publishEvent(
        new SystemChatMessageEvent(this, hand.getGameId(), toastMessage));

    applicationEventPublisher.publishEvent(
        new HandActionEvent(this, hand.getGameId(), hand.getId(), HandAction.Draw, card));
    return new ApiSuccessModel("Card was drawn successfully.");
  }

  /**
   * Determines the winner of the current hand associated with the specified game document.
   *
   * @param gameModel The specified game document.
   */
  public void determineWinner(final GameModel gameModel) {
    final HandModel hand = getHand(gameModel);
    determineWinner(hand);
  }

  /**
   * Determines the winner of the specified hand and returns the winner.
   *
   * @param hand The specified hand.
   * @return The winner of the specified hand.
   */
  public PlayerModel determineWinner(final HandModel hand) {
    final GamePlayerModel winner;
    // Should be able
    final List<HandActionModel> actions = new ArrayList<>(hand.getActions());
    actions.sort((a, b) -> cardService.compare(a.getDrawnCard(), b.getDrawnCard()));
    Collections.reverse(actions);
    actions.get(0).getPlayer().setScore(actions.get(0).getPlayer().getScore() + 1);
    winner = actions.get(0).getPlayer();
    applicationEventPublisher.publishEvent(
        new SystemChatMessageEvent(
            this,
            hand.getGameId(),
            winner.getFirstName() + " " + winner.getLastName() + " won the round!"));
    return winner;
  }

  /**
   * Ends the active hand associated with the specified game.
   *
   * @param game The game associated with the hand being ended.
   * @throws BadRequestException If there is no active hand associated with the game specified.
   */
  public void endHand(final GameModel game) throws BadRequestException {
    // Get the ID of the most recent hand in the game.
    final UUID handId = game.getHands().get(game.getHands().size() - 1);

    // Verify that this is an active hand.
    getHand(handId); // This will throw if the hand does not exist
    getHand(game);

    // Empty cards list for each player

    applicationEventPublisher.publishEvent(
        new PublishMessageEvent<>(
            this,
            appConfig.getGameTopic() + game.getId() + "/drawn-cards",
            new CardModel() // Send a blank card model to indicate the hand is over.
        ));

    // Save the hand to the database.
    handRepository.save(hands.remove(handId));
    gameIdToHandIdMap.remove(game.getId());
    game.getPlayers().forEach(p -> userIdToGameIdMap.remove(p.getId()));
  }
}
