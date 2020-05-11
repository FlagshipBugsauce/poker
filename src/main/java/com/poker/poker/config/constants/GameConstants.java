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

  private final long gameEmitterDuration = 1000 * 60 * 60 * 24;
  private final long joinGameEmitterDuration = 1000 * 60 * 60;
  private final int minNumberOfPlayers = 2;
  private final int maxNumberOfPlayers = 10;
  /** Re-send last data sent by emitter to prevent browser from timing the emitter out. */
  private final int emitterRefreshRateInMinutes = 1;
  /** If no new data has been sent to an emitter for this many minutes, then destroy the emitter. */
  private final int emitterInactiveExpirationInMinutes = 60;

  // Exceptions
  private final BadRequestException gameNotFoundException =
      new BadRequestException("No Game Found", "There is no game associated with the ID provided");

  private final BadRequestException invalidEmitterTypeException =
      new BadRequestException("Invalid Emitter Type", "The emitter type specified is invalid.");

  private final BadRequestException joinGamePlayerAlreadyJoinedException =
      new BadRequestException("Failed to Join", "Cannot join more than one game at a time.");

  private final BadRequestException emitterFailToSendException =
      new BadRequestException("Failed to Send Update", "Sending update to client failed.");

  private final BadRequestException readyStatusUpdateFailException =
      new BadRequestException(
          "Status Not Updated", "Player must be in a game in order to update their ready status.");

  private final BadRequestException userNotInGameException =
      new BadRequestException(
          "Emitter Not Sent", "Player must be in a game in order to receive a game emitter.");

  private final BadRequestException noEmitterForIdException =
      new BadRequestException(
          "No Emitter Found", "There is no emitter associated with the ID provided.");

  private final BadRequestException noEmitterModelForIdException =
      new BadRequestException(
          "No Emitter Model Found", "There is no emitter model associated with the ID provided.");

  private final BadRequestException leaveGameException =
      new BadRequestException(
          "Error Leaving Game", "Game does not exist, or player is not in a game.");
}
