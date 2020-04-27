package com.poker.poker.validation.exceptions;

import com.poker.poker.models.ApiErrorModel;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

@Data
public class ForbiddenException extends RuntimeException {
    @Getter
    private final ApiErrorModel apiErrorModel;

    /**
     * Creates an ForbiddenException with an error object.
     * @param errorType The type of the error.
     * @param description Description of the error;
     */
    public ForbiddenException(final String errorType, final String description) {
        apiErrorModel = new ApiErrorModel(errorType, description, new Date());
    }
}
