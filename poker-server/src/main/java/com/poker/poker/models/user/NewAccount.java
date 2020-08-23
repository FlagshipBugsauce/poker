package com.poker.poker.models.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewAccount {

  @Schema(description = "User's email address.", example = "email@domain.com")
  private String email;

  @Schema(description = "User's account password.", example = "password123")
  private String password;

  @Schema(description = "Users first name.", example = "Fred")
  private String firstName;

  @Schema(description = "Users last name.", example = "Flintstone")
  private String lastName;
}
