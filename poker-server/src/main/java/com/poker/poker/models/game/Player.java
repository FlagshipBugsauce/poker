package com.poker.poker.models.game;

import com.poker.poker.models.user.User;
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
public abstract class Player {

  /**
   * Player's user ID.
   */
  @Schema(
      description = "User's ID.",
      example = "0a7d95ef-94ba-47bc-b591-febb365bc543",
      implementation = UUID.class)
  protected UUID id;

  /**
   * Player's first name.
   */
  @Schema(description = "User's first name.", example = "Fred")
  protected String firstName;

  /**
   * Player's last name.
   */
  @Schema(description = "User's last name.", example = "Flintstone")
  protected String lastName;

  /**
   * Constructor that takes in a User and fill in the fields the two classes have in common.
   *
   * @param user User representing a player.
   */
  public Player(final User user) {
    id = user.getId();
    firstName = user.getFirstName();
    lastName = user.getLastName();
  }

  /**
   * Copy constructor that will take a player model of any dynamic type and create a new player
   * model with the same values in the main fields.
   *
   * @param player Player that will be copied.
   */
  public Player(final Player player) {
    id = player.getId();
    firstName = player.getFirstName();
    lastName = player.getLastName();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Player)) {
      return false;
    }
    final Player that = (Player) o;
    return getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
