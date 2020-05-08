package com.poker.poker.models;

import com.poker.poker.models.user.UserModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseModel {

  @Schema(
      description = "JSON Web Token that can be used to access secured endpoints.",
      example =
          "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBkb21haW4uY29tIiwiZXhwIjoxNTg4OTAzODg5LCJpYXQiOjE1ODc2"
              + "OTQyODl9.u9Uo7Al_a6vu_Rydt1zkhOYWFsIDPR5BgMxJjWRty9w")
  private String jwt;

  @Schema(implementation = UserModel.class, description = "The user's information.")
  private UserModel userDetails;
}
