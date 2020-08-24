package com.poker.poker.services;

import com.poker.poker.common.TestBaseClass;
import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.models.enums.UserGroup;
import com.poker.poker.models.user.AuthResponse;
import com.poker.poker.models.user.User;
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

  @Mock private AuthenticationManager authenticationManager;

  @Mock private JwtService jwtService;

  @Mock private CustomUserDetailsService customUserDetailsService;

  @Spy private AppConstants appConstants;

  @Mock private UserRepository userRepository;

  @Spy private PasswordEncoder passwordEncoder;

  @InjectMocks private UserService userService;

  @Test
  public void testAuthenticationWithValidCredentials() {
    // Given
    Mockito.when(authenticationManager.authenticate(Mockito.any(Authentication.class)))
        .thenReturn(null);
    Mockito.when(customUserDetailsService.loadUserByUsername(Mockito.any(String.class)))
        .thenReturn(getUserDetails());
    Mockito.when(jwtService.generateToken(Mockito.any(UserDetails.class)))
        .thenReturn(getSampleJwt());
    Mockito.when(userRepository.findUserDocumentByEmail(getSampleEmail())).thenReturn(getUser());

    // When
    AuthResponse response = userService.authenticate(getSampleAuthRequest());

    // Then
    Assertions.assertEquals(response, getSampleAuthResponse());
  }

  @Test
  public void testAuthenticationWithInvalidUsername() {
    // Given
    Mockito.when(authenticationManager.authenticate(Mockito.any(Authentication.class)))
        .thenThrow(new BadCredentialsException("Invalid Credentials"));

    // When/Then
    Assertions.assertThrows(
        BadRequestException.class, () -> userService.authenticate(getSampleAuthRequest()));
  }

  @Test
  public void testRegistrationWithUniqueEmail() {
    // Given
    Mockito.when(userRepository.findUserDocumentByEmail(Mockito.anyString())).thenReturn(getUser());
    Mockito.when(userRepository.findUserDocumentByEmail(getSampleEmail())).thenReturn(null);
    Mockito.when(userRepository.save(getUser())).thenReturn(null);

    // When/Then
    Assertions.assertNotNull(userService.register(getSampleNewAccount()));
  }

  @Test
  public void testRegistrationWithEmailThatAlreadyExists() {
    // Given
    Mockito.when(userRepository.findUserDocumentByEmail(Mockito.anyString())).thenReturn(null);
    Mockito.when(userRepository.findUserDocumentByEmail(getSampleEmail())).thenReturn(getUser());
    Mockito.when(userRepository.save(getUser())).thenReturn(null);

    // When/Then
    Assertions.assertThrows(
        BadRequestException.class, () -> userService.register(getSampleNewAccount()));
  }

  /** Testing that validation works correctly when a user is a member of the Client group. */
  @Test
  public void testUserGroupValidation01() {
    // Given
    final String jwt = "token";
    Mockito.when(jwtService.extractEmail(jwt)).thenReturn(getSampleEmail());
    Mockito.when(userRepository.findUserDocumentByEmail(getSampleEmail())).thenReturn(getUser());

    // When/Then
    Assertions.assertThrows(
        ForbiddenException.class, () -> userService.validate(jwt, appConstants.getAdminGroups()));
    Assertions.assertDoesNotThrow(() -> userService.validate(jwt, appConstants.getClientGroups()));
    Assertions.assertDoesNotThrow(() -> userService.validate(jwt, appConstants.getAllUsers()));
  }

  /** Testing that validation works correctly when a user is a member of the Guest group. */
  @Test
  public void testUserGroupValidation02() {
    // Given
    final String jwt = "token";
    final User guestUser =
        new User(
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

  /** Testing that validation works correctly when a user is a member of the Administrator group. */
  @Test
  public void testUserGroupValidation03() {
    // Given
    final String jwt = "token";
    final User adminUser =
        new User(
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
