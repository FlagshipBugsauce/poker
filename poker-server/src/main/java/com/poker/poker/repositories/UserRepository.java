package com.poker.poker.repositories;

import com.poker.poker.models.user.UserModel;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<UserModel, UUID> {

  @Query("{ _id: ?0 }")
  UserModel findUserDocumentById(UUID id);

  @Query("{ email: ?0 }")
  UserModel findUserDocumentByEmail(String email);
}
