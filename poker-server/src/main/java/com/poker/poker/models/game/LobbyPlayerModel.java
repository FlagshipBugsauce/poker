package com.poker.poker.models.game;

import com.poker.poker.models.user.UserModel;
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
public class LobbyPlayerModel extends PlayerModel {

  @Schema(description = "Specifies whether a player is ready to start the game.", example = "true")
  protected boolean ready;

  @Schema(description = "Specifies whether the player created the game.", example = "false")
  protected boolean host;

  /**
   * Constructor that takes in a UserModel as well as the ready status and whether this player is
   * currently the host of game.
   *
   * @param userModel The general model of the player (does not contain game related information).
   * @param ready Flag which specifies if the player's ready status should be true or false.
   * @param host Flag which specifies if the player is the host of the game.
   */
  public LobbyPlayerModel(final UserModel userModel, boolean ready, boolean host) {
    super(userModel);
    this.ready = ready;
    this.host = host;
  }
}
