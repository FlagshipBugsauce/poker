package com.poker.poker.models.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Authorization request using a JWT.")
public class JwtAuthRequest {

  /** JWT being used to authenticate. */
  @Schema(description = "JWT being used to authenticate.")
  private String jwt;
}
