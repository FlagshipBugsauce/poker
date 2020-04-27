package com.poker.poker.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.AuthRequestModel;
import com.poker.poker.models.AuthResponseModel;
import com.poker.poker.models.NewAccountModel;
import com.poker.poker.models.enums.UserGroup;
import java.util.ArrayList;
import java.util.UUID;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@Data
public class TestBaseClass {
  private final String sampleJwt = "SampleJWT";
  private final String sampleEmail = "admin@domain.com";
  private final String samplePassword = "password123";
  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private final String sampleHashedPassword = passwordEncoder.encode(samplePassword);
  private final String sampleFirstName = "admin";
  private final String sampleLastName = "admin";
  private final UserGroup sampleUserGroup = UserGroup.User;
  private final UserGroup sampleAdminUserGroup = UserGroup.Administrator;
  private final UUID zeroUUID = new UUID(0, 0);
  private final long tokenExpirationInMillis = 1000 * 60 * 60 * 24 * 14;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final AuthRequestModel sampleAuthRequestModel =
      new AuthRequestModel(sampleEmail, samplePassword);
  private final AuthResponseModel sampleAuthResponseModel = new AuthResponseModel(sampleJwt);
  private final UserDetails userDetails = new User(sampleEmail, samplePassword, new ArrayList<>());
  private final UserDocument userDocument =
      new UserDocument(
          zeroUUID,
          sampleEmail,
          sampleHashedPassword,
          sampleUserGroup,
          sampleFirstName,
          sampleLastName);
  private final NewAccountModel sampleNewAccountModel =
      new NewAccountModel(sampleEmail, samplePassword, sampleFirstName, sampleLastName);
  private final ApiSuccessModel sampleRegisterSuccessModel = new ApiSuccessModel("Success.");

  public MockHttpServletResponse mockAuthResponse(
      final MockMvc mockMvc, final String uri, final String inputJson) throws Exception {
    return mockMvc
        .perform(
            post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
        .andReturn()
        .getResponse();
  }

  public MockHttpServletResponse mockPostResponse(
      final MockMvc mockMvc, final String uri, final String inputJson) throws Exception {
    return mockMvc
        .perform(
            post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", sampleJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
        .andReturn()
        .getResponse();
  }
}
