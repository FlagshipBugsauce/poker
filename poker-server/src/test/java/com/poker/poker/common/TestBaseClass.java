package com.poker.poker.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poker.poker.documents.LobbyDocument;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.ApiSuccessModel;
import com.poker.poker.models.AuthRequestModel;
import com.poker.poker.models.AuthResponseModel;
import com.poker.poker.models.enums.UserGroup;
import com.poker.poker.models.game.CreateGameModel;
import com.poker.poker.models.game.LobbyPlayerModel;
import com.poker.poker.models.user.NewAccountModel;
import com.poker.poker.models.user.UserModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
  private final UserGroup sampleUserGroup = UserGroup.Client;
  private final UserGroup sampleAdminUserGroup = UserGroup.Administrator;
  private final UUID zeroUUID = new UUID(0, 0);
  private final long tokenExpirationInMillis = 1000 * 60 * 60 * 24 * 14;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final AuthRequestModel sampleAuthRequestModel =
      new AuthRequestModel(sampleEmail, samplePassword);
  private final UserDetails userDetails = new User(sampleEmail, samplePassword, new ArrayList<>());
  private final UserDocument userDocument =
      new UserDocument(
          zeroUUID,
          sampleEmail,
          sampleHashedPassword,
          sampleUserGroup,
          sampleFirstName,
          sampleLastName);
  private final UserModel sampleUserModel =
      new UserModel(zeroUUID, sampleEmail, sampleUserGroup, sampleFirstName, sampleLastName);
  private final AuthResponseModel sampleAuthResponseModel =
      new AuthResponseModel(sampleJwt, sampleUserModel);
  private final NewAccountModel sampleNewAccountModel =
      new NewAccountModel(sampleEmail, samplePassword, sampleFirstName, sampleLastName);
  private final ApiSuccessModel sampleRegisterSuccessModel = new ApiSuccessModel("Success.");

  // GameService resources
  private final String sampleGameName = "Randy's NL Hold'em Game";
  private final int sampleMaxPlayers = 7;
  private final BigDecimal sampleBuyIn = new BigDecimal("69");
  private final CreateGameModel sampleCreateGameModel =
      new CreateGameModel(sampleGameName, sampleMaxPlayers, sampleBuyIn);
  @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
  private final LobbyDocument sampleLobbyDocument =
      new LobbyDocument(
          UUID.randomUUID(),
          zeroUUID,
          sampleGameName,
          sampleMaxPlayers,
          sampleBuyIn,
          Arrays.asList(new LobbyPlayerModel()), // TODO: Fix this properly
          new ArrayList<>());

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

  /**
   * Generates a random string of n letters.
   *
   * @param n The desired length of the string.
   * @return A string of n random letters.
   */
  protected String randomLetterString(int n) {
    StringBuilder sb = new StringBuilder(n);
    for (int i = 0; i < n; i++) {
      sb.append((char) (Math.random() * 26 + (Math.random() < 0.5 ? 'a' : 'A')));
    }
    return sb.toString();
  }

  /**
   * Generates a random UserDocument.
   *
   * @return A random UserDocument.
   */
  protected UserDocument randomUserDocument() {
    return new UserDocument(
        UUID.randomUUID(),
        "test_email_" + randomLetterString(10) + "@" + randomLetterString(7) + ".com",
        randomLetterString(50),
        UserGroup.Client,
        randomLetterString(15),
        randomLetterString(15));
  }
}
