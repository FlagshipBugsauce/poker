package com.poker.poker.models.game;

import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.enums.UserGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public abstract class PlayerModel {

  @Schema(description = "User's ID.", example = "0a7d95ef-94ba-47bc-b591-febb365bc543")
  protected UUID id;

  @Schema(description = "User's email address.", example = "user@domain.com")
  protected String email;

  @Schema(description = "User's user group.", example = "Administrator")
  protected UserGroup group;

  @Schema(description = "User's first name.", example = "Fred")
  protected String firstName;

  @Schema(description = "User's last name.", example = "Flintstone")
  protected String lastName;

  /**
   * Constructor that takes in a UserDocument and fill in the fields the two classes have in common.
   *
   * @param userDocument UserDocument representing a player.
   */
  public PlayerModel(UserDocument userDocument) {
    id = userDocument.getId();
    email = userDocument.getEmail();
    group = userDocument.getGroup();
    firstName = userDocument.getFirstName();
    lastName = userDocument.getLastName();
  }

  /**
   * Copy constructor that will take a player model of any dynamic type and create a new player
   * model with the same values in the main fields.
   *
   * @param playerModel PlayerModel that will be copied.
   */
  public PlayerModel(PlayerModel playerModel) {
    id = playerModel.getId();
    email = playerModel.getEmail();
    group = playerModel.getGroup();
    firstName = playerModel.getFirstName();
    lastName = playerModel.getLastName();
  }
}
