package com.poker.poker.repositories;

import com.poker.poker.models.ServerStateModel;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ServerStateRepository extends MongoRepository<ServerStateModel, UUID> {

  @Query("{ _id: ?0 }")
  ServerStateModel findServerStateById(UUID id);
}
