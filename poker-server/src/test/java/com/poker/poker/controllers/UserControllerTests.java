package com.poker.poker.controllers;

import com.poker.poker.common.TestBaseClass;
import com.poker.poker.models.user.AuthRequest;
import com.poker.poker.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class UserControllerTests extends TestBaseClass {

  private final String baseMapping = "/user";
  private MockMvc mockMvc;
  @Mock private UserService userService;

  @InjectMocks private UserController userController;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
  }

  @Test
  public void testAuthEndpointReturnsJwt() throws Exception {
    // Given
    final String inputJson = getObjectMapper().writeValueAsString(getSampleAuthRequest());
    final String uri = baseMapping + "/auth";
    Mockito.when(userService.authenticate(Mockito.any(AuthRequest.class)))
        .thenReturn(getSampleAuthResponse());

    // When
    final MockHttpServletResponse response = mockAuthResponse(mockMvc, uri, inputJson);

    // Then
    Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    Assertions.assertEquals(
        getObjectMapper().writeValueAsString(getSampleAuthResponse()),
        response.getContentAsString());
  }

  @Test
  public void testRegistrationEndpointReturnsSuccess() throws Exception {
    // Given
    final String inputJson = getObjectMapper().writeValueAsString(getSampleNewAccount());
    final String uri = baseMapping + "/register";
    Mockito.when(userService.register(getSampleNewAccount()))
        .thenReturn(getSampleRegisterSuccessModel());

    // When
    final MockHttpServletResponse response = mockPostResponse(mockMvc, uri, inputJson);

    // Then
    Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    Assertions.assertEquals(
        getObjectMapper().writeValueAsString(getSampleRegisterSuccessModel()),
        response.getContentAsString());
  }
}
