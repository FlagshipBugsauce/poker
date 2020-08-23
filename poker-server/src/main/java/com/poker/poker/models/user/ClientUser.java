package com.poker.poker.models.user;

import com.poker.poker.models.enums.UserGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientUser {

  @Schema(description = "User's ID.", example = "0a7d95ef-94ba-47bc-b591-febb365bc543")
  private UUID id;

  @Schema(description = "User's email address.", example = "user@domain.com")
  private String email;

  @Schema(description = "User's user group.", example = "Administrator")
  private UserGroup group;

  @Schema(description = "User's first name.", example = "Fred")
  private String firstName;

  @Schema(description = "User's last name.", example = "Flintstone")
  private String lastName;
}
