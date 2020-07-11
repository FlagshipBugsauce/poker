package com.poker.poker.repositories;

import com.poker.poker.models.game.HandModel;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface HandRepository extends MongoRepository<HandModel, UUID> {

  @Query("{ _id: ?0 }")
  HandModel findHandDocumentById(UUID id);
}
