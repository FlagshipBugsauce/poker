package com.poker.poker.config.constants;

import com.poker.poker.models.enums.UserGroup;
import com.poker.poker.validation.exceptions.BadRequestException;
import com.poker.poker.validation.exceptions.ForbiddenException;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AppConstants {
  // INITIALIZATION CONSTANTS
  private final String defaultAdminEmail = "admin@domain.com";
  private final String defaultAdminPassword = "admin!@#";
  private final String defaultAdminFirstName = "admin";
  private final String defaultAdminLastName = "admin";

  // SECURITY CONSTANTS
  private final String[] securityExceptions = {
    "/user/auth",
    "/swagger-ui/**",
    "/v3/**",
    "/user/register",
    "/test/sse/**",
    "/emitters/request/**",
    "/emitters/request-lobby/**"
  };

  // AUTHORIZATION CONSTANTS
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
  private final BadRequestException invalidUuidException =
      new BadRequestException("Invalid UUID", "UUID provided is invalid.");

  // UserService CONSTANTS
  private final BadRequestException badPasswordException =
      new BadRequestException(
          "Authentication Failed", "The email or password entered is invalid. Please try again.");

  private final BadRequestException registrationFailedException =
      new BadRequestException(
          "Invalid Email", "An account with the email provided already exists.");

  private final ForbiddenException invalidGroupException =
      new ForbiddenException(
          "Insufficient Permissions",
          "User is not a member of a group which has permission to access this resource.");

  private final BadRequestException userInfoNotFoundException =
      new BadRequestException("User Not Found", "No user with the ID provided could be found.");

  private final BadRequestException userNotFoundException =
      new BadRequestException("No User Found", "The user could not be found.");

  // UserGroup CONSTANTS
  private final List<UserGroup> adminGroups =
      new ArrayList<>(Collections.singletonList(UserGroup.Administrator));
  private final List<UserGroup> clientGroups =
      new ArrayList<>(Arrays.asList(UserGroup.Administrator, UserGroup.Client));
  private final List<UserGroup> allUsers =
      new ArrayList<>(Arrays.asList(UserGroup.Administrator, UserGroup.Client, UserGroup.Guest));
}
