package com.poker.poker.repositories;

import com.poker.poker.models.game.GameModel;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<GameModel, UUID> {}
