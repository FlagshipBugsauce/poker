package com.poker.poker.repositories;

import com.poker.poker.models.game.Lobby;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LobbyRepository extends MongoRepository<Lobby, UUID> {}
