package com.poker.poker.models.game;

import com.poker.poker.documents.UserDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a player in the game lobby. Contains several extra fields required for the lobby
 * stage of the game, such as ready status and whether this player is the games host.
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
   * Constructor that takes in a UserDocument as well as the ready status and whether this player is
   * currently the host of game.
   *
   * @param userDocument UserDocument representing a player.
   */
  public LobbyPlayerModel(UserDocument userDocument, boolean ready, boolean host) {
    super(userDocument);
    this.ready = ready;
    this.host = host;
  }
}
