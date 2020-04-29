package com.poker.poker.models.game;

import com.poker.poker.models.enums.GameAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameActionModel {
    private UUID userID;
    private GameAction gameAction;
}
