package com.poker.poker.services;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.models.ApiSuccess;
import com.poker.poker.models.enums.UserGroup;
import com.poker.poker.models.user.AuthRequest;
import com.poker.poker.models.user.AuthResponse;
import com.poker.poker.models.user.ClientUser;
import com.poker.poker.models.user.NewAccount;
import com.poker.poker.models.user.User;
import com.poker.poker.repositories.UserRepository;
import com.poker.poker.validation.exceptions.BadRequestException;
import com.poker.poker.validation.exceptions.ForbiddenException;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * This service handles all "user" related actions, such as authentication, account creation,
 * modification of user details, etc...
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final CustomUserDetailsService customUserDetailsService;
  private final AppConstants appConstants;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UuidService uuidService;

  /**
   * Authenticates the user if the email and password provided in the AuthRequest is valid.
   *
   * @param authRequest A model containing an email and a password.
   * @return An AuthResponse containing a JWT which can be used to access secured endpoints.
   */
  public AuthResponse authenticate(final AuthRequest authRequest) {
    try {
      log.debug("Attempting to authenticate user with email: {}.", authRequest.getEmail());
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authRequest.getEmail(), authRequest.getPassword()));
    } catch (BadCredentialsException e) {
      log.error(
          "Authentication of user {} failed because the password provided is invalid.",
          authRequest.getEmail());
      throw appConstants.getBadPasswordException();
    }

    log.debug("Authentication of user {} was successful.", authRequest.getEmail());
    final UserDetails userDetails =
        customUserDetailsService.loadUserByUsername(authRequest.getEmail());
    final String jwt = jwtService.generateToken(userDetails);

    final User user = userRepository.findUserDocumentByEmail(authRequest.getEmail());

    return new AuthResponse(
        jwt,
        new ClientUser(
            user.getId(),
            user.getEmail(),
            user.getGroup(),
            user.getFirstName(),
            user.getLastName()));
  }

  /**
   * Performs a pseudo authentication when client provides a JWT.
   *
   * @param jwt Authentication token.
   * @return AuthResponse containing the same data that is returned when a client authenticates
   * normally.
   */
  public AuthResponse authenticateWithJwt(final String jwt) {
    final String email = jwtService.extractEmail(jwt);
    final UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
    if (jwtService.validateToken(jwt, userDetails)) {
      final User user = userRepository.findUserDocumentByEmail(email);
      return new AuthResponse(
          jwt,
          new ClientUser(
              user.getId(),
              user.getEmail(),
              user.getGroup(),
              user.getFirstName(),
              user.getLastName()));
    } else {
      throw new ForbiddenException("Invalid JWT", "The JWT provided is invalid.");
    }
  }

  /**
   * Creates a new account, provided the NewAccount contains an email that has not yet been used.
   *
   * @param newAccount A model containing the information necessary to create a new account.
   * @return An ApiSuccess, if the account is created successfully.
   * @throws BadRequestException If the account is not created successfully.
   */
  public ApiSuccess register(final NewAccount newAccount)
      throws BadRequestException {
    // Log
    log.debug("Attempting to create account for user with email: {}.", newAccount.getEmail());

    // Make sure that the email doesn't already exist:
    if (userRepository.findUserDocumentByEmail(newAccount.getEmail()) != null) {
      log.error("Failed to create account for with email: {}.", newAccount.getEmail());
      throw appConstants.getRegistrationFailedException();
    }

    // Create a user with random UUID, in the "Client" user group.
    userRepository.save(
        new User(
            UUID.randomUUID(),
            newAccount.getEmail(),
            passwordEncoder.encode(newAccount.getPassword()),
            UserGroup.Client,
            newAccount.getFirstName(),
            newAccount.getLastName()));

    log.info("Account created successfully for email: {}.", newAccount.getEmail());
    return new ApiSuccess("Account created successfully.");
  }

  /**
   * Validates the user to check if the group they are in is correct based on their JWT.
   *
   * @param jwt A string that contains the Authentication information of a user.
   * @param groupsAllowed a list of user groups desired to validate a user against.
   */
  public void validate(final String jwt, final List<UserGroup> groupsAllowed)
      throws ForbiddenException {
    final User user =
        userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt));
    // User is not in the correct group.
    if (!groupsAllowed.contains(user.getGroup())) {
      log.error(
          "User: {}, was denied access. Groups allowed: {}. User's group: {}.",
          user.getId(),
          groupsAllowed,
          user.getGroup());
      throw appConstants.getInvalidGroupException();
    }
    // User is in the correct group.
    else {
      log.debug(
          "User: {} attempted to validate and was successful. Groups allowed: {}.",
          user.getId(),
          groupsAllowed);
    }
  }

  /**
   * Generates a User from a user id.
   *
   * @param userId The UUID of the user.
   * @return A User associated to the ID provided.
   */
  public ClientUser getUserInfo(final String userId) {
    // Check if the string provided is a valid UUID.
    uuidService.checkIfValidAndThrowBadRequest(userId);
    // We know the UUID is valid.
    final UUID id = UUID.fromString(userId);
    final User user = userRepository.findUserDocumentById(id);
    if (user == null) {
      log.error("Could not find user with ID of {}.", userId);
      throw appConstants.getUserInfoNotFoundException();
    }
    return new ClientUser(
        id,
        user.getEmail(),
        user.getGroup(),
        user.getFirstName(),
        user.getLastName());
  }
}
