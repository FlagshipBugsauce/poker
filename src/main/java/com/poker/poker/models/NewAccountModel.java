package com.poker.poker.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NewAccountModel {
    @Schema(description = "Email address associated with a user account.", example = "email@domain.com")
    private final String email;

    @Schema(description = "Password to the account associated with the email provided.", example = "password123")
    private final String password;

    @Schema(description = "Users first name.", example = "Fred")
    private final String firstName;

    @Schema(description = "Users last name.", example = "Flintstone")
    private final String lastName;
}
