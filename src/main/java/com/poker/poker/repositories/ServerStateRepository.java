package com.poker.poker.repositories;

import com.poker.poker.documents.ServerStateDocument;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ServerStateRepository extends MongoRepository<ServerStateDocument, UUID> {
  @Query("{ _id: ?0 }")
  public ServerStateDocument findServerStateById(UUID id);
}
