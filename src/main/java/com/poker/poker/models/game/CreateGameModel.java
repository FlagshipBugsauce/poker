package com.poker.poker.models.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGameModel {
    private String name;
    //TODO add other input fields that relate to creation of a game of poker
}
