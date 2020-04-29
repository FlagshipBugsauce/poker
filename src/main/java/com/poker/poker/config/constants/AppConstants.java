package com.poker.poker.config.constants;

import com.poker.poker.models.enums.UserGroup;
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
    "/user/auth", "/swagger-ui/**", "/v3/**", "/user/register", "/test/sse/**"
  };

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
      "User {} tried to access an endpoint with user group {} and failed";
  private final String validateSuccessLog =
      "User {} accessed an ednpoint with user group {} sucessfuly";
  private final String validateErrorType = "Invalid Group";
  private final String validateErrorDescription =
      "The user group does not allow access to destination";

  // GameService CONSTANTS
  private final String gameCreation = "User {} created a game";

  // UserGroup CONSTANTS
  private final List<UserGroup> adminGroup = new ArrayList<UserGroup>(Arrays.asList
          (
                  UserGroup.Administrator
          ));
  private final List<UserGroup> clientGroup = new ArrayList<UserGroup>(Arrays.asList
          (
                  UserGroup.Administrator,
                  UserGroup.Client
          ));
  private final List<UserGroup> allUsers = new ArrayList<UserGroup>(Arrays.asList
          (
                  UserGroup.Administrator,
                  UserGroup.Client,
                  UserGroup.Guest
          ));
}
