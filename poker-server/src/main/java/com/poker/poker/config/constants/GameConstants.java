package com.poker.poker.config.constants;

import com.poker.poker.validation.exceptions.BadRequestException;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GameConstants extends AppConstants {
  // Exceptions
  private final BadRequestException lobbyNotFoundException =
      new BadRequestException(
          "No Lobby Found", "There is no lobby associated with the ID provided");

  private final BadRequestException noUserIdToLobbyIdMappingFound =
      new BadRequestException(
          "No Mapping Found", "There is no mapping from the user ID provided, to a lobby ID.");

  private final BadRequestException gameNotFoundException =
      new BadRequestException("No Game Found", "There is no game associated with the ID provided");

  private final BadRequestException noUserIdToGameIdMappingFound =
      new BadRequestException(
          "No Mapping Found", "There is no mapping from the user ID provided, to a game ID.");

  private final BadRequestException joinGamePlayerAlreadyJoinedException =
      new BadRequestException("Failed to Join", "Cannot join more than one game at a time.");

  private final BadRequestException createGamePlayerAlreadyInGameException =
      new BadRequestException("Cannot Create Game", "Player is already in a game.");

  private final BadRequestException readyStatusUpdateFailException =
      new BadRequestException(
          "Status Not Updated", "Player must be in a game in order to update their ready status.");

  private final BadRequestException userNotInGameException =
      new BadRequestException(
          "Emitter Not Sent", "Player must be in a game in order to receive a game emitter.");

  private final BadRequestException leaveGameException =
      new BadRequestException(
          "Error Leaving Game", "Game does not exist, or player is not in a game.");

  private final BadRequestException canOnlyJoinLobbyException =
      new BadRequestException("Error Joining Game", "Only lobbies can be joined.");

  private final BadRequestException deckNotFoundException =
      new BadRequestException(
          "Error Retrieving Deck", "There is no deck associated with the specified game.");

  private final BadRequestException playerNotReadyException =
      new BadRequestException(
          "Error Starting Game",
          "Status of all players must be set to ready before the game can begin.");

  private final BadRequestException gameDataNotFoundException =
      new BadRequestException(
          "Game Data Not Found", "Could not find game data for the ID provided.");

  private final BadRequestException playerNotInGameException =
      new BadRequestException(
          "Error Changing AFK Status",
          "Player with ID provided was not in the list of players of the specified game.");

  private final BadRequestException playerDataNotFoundException =
      new BadRequestException(
          "Error Retrieving Player Data", "Player data for the ID provided was not found.");
}
