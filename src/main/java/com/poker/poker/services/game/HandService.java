package com.poker.poker.services.game;

import com.poker.poker.config.constants.HandConstants;
import com.poker.poker.documents.GameDocument;
import com.poker.poker.documents.HandDocument;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.events.HandActionEvent;
import com.poker.poker.events.WaitForPlayerEvent;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.enums.EmitterType;
import com.poker.poker.models.enums.HandAction;
import com.poker.poker.models.game.GamePlayerModel;
import com.poker.poker.models.game.hand.HandActionModel;
import com.poker.poker.models.game.hand.RollActionModel;
import com.poker.poker.repositories.HandRepository;
import com.poker.poker.repositories.UserRepository;
import com.poker.poker.services.SseService;
import com.poker.poker.validation.exceptions.BadRequestException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

  private HandConstants handConstants;

  /** Mapping of hand Id to HandDocument of active hand. */
  private Map<UUID, HandDocument> hands;

  /** Mapping of game Id to Id of an active hand. */
  private Map<UUID, UUID> gameIdToHandIdMap;

  /** Mapping of user Id to game Id. */
  private Map<UUID, UUID> userIdToGameIdMap;

  private UserRepository userRepository;

  private ApplicationEventPublisher applicationEventPublisher;

  private SseService sseService;

  private HandRepository handRepository;

  /**
   * Checks if there is a hand associated with the ID provided, throws if not and returns the hand
   * if there is.
   *
   * @param handId The ID of the hand being sought.
   * @return The hand associated with the ID specified.
   * @throws BadRequestException If there is no hand associated with the ID provided.
   */
  public HandDocument getHand(UUID handId) throws BadRequestException {
    if (hands.get(handId) == null) {
      throw handConstants.getHandNotFoundException();
    }
    return hands.get(handId);
  }

  /**
   * Checks if there is an active hand associated with the game specified, throws if not and returns
   * the hand if there is.
   *
   * @param gameDocument Model of the game associated with the hand being sought.
   * @return The active hand associated with the game specified.
   * @throws BadRequestException If there is no hand associated with the game specified.
   */
  public HandDocument getHand(GameDocument gameDocument) throws BadRequestException {
    if (gameIdToHandIdMap.get(gameDocument.getId()) == null) {
      throw handConstants.getNoGameToHandMappingException();
    }
    return getHand(gameIdToHandIdMap.get(gameDocument.getId()));
  }

  /**
   * Checks if there is an active hand associated with the specified user, throws if not and returns
   * the hand if there is.
   *
   * @param userDocument The specified user.
   * @return Hand associated with the specified user.
   * @throws BadRequestException If there is no hand associated with the specified user.
   */
  public HandDocument getHand(UserDocument userDocument) throws BadRequestException {
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
  public HandDocument getHandForUserId(UUID userId) throws BadRequestException {
    if (userIdToGameIdMap.get(userId) == null) {
      throw handConstants.getNoUserIdToGameIdMappingFound();
    }
    if (gameIdToHandIdMap.get(userIdToGameIdMap.get(userId)) == null) {
      throw handConstants.getNoGameToHandMappingException();
    }
    return hands.get(gameIdToHandIdMap.get(userIdToGameIdMap.get(userId)));
  }

  /**
   * Creates a new hand for the specified game.
   *
   * @param gameDocument The game the new hand is being created for.
   */
  public void newHand(GameDocument gameDocument) {
    log.debug("Creating new hand for game {}.", gameDocument.getId());
    final HandDocument hand =
        new HandDocument(UUID.randomUUID(), gameDocument.getId(), null, new ArrayList<>(), null);

    // Adding hand to list of hands in game document.
    gameDocument.getHands().add(hand.getId());

    // Add required mappings.
    hands.put(hand.getId(), hand);
    gameIdToHandIdMap.put(gameDocument.getId(), hand.getId());
    gameDocument
        .getPlayers().forEach(p -> userIdToGameIdMap.put(p.getId(), gameDocument.getId()));

    hand.setPlayerToAct(gameDocument.getPlayers().get(0)); // First in the list acts first.
    broadcastHandUpdate(gameDocument);  // Broadcast the new hand.
    applicationEventPublisher
        .publishEvent(new WaitForPlayerEvent(this, hand.getPlayerToAct()));
  }

  /**
   * Broadcasts the active hand associated with the specified game, to all players in this game.
   *
   * @param gameDocument The game whose players are being broadcast to.
   */
  public void broadcastHandUpdate(GameDocument gameDocument) {
    final HandDocument hand = getHand(gameDocument);
    gameDocument
        .getPlayers()
        .forEach(p -> sseService.sendUpdate(EmitterType.Hand, p.getId(), hand));
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
  public void wait(WaitForPlayerEvent waitForPlayerEvent) {
    final UUID userId = waitForPlayerEvent.getPlayer().getId();
    log.debug("Waiting for {} to act.", userId);
    final HandDocument hand = getHandForUserId(userId);
    final int numActions = hand.getActions().size();

    try {
      Thread.sleep(handConstants.getTimeToActInMillis());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (hand.getActions().size() > numActions) {
      return;
    }

    log.debug(
        "Waited for {} ms, did not detect any action from {}.",
        handConstants.getTimeToActInMillis(),
        userId);
    log.debug("Performing default action for {}.", userId);
    roll(
        userRepository.findUserDocumentById(
            userId)); // TODO: Probably want to avoid using repo here
  }

  /**
   * Roll is one of the actions a player can perform. When this action is performed, an event is
   * published and is handled by an asynchronous event listener in the game service.
   *
   * @param user The user performing the roll action.
   * @return An ApiSuccessModel indicating the action was performed successfully.
   */
  public ApiSuccessModel roll(UserDocument user) {
    log.debug("{} performed a roll action.", user.getId());
    final HandDocument hand = getHand(user);

    if (!hand.getPlayerToAct().getId().equals(user.getId())) {
      log.error("Player {} attempted to roll but it is not this players turn.", user.getId());
      throw new BadRequestException(
          "Action Denied", "It is not your turn."); // TODO: Update with constant and better text
    }

    // Make sure the roll is unique so there are no ties.
    final Set<Integer> currentRolls = new HashSet<>();
    for (HandActionModel handActionModel : hand.getActions()) {
      if (handActionModel instanceof RollActionModel) {
        currentRolls.add(((RollActionModel) handActionModel).getValue());
      }
    }

    int number = -1;
    while (number == -1) {
      number = (int) (Math.random() * 100);
      final boolean unique = currentRolls.add(number);
      number = unique ? number : -1;
    }

    log.debug("{} rolled {}.", user.getId(), number);

    hand.getActions()
        .add(
            new RollActionModel(
                UUID.randomUUID(),
                String.format("%s %s rolled %d.", user.getFirstName(), user.getLastName(), number),
                hand.getPlayerToAct(),
                number));

    // Create event indicating that a player rolled.
    log.debug("Creating roll event to be handled by game service.");
    applicationEventPublisher.publishEvent(
        new HandActionEvent(this, hand.getGameId(), hand.getId(), HandAction.Roll));

    return new ApiSuccessModel("Roll was successful");
  }

  public void determineWinner(GameDocument gameDocument) {
    final HandDocument hand = getHand(gameDocument);
    GamePlayerModel winner = null;
    int highestRoll = -1;
    for (RollActionModel r : hand.getActions()) {
      if (r.getValue() > highestRoll) {
        winner = r.getPlayer();
        highestRoll = r.getValue();
      }
    }
    assert(winner != null);
    assert(highestRoll != -1);
    winner.setScore(winner.getScore() + 1);

    // TODO: Hack to avoid repeating the same toast on the client. Find a better solution.
    hand.getActions().get(hand.getActions().size() - 1).setMessage(
        String.format("%s %s wins the round.", winner.getFirstName(), winner.getLastName()));
  }

  /**
   * Ends the active hand associated with the specified game.
   *
   * @param gameDocument The game associated with the hand being ended.
   * @throws BadRequestException If there is no active hand associated with the game specified.
   */
  public void endHand(GameDocument gameDocument) throws BadRequestException {
    determineWinner(gameDocument);
    // Get the ID of the most recent hand in the game.
    final UUID handId = gameDocument.getHands().get(gameDocument.getHands().size() - 1);

    // Verify that this is an active hand.
    getHand(handId); // This will throw if the hand does not exist
    getHand(gameDocument);

    // Save the hand to the database.
    handRepository.save(hands.remove(handId));
    gameIdToHandIdMap.remove(gameDocument.getId());
    gameDocument.getPlayers().forEach(p -> userIdToGameIdMap.remove(p.getId()));
  }
}
