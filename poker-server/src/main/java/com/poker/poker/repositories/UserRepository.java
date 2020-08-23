package com.poker.poker.repositories;

import com.poker.poker.models.user.User;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, UUID> {

  @Query("{ _id: ?0 }")
  User findUserDocumentById(UUID id);

  @Query("{ email: ?0 }")
  User findUserDocumentByEmail(String email);
}
