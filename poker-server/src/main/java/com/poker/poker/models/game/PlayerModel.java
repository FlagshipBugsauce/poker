package com.poker.poker.models.game;

import com.poker.poker.documents.UserDocument;
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

  @Schema(description = "User's first name.", example = "Fred")
  protected String firstName;

  @Schema(description = "User's last name.", example = "Flintstone")
  protected String lastName;

  /**
   * Constructor that takes in a UserDocument and fill in the fields the two classes have in common.
   *
   * @param userDocument UserDocument representing a player.
   */
  public PlayerModel(final UserDocument userDocument) {
    id = userDocument.getId();
    firstName = userDocument.getFirstName();
    lastName = userDocument.getLastName();
  }

  /**
   * Copy constructor that will take a player model of any dynamic type and create a new player
   * model with the same values in the main fields.
   *
   * @param playerModel PlayerModel that will be copied.
   */
  public PlayerModel(final PlayerModel playerModel) {
    id = playerModel.getId();
    firstName = playerModel.getFirstName();
    lastName = playerModel.getLastName();
  }
}
