package com.poker.poker.documents;

import com.poker.poker.models.enums.UserGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class UserDocument {
    @Id
    private UUID id;
    private String email;
    private String password;
    private UserGroup group;
    private String firstName;
    private String lastName;
}
