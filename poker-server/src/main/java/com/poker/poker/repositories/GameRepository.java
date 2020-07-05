package com.poker.poker.repositories;

import com.poker.poker.documents.GameDocument;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<GameDocument, UUID> {

}
