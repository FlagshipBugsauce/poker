package com.poker.poker.services;

import com.poker.poker.common.TestBaseClass;
import com.poker.poker.config.constants.AppConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTests extends TestBaseClass {
    @Mock
    private AppConstants appConstants;

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    public void setup() {
        Mockito.when(appConstants.getJwtSecretKey()).thenReturn("SECRET");
    }

    @Test
    public void testExtractEmail() {
        // Given
        Mockito.when(appConstants.getTokenDurationInMillis()).thenReturn(getTokenExpirationInMillis());

        // When
        String token = jwtService.generateToken(getUserDetails());

        // Then
        Assertions.assertEquals(getSampleEmail(), jwtService.extractEmail(token));
    }

    @Test
    public void testExtractExpiration() {
        // Given
        Mockito.when(appConstants.getTokenDurationInMillis()).thenReturn(getTokenExpirationInMillis());

        // When
        String token = jwtService.generateToken(getUserDetails());

        // Then
        Assertions.assertTrue(
                new Date(System.currentTimeMillis() + getTokenExpirationInMillis())
                        .after(jwtService.extractExpiration(token))
        );
        // Assuming that this process shouldn't take longer than 1 second.
        Assertions.assertTrue(
                new Date(System.currentTimeMillis() + getTokenExpirationInMillis() + 1000)
                        .after(jwtService.extractExpiration(token))
        );
    }
}
