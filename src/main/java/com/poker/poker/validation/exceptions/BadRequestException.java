package com.poker.poker.validation.exceptions;

import com.poker.poker.models.ApiErrorModel;
import lombok.Getter;

import java.util.Date;

public class BadRequestException extends RuntimeException {
    @Getter
    private final ApiErrorModel apiErrorModel;

    /**
     * Creates an BadRequestException with an error object.
     * @param errorType The type of the error.
     * @param description Description of the error;
     */
    public BadRequestException(final String errorType, final String description) {
        apiErrorModel = new ApiErrorModel(errorType, description, new Date());
    }
}
