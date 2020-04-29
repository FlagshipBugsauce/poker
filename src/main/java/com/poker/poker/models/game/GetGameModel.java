package com.poker.poker.models.game;

import com.poker.poker.documents.GameDocument;
import com.poker.poker.models.enums.GameState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class GetGameModel {
    // subset of fields from Game Document
    private String name;
    private int totalUsers;
    private GameState currentGameState;
}
