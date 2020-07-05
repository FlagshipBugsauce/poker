package com.poker.poker.services;

import com.poker.poker.common.TestBaseClass;
import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.AuthResponseModel;
import com.poker.poker.models.enums.UserGroup;
import com.poker.poker.repositories.UserRepository;
import com.poker.poker.validation.exceptions.BadRequestException;
import com.poker.poker.validation.exceptions.ForbiddenException;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTests extends TestBaseClass {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtService jwtService;

  @Mock
  private CustomUserDetailsService customUserDetailsService;

  @Spy
  private AppConstants appConstants;

  @Mock
  private UserRepository userRepository;

  @Spy
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  @Test
  public void testAuthenticationWithValidCredentials() {
    // Given
    Mockito.when(authenticationManager.authenticate(Mockito.any(Authentication.class)))
        .thenReturn(null);
    Mockito.when(customUserDetailsService.loadUserByUsername(Mockito.any(String.class)))
        .thenReturn(getUserDetails());
    Mockito.when(jwtService.generateToken(Mockito.any(UserDetails.class)))
        .thenReturn(getSampleJwt());
    Mockito.when(userRepository.findUserDocumentByEmail(getSampleEmail()))
        .thenReturn(getUserDocument());

    // When
    AuthResponseModel response = userService.authenticate(getSampleAuthRequestModel());

    // Then
    Assertions.assertEquals(response, getSampleAuthResponseModel());
  }

  @Test
  public void testAuthenticationWithInvalidUsername() {
    // Given
    Mockito.when(authenticationManager.authenticate(Mockito.any(Authentication.class)))
        .thenThrow(new BadCredentialsException("Invalid Credentials"));

    // When/Then
    Assertions.assertThrows(
        BadRequestException.class, () -> userService.authenticate(getSampleAuthRequestModel()));
  }

  @Test
  public void testRegistrationWithUniqueEmail() {
    // Given
    Mockito.when(userRepository.findUserDocumentByEmail(Mockito.anyString()))
        .thenReturn(getUserDocument());
    Mockito.when(userRepository.findUserDocumentByEmail(getSampleEmail())).thenReturn(null);
    Mockito.when(userRepository.save(getUserDocument())).thenReturn(null);

    // When/Then
    Assertions.assertNotNull(userService.register(getSampleNewAccountModel()));
  }

  @Test
  public void testRegistrationWithEmailThatAlreadyExists() {
    // Given
    Mockito.when(userRepository.findUserDocumentByEmail(Mockito.anyString())).thenReturn(null);
    Mockito.when(userRepository.findUserDocumentByEmail(getSampleEmail()))
        .thenReturn(getUserDocument());
    Mockito.when(userRepository.save(getUserDocument())).thenReturn(null);

    // When/Then
    Assertions.assertThrows(
        BadRequestException.class, () -> userService.register(getSampleNewAccountModel()));
  }

  /**
   * Testing that validation works correctly when a user is a member of the Client group.
   */
  @Test
  public void testUserGroupValidation01() {
    // Given
    final String jwt = "token";
    Mockito.when(jwtService.extractEmail(jwt)).thenReturn(getSampleEmail());
    Mockito.when(userRepository.findUserDocumentByEmail(getSampleEmail()))
        .thenReturn(getUserDocument());

    // When/Then
    Assertions.assertThrows(
        ForbiddenException.class, () -> userService.validate(jwt, appConstants.getAdminGroups()));
    Assertions.assertDoesNotThrow(() -> userService.validate(jwt, appConstants.getClientGroups()));
    Assertions.assertDoesNotThrow(() -> userService.validate(jwt, appConstants.getAllUsers()));
  }

  /**
   * Testing that validation works correctly when a user is a member of the Guest group.
   */
  @Test
  public void testUserGroupValidation02() {
    // Given
    final String jwt = "token";
    final UserDocument guestUser =
        new UserDocument(
            UUID.randomUUID(),
            getSampleEmail(),
            passwordEncoder.encode(getSamplePassword()),
            UserGroup.Guest,
            getSampleFirstName(),
            getSampleLastName());
    Mockito.when(jwtService.extractEmail(jwt)).thenReturn(getSampleEmail());
    Mockito.when(userRepository.findUserDocumentByEmail(getSampleEmail())).thenReturn(guestUser);

    // When/Then
    Assertions.assertThrows(
        ForbiddenException.class, () -> userService.validate(jwt, appConstants.getAdminGroups()));
    Assertions.assertThrows(
        ForbiddenException.class, () -> userService.validate(jwt, appConstants.getClientGroups()));
    Assertions.assertDoesNotThrow(() -> userService.validate(jwt, appConstants.getAllUsers()));
  }

  /**
   * Testing that validation works correctly when a user is a member of the Administrator group.
   */
  @Test
  public void testUserGroupValidation03() {
    // Given
    final String jwt = "token";
    final UserDocument adminUser =
        new UserDocument(
            UUID.randomUUID(),
            getSampleEmail(),
            passwordEncoder.encode(getSamplePassword()),
            UserGroup.Administrator,
            getSampleFirstName(),
            getSampleLastName());
    Mockito.when(jwtService.extractEmail(jwt)).thenReturn(getSampleEmail());
    Mockito.when(userRepository.findUserDocumentByEmail(getSampleEmail())).thenReturn(adminUser);

    // When/Then
    Assertions.assertDoesNotThrow(() -> userService.validate(jwt, appConstants.getAdminGroups()));
    Assertions.assertDoesNotThrow(() -> userService.validate(jwt, appConstants.getClientGroups()));
    Assertions.assertDoesNotThrow(() -> userService.validate(jwt, appConstants.getAllUsers()));
  }
}
