package com.poker.poker.services;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.AuthRequestModel;
import com.poker.poker.models.AuthResponseModel;
import com.poker.poker.models.enums.UserGroup;
import com.poker.poker.models.user.NewAccountModel;
import com.poker.poker.models.user.UserModel;
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
   * Authenticates the user if the email and password provided in the AuthRequestModel is valid.
   *
   * @param authRequestModel A model containing an email and a password.
   * @return An AuthResponseModel containing a JWT which can be used to access secured endpoints.
   */
  public AuthResponseModel authenticate(final AuthRequestModel authRequestModel) {
    try {
      log.debug("Attempting to authenticate user with email: {}.", authRequestModel.getEmail());
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authRequestModel.getEmail(), authRequestModel.getPassword()));
    } catch (BadCredentialsException e) {
      log.error(
          "Authentication of user {} failed because the password provided is invalid.",
          authRequestModel.getEmail());
      throw appConstants.getBadPasswordException();
    }

    log.debug("Authentication of user {} was successful.", authRequestModel.getEmail());
    final UserDetails userDetails =
        customUserDetailsService.loadUserByUsername(authRequestModel.getEmail());
    final String jwt = jwtService.generateToken(userDetails);

    final UserDocument user =
        userRepository.findUserDocumentByEmail(authRequestModel.getEmail());

    return new AuthResponseModel(
        jwt,
        new UserModel(
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
   * @return AuthResponseModel containing the same data that is returned when a client authenticates
   * normally.
   */
  public AuthResponseModel authenticateWithJwt(final String jwt) {
    final String email = jwtService.extractEmail(jwt);
    final UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
    if (jwtService.validateToken(jwt, userDetails)) {
      final UserDocument user = userRepository.findUserDocumentByEmail(email);
      return new AuthResponseModel(
          jwt,
          new UserModel(
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
   * Creates a new account, provided the NewAccountModel contains an email that has not yet been
   * used.
   *
   * @param newAccountModel A model containing the information necessary to create a new account.
   * @return An ApiSuccessModel, if the account is created successfully.
   * @throws BadRequestException If the account is not created successfully.
   */
  public ApiSuccessModel register(final NewAccountModel newAccountModel)
      throws BadRequestException {
    // Log
    log.debug("Attempting to create account for user with email: {}.", newAccountModel.getEmail());

    // Make sure that the email doesn't already exist:
    if (userRepository.findUserDocumentByEmail(newAccountModel.getEmail()) != null) {
      log.error("Failed to create account for with email: {}.", newAccountModel.getEmail());
      throw appConstants.getRegistrationFailedException();
    }

    // Create a user with random UUID, in the "Client" user group.
    userRepository.save(
        new UserDocument(
            UUID.randomUUID(),
            newAccountModel.getEmail(),
            passwordEncoder.encode(newAccountModel.getPassword()),
            UserGroup.Client,
            newAccountModel.getFirstName(),
            newAccountModel.getLastName()));

    log.info("Account created successfully for email: {}.", newAccountModel.getEmail());
    return new ApiSuccessModel("Account created successfully.");
  }

  /**
   * Validates the user to check if the group they are in is correct based on their JWT.
   *
   * @param jwt A string that contains the Authentication information of a user.
   * @param groupsAllowed a list of user groups desired to validate a user against.
   */
  public void validate(final String jwt, final List<UserGroup> groupsAllowed)
      throws ForbiddenException {
    final UserDocument userDocument =
        userRepository.findUserDocumentByEmail(jwtService.extractEmail(jwt));
    // User is not in the correct group.
    if (!groupsAllowed.contains(userDocument.getGroup())) {
      log.error(
          "User: {}, was denied access. Groups allowed: {}. User's group: {}.",
          userDocument.getId(),
          groupsAllowed,
          userDocument.getGroup());
      throw appConstants.getInvalidGroupException();
    }
    // User is in the correct group.
    else {
      log.debug(
          "User: {} attempted to validate and was successful. Groups allowed: {}.",
          userDocument.getId(),
          groupsAllowed);
    }
  }

  /**
   * Generates a UserModel from a user id.
   *
   * @param userId The UUID of the user.
   * @return A UserModel associated to the ID provided.
   */
  public UserModel getUserInfo(final String userId) {
    // Check if the string provided is a valid UUID.
    uuidService.checkIfValidAndThrowBadRequest(userId);
    // We know the UUID is valid.
    final UUID id = UUID.fromString(userId);
    final UserDocument userDocument = userRepository.findUserDocumentById(id);
    if (userDocument == null) {
      log.error("Could not find user with ID of {}.", userId);
      throw appConstants.getUserInfoNotFoundException();
    }
    return new UserModel(
        id,
        userDocument.getEmail(),
        userDocument.getGroup(),
        userDocument.getFirstName(),
        userDocument.getLastName());
  }
}
