package com.poker.poker.services;

import com.poker.poker.common.TestBaseClass;
import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CustomUserDetailsServiceTests extends TestBaseClass {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AppConstants appConstants;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    public void setup() {
        /*
            If the user repository tries to find a user with any email other than the sample
            email provided in TestBaseClass, then it will return null. If the sample email is
            provided, then the appropriate UserDocument will be returned, which allows us to
            test whether the CustomUserDetailsService is behaving as expected.
         */
        Mockito.when(userRepository.findUserDocumentByEmail(Mockito.anyString())).thenReturn(null);
        Mockito.when(userRepository.findUserDocumentByEmail(getSampleEmail())).thenReturn(getUserDocument());
    }

    @Test
    public void testLoadUserByUsernameWithUsernameThatExists() {
        // When
        Assertions.assertDoesNotThrow(() -> customUserDetailsService.loadUserByUsername(getSampleEmail()));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(getSampleEmail());

        // Then
        Assertions.assertEquals(getUserDetails(), userDetails);
    }

    @Test
    public void testLoadUserByUsernameWithUsernameThatDoesntExist() {
        // Given
        Mockito.when(appConstants.getInvalidCredentials()).thenReturn("Invalid Credentials");

        // Then
        Assertions.assertThrows(
                ResponseStatusException.class,
                () -> customUserDetailsService.loadUserByUsername("bad"),
                "403 FORBIDDEN \"Invalid Credentials\"");
    }
}
