package com.poker.poker.config.constants;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.Data;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AppConstants {
    // INITIALIZATION CONSTANTS
    /** Message displayed when server initialization procedure runs. */
    private final String runningInitializationMessage = "RUNNING INITIALIZATION";
    private final String defaultAdminEmail = "admin@domain.com";
    private final String defaultAdminPassword = "admin!@#";

    // AUTHORIZATION CONSTANTS
    private final String invalidCredentialsErrorType = "Bad Request";
    private final String invalidCredentialsDescription =
            "The email or password entered is invalid. Please try again.";

    // JWT CONSTANTS
    private final long tokenDurationInMillis = 1000 * 60 * 60 * 24 * 14;    // 14 days
    private final String JwtSecretKey = "secret"; // TODO: Change this to something more secure.

    // SWAGGER CONSTANTS
    private final String securityScheme = "bearer";
    private final String bearerFormat = "JWT";
    private final List<SecurityRequirement> securityRequirements =
            Collections.singletonList(new SecurityRequirement().addList(securityScheme));
    private final String swaggerTitle = "Poker Backend";
    private final String swaggerDescription = "Documentation for online, multi-player, poker application.";

    // UserService CONSTANTS
}
