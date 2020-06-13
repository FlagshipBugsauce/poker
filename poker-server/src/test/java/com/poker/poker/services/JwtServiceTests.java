package com.poker.poker.services;

import com.poker.poker.common.TestBaseClass;
import com.poker.poker.config.constants.AppConstants;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTests extends TestBaseClass {
  @Spy
  private AppConstants appConstants;

  @InjectMocks private JwtService jwtService;

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
            .after(jwtService.extractExpiration(token)));
    // Assuming that this process shouldn't take longer than 1 second.
    Assertions.assertTrue(
        new Date(System.currentTimeMillis() + getTokenExpirationInMillis() - 1000)
            .before(jwtService.extractExpiration(token)));
  }
}
