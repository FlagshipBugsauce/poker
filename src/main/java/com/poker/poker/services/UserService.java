package com.poker.poker.services;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.*;
import com.poker.poker.models.enums.UserGroup;
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
  private AuthenticationManager authenticationManager;
  private JwtService jwtService;
  private CustomUserDetailsService customUserDetailsService;
  private AppConstants appConstants;
  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;

  /**
   * Authenticates the user if the email and password provided in the AuthRequestModel is valid.
   *
   * @param authRequestModel A model containing an email and a password.
   * @return An AuthResponseModel containing a JWT which can be used to access secured endpoints.
   */
  public AuthResponseModel authenticate(AuthRequestModel authRequestModel) {
    try {
      log.info(appConstants.getAuthenticationCommencing(), authRequestModel.getEmail());
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authRequestModel.getEmail(), authRequestModel.getPassword()));
    } catch (BadCredentialsException e) {
      log.error(appConstants.getAuthenticationFailed(), authRequestModel.getEmail());
      throw new BadRequestException(
          appConstants.getInvalidCredentialsErrorType(),
          appConstants.getInvalidCredentialsDescription());
    }

    log.info(appConstants.getAuthenticationSuccessful(), authRequestModel.getEmail());
    final UserDetails userDetails =
        customUserDetailsService.loadUserByUsername(authRequestModel.getEmail());
    final String jwt = jwtService.generateToken(userDetails);

    return new AuthResponseModel(jwt);
  }

  /**
   * Creates a new account, provided the NewAccountModel contains an email that has not yet been
   * used.
   *
   * @param newAccountModel A model containing the information necessary to create a new account.
   * @return An ApiSuccessModel, if the account is created successfully.
   * @throws BadRequestException If the account is not created successfully.
   */
  public ApiSuccessModel register(NewAccountModel newAccountModel) throws BadRequestException {
    // Log
    log.info(appConstants.getRegistrationCommencing(), newAccountModel.getEmail());

    // Make sure that the email doesn't already exist:
    if (userRepository.findUserDocumentByEmail(newAccountModel.getEmail()) != null) {
      log.error(appConstants.getRegistrationFailed(), newAccountModel.getEmail());
      throw new BadRequestException(
          appConstants.getRegistrationErrorType(), appConstants.getRegistrationErrorDescription());
    }

    // Create a user with random UUID, in the "User" user group, with the data provided in the
    // NewAccountModel.
    userRepository.save(
        new UserDocument(
            UUID.randomUUID(),
            newAccountModel.getEmail(),
            passwordEncoder.encode(newAccountModel.getPassword()),
            UserGroup.User,
            newAccountModel.getFirstName(),
            newAccountModel.getLastName()));

    log.info(appConstants.getRegistrationSuccessfulLog(), newAccountModel.getEmail());
    return new ApiSuccessModel(appConstants.getRegistrationSuccessful());
  }
  /**
   * Validates the user to check if the group they are in is correct based on their JWT.
   *
   * @param jwt A string that contains the Authentication information of a user.
   * @param userGroup a list of user groups desired to validate a user against.
   */
  public void validate(String jwt, List<UserGroup> userGroup) throws ForbiddenException {
    String jwtEmail = jwtService.extractEmail(jwt);
    UserDocument userDoc = userRepository.findUserDocumentByEmail(jwtEmail);
    // User is not in the correct group.
    if (userGroup.contains(userDoc.getGroup())) {
      log.error(appConstants.getValidateFailedLog(), userDoc.getId(), userGroup);
      throw new ForbiddenException(
          appConstants.getValidateErrorType(), appConstants.getValidateErrorDescription());
    }
    // User is in the correct group.
    else {
      log.info(appConstants.getValidateSuccessLog(), userDoc.getId(), userGroup);
    }
  }
}
