package com.poker.poker.config.constants;

import com.poker.poker.models.enums.UserGroup;
import com.poker.poker.validation.exceptions.BadRequestException;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AppConstants {
  // INITIALIZATION CONSTANTS
  /** Message displayed when server initialization procedure runs. */
  private final String runningInitializationMessage = "RUNNING INITIALIZATION";

  private final String defaultAdminEmail = "admin@domain.com";
  private final String defaultAdminPassword = "admin!@#";
  private final String defaultAdminFirstName = "admin";
  private final String defaultAdminLastName = "admin";

  // SECURITY CONSTANTS
  private final String[] securityExceptions = {
    "/user/auth", "/swagger-ui/**", "/v3/**", "/user/register", "/test/sse/**", "/game/emitter/**"
  };

  // GENERAL GAME CONSTANTS
  private final int minNumberOfPlayers = 2;
  private final int maxNumberOfPlayers = 10;

  // EXCEPTION HANDLER CONSTANTS

  // AUTHORIZATION CONSTANTS
  private final String invalidCredentialsErrorType = "Bad Request";
  private final String invalidCredentialsDescription =
      "The email or password entered is invalid. Please try again.";
  private final String emailCouldNotBeFound = "User with email of {} could not be found.";

  // JWT CONSTANTS
  private final long tokenDurationInMillis = 1000 * 60 * 60 * 24 * 14; // 14 days
  private final String JwtSecretKey = "secret"; // TODO: Change this to something more secure.

  // SWAGGER CONSTANTS
  private final String securityScheme = "bearer";
  private final String bearerFormat = "JWT";
  private final List<SecurityRequirement> securityRequirements =
      Collections.singletonList(new SecurityRequirement().addList(securityScheme));
  private final String swaggerTitle = "Poker Backend";
  private final String swaggerDescription =
      "Documentation for online, multi-player, poker application.";

  // General Purpose UUID related CONSTANTS
  private final String invalidUuidErrorType = "Invalid UUID";
  private final String invalidUuidErrorDescription = "UUID provided is invalid.";
  private final BadRequestException invalidUuidException =
      new BadRequestException(invalidUuidErrorType, invalidUuidErrorDescription);

  // UserService CONSTANTS
  private final String authenticationCommencing = "Attempting to authenticate user {}.";
  private final String authenticationFailed =
      "Authentication of user {} failed because the password provided is invalid.";
  private final String authenticationSuccessful = "Authentication of user {} was successful.";
  private final String registrationSuccessful = "Account created successfully.";
  private final String registrationCommencing =
      "Attempting to create account for user with email: {}.";
  private final String registrationFailed = "Failed to create account for with email: {}.";
  private final String registrationSuccessfulLog = "Account created successfully for email: {}.";
  private final String registrationErrorType = "Invalid Email";
  private final String registrationErrorDescription =
      "An account with the email provided already exists.";
  private final String validateFailedLog =
      "User: {}, was denied access. Groups allowed: {}. User's group: {}.";
  private final String validateSuccessLog =
      "User: {} attempted to validate and was successful. Groups allowed: {}.";
  private final String validateErrorType = "Invalid Group";
  private final String validateErrorDescription =
      "User does not have permission to access this resource.";

  // GameService CONSTANTS
  private final String gameCreation = "User: {} created a game.";
  private final String joinGameSendingUpdate =
      "Attempting to send updated gameDocument to client with ID: {}.";
  private final String joinGameSendingUpdateFailed =
      "Failed to send updated GameDocument to client with ID: {}.";
  private final String joinGameJoinSuccessful = "User joined the game successfully.";
  private final BadRequestException joinGamePlayerAlreadyJoinedException =
      new BadRequestException("Failed to Join", "Cannot join more than one game at a time.");
  private final String getGameEmitterPlayerNotInGameErrorType = "Player Not In Game";
  private final String getGameEmitterPlayerNotInGameErrorDescription =
      "Emitters will only be given to players who are in the game specified in the request.";
  private final long gameEmitterDuration = 1000 * 60 * 60 * 24;
  private final long joinGameEmitterDuration = 1000 * 60 * 10;
  private final String emitterFailToSendExceptionErrorType = "Failed to Send Update";
  private final BadRequestException emitterFailToSendException =
      new BadRequestException(
          emitterFailToSendExceptionErrorType, "Sending update to client failed.");
  private final BadRequestException userNotInGameException =
      new BadRequestException(
          "Emitter Not Sent", "Player must be in a game in order to receive an emitter.");
  private final String updatedGameDocumentSentSuccessfully =
      "Updated game document was sent successfully.";
  private final String getGameDocumentErrorLog =
      "Failed to send updated game document to user: {}.";
  private final String joinGamePlayerAlreadyInGameResponse = "Player is already in the game.";
  private final String playerReadySuccessful = "Player status set to ready.";
  private final String playerReadySuccessfulLog = "Player status set to ready (ID: {}).";
  private final String playerReadyUnsuccessfulLog =
      "Failed to set player's status to ready (user ID: {}).";
  private final String failedToSendGameListLog = "Failed to send game list update.";
  private final String playerHasLeftMessage = "Player has left the game.";
  private final BadRequestException leaveGameException = new BadRequestException(
      "Cannot Leave Game", "Game does not exist, or player is not in a game.");
  private final String noEmitterForIdProvided =
      "There is no emitter associated with the ID provided.";
  private final String noEmitterForIdErrorType = "No Emitter Found";
  private final BadRequestException noEmitterForIdException = new BadRequestException(
      noEmitterForIdErrorType, noEmitterForIdProvided);
  private final String emitterCompleteSuccess = "Emitter was destroyed successfully.";

  // UserGroup CONSTANTS
  private final List<UserGroup> adminGroups =
      new ArrayList<>(Collections.singletonList(UserGroup.Administrator));
  private final List<UserGroup> clientGroups =
      new ArrayList<>(Arrays.asList(UserGroup.Administrator, UserGroup.Client));
  private final List<UserGroup> allUsers =
      new ArrayList<>(Arrays.asList(UserGroup.Administrator, UserGroup.Client, UserGroup.Guest));
  private final String getUserInfoUserNotFoundErrorType = "User Not Found";
  private final String getUserInfoUserNotFoundErrorDescription =
      "No user with ID provided could be found.";
  private final BadRequestException getGameEmitterPlayerNotInGameException =
      new BadRequestException(
          getGameEmitterPlayerNotInGameErrorType, getGetUserInfoUserNotFoundErrorDescription());
  private final String getUserInfoUserNotFoundErrorLog = "Could not find user with ID of {}.";
}
