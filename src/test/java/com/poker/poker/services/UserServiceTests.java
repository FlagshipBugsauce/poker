package com.poker.poker.services;

import com.poker.poker.common.TestBaseClass;
import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.models.AuthResponseModel;
import com.poker.poker.validation.exceptions.BadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests extends TestBaseClass {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private AppConstants appConstants;

    @InjectMocks
    private UserService userService;

    @Test
    public void testAuthenticationWithValidCredentials() {
        // Given
        Mockito.when(authenticationManager.authenticate(Mockito.any(Authentication.class))).thenReturn(null);
        Mockito
                .when(customUserDetailsService.loadUserByUsername(Mockito.any(String.class)))
                .thenReturn(getUserDetails());
        Mockito.when(jwtService.generateToken(Mockito.any(UserDetails.class))).thenReturn(getSampleJwt());

        // When
        AuthResponseModel response = userService.authenticate(getAuthRequestModel());

        // Then
        Assertions.assertEquals(response, getAuthResponseModel());
    }

    @Test
    public void testAuthenticationWithInvalidUsername() {
        // Given
        Mockito
                .when(authenticationManager.authenticate(Mockito.any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Invalid Credentials"));
        Mockito.when(appConstants.getInvalidCredentialsDescription()).thenReturn("Invalid Credentials");

        // Then
        Assertions.assertThrows(BadRequestException.class, () -> userService.authenticate(getAuthRequestModel()));
    }
}
