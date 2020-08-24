package com.poker.poker.repositories;

import com.poker.poker.models.ServerState;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ServerStateRepository extends MongoRepository<ServerState, UUID> {

  @Query("{ _id: ?0 }")
  ServerState findServerStateById(UUID id);
}
