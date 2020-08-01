package com.poker.poker.models.user;

import com.poker.poker.models.enums.UserGroup;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class UserModel {

  @Id
  private UUID id;
  private String email;
  private String password;
  private UserGroup group;
  private String firstName;
  private String lastName;
}
