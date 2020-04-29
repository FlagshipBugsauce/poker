package com.poker.poker.documents;

import com.poker.poker.models.game.GameActionModel;
import com.poker.poker.models.enums.GameState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "games")
public class GameDocument {
    @Id private UUID id;
    private UUID gameCreator;
    private String gameName;
    private List<UUID> userIDs;
    private List<GameActionModel> gameActions;
    private GameState currentGameState;
}

//TODO
/*
    -- endpoints --
    create game
    get game -> get list of games so you can join -> games in created state
    join game
    ready user -> user has clicked ready,  how to keep track?
    start game -> for the host -> transtion the game state from created to started -> every player must be ready
    leave game

    -- games --
    hashmap -> store games, key = UUID, value = GameModel

    -- created game --
    creating game endpoint
    model for the client to send to backend to create game doc, like a lobby (Pre-Game) -> total num of ppl in game
    -> game name
    -> type of game
    -> pot limit
    -> buy in
    -> basically configurations for Poker

    method creates game model and adds it to hashmap.
    return game id to client

    Game controller -> calls GameService to do the meat
    Game service -> singleton
*/
