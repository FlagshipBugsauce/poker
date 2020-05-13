package com.poker.poker.repositories;

import com.poker.poker.documents.LobbyDocument;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LobbyRepository extends MongoRepository<LobbyDocument, UUID> {}
