package com.poker.poker.models.game;

import com.poker.poker.models.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a player in the game lobby. Contains several extra fields required for the lobby stage
 * of the game, such as ready status and whether this player is the games host.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LobbyPlayer extends Player {

  @Schema(description = "Specifies whether a player is ready to start the game.", example = "true")
  protected boolean ready;

  @Schema(description = "Specifies whether the player created the game.", example = "false")
  protected boolean host;

  /**
   * Constructor that takes in a User as well as the ready status and whether this player is
   * currently the host of game.
   *
   * @param user  The general model of the player (does not contain game related information).
   * @param ready Flag which specifies if the player's ready status should be true or false.
   * @param host  Flag which specifies if the player is the host of the game.
   */
  public LobbyPlayer(final User user, boolean ready, boolean host) {
    super(user);
    this.ready = ready;
    this.host = host;
  }
}
