package com.poker.poker.documents;

import com.poker.poker.models.enums.GameState;
import com.poker.poker.models.game.GameActionModel;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "games")
public class GameDocument {
  @Id private UUID id;
  private UUID host;
  private String name;
  private int maxPlayers;
  private BigDecimal buyIn;
  private List<UUID> playerIds;
  private List<GameActionModel> gameActions;
  private GameState currentGameState;
}
