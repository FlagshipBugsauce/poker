package com.poker.poker.repositories;

import com.poker.poker.documents.UserDocument;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<UserDocument, UUID> {
  @Query("{ _id: ?0 }")
  public UserDocument findUserDocumentById(UUID id);

  @Query("{ email: ?0 }")
  public UserDocument findUserDocumentByEmail(String email);
}
