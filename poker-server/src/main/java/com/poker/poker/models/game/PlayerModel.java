package com.poker.poker.models.game;

import com.poker.poker.documents.UserDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Player of a game.")
public abstract class PlayerModel {

  /** Player's user ID. */
  @Schema(
      description = "User's ID.",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = UUID.class)
  protected UUID id;

  /** Player's first name. */
  @Schema(description = "User's first name.", example = "Fred")
  protected String firstName;

  /** Player's last name. */
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PlayerModel)) {
      return false;
    }
    PlayerModel that = (PlayerModel) o;
    return getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
