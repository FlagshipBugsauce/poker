package com.poker.poker.models.game;

import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.enums.UserGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerModel {
  @Schema(description = "User's ID.", example = "0a7d95ef-94ba-47bc-b591-febb365bc543")
  private UUID id;

  @Schema(description = "User's email address.", example = "user@domain.com")
  private String email;

  @Schema(description = "User's user group.", example = "Administrator")
  private UserGroup group;

  @Schema(description = "User's first name.", example = "Fred")
  private String firstName;

  @Schema(description = "User's last name.", example = "Flintstone")
  private String lastName;

  @Schema(description = "Specifies whether a player is ready to start the game.", example = "true")
  private boolean ready;

  @Schema(description = "Specifies whether the player created the game.", example = "false")
  private boolean host;

  /**
   * Constructor that takes in a UserDocument and fill in the fields the two classes have in common.
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
   * Constructor that takes in a UserDocument as well as the ready status and whether this player is
   * currently the host of game.
   * @param userDocument UserDocument representing a player.
   */
  public PlayerModel(UserDocument userDocument, boolean ready, boolean host) {
    this(userDocument);
    this.ready = ready;
    this.host = host;
  }
}
