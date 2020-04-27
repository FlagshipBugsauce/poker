package com.poker.poker.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ApiSuccessModel {
    @Schema(description = "Returned to indicate API call was successful", example = "Account created successfully.")
    private final String message;
}
