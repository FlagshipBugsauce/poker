package com.poker.poker.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewAccountModel {
  @Schema(
      description = "Email address associated with a user account.",
      example = "email@domain.com")
  private String email;

  @Schema(
      description = "Password to the account associated with the email provided.",
      example = "password123")
  private String password;

  @Schema(description = "Users first name.", example = "Fred")
  private String firstName;

  @Schema(description = "Users last name.", example = "Flintstone")
  private String lastName;
}
