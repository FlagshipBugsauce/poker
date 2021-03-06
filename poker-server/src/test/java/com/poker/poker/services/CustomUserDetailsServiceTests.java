package com.poker.poker.services;

import com.poker.poker.common.TestBaseClass;
import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.repositories.UserRepository;
import com.poker.poker.validation.exceptions.BadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CustomUserDetailsServiceTests extends TestBaseClass {

  @Mock private UserRepository userRepository;

  @Spy private AppConstants appConstants;

  @InjectMocks private CustomUserDetailsService customUserDetailsService;

  @BeforeEach
  public void setup() {
    /*
       If the user repository tries to find a user with any email other than the sample
       email provided in TestBaseClass, then it will return null. If the sample email is
       provided, then the appropriate User will be returned, which allows us to
       test whether the CustomUserDetailsService is behaving as expected.
    */
    Mockito.when(userRepository.findUserDocumentByEmail(Mockito.anyString())).thenReturn(null);
    Mockito.when(userRepository.findUserDocumentByEmail(getSampleEmail())).thenReturn(getUser());
  }

  @Test
  public void testLoadUserByUsernameWithUsernameThatExists() {
    // When
    Assertions.assertDoesNotThrow(
        () -> customUserDetailsService.loadUserByUsername(getSampleEmail()));
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(getSampleEmail());

    // Then
    Assertions.assertEquals(getUserDetails(), userDetails);
  }

  @Test
  public void testLoadUserByUsernameWithUsernameThatDoesntExist() {
    // When/Then
    Assertions.assertThrows(
        BadRequestException.class, () -> customUserDetailsService.loadUserByUsername("bad"));
  }
}
