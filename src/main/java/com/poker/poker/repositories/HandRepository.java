package com.poker.poker.repositories;

import com.poker.poker.documents.HandDocument;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface HandRepository extends MongoRepository<HandDocument, UUID> {

  @Query("{ _id: ?0 }")
  HandDocument findHandDocumentById(UUID id);
}
