package com.poker.poker.models.game;

import com.poker.poker.models.user.UserModel;
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
   * Constructor that takes in a UserModel and fill in the fields the two classes have in common.
   *
   * @param userModel UserModel representing a player.
   */
  public PlayerModel(final UserModel userModel) {
    id = userModel.getId();
    firstName = userModel.getFirstName();
    lastName = userModel.getLastName();
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
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PlayerModel)) {
      return false;
    }
    final PlayerModel that = (PlayerModel) o;
    return getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
