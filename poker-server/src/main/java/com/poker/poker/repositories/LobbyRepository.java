package com.poker.poker.repositories;

import com.poker.poker.models.game.LobbyModel;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LobbyRepository extends MongoRepository<LobbyModel, UUID> {}
