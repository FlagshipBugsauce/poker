package com.poker.poker.config.constants;

import lombok.Data;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AppConstants {
    /** Message displayed when server initialization procedure runs. */
    private final String runningInitializationMessage = "RUNNING INITIALIZATION";
    private final String defaultAdminEmail = "admin@domain.com";
    private final String defaultAdminPassword = "admin!@#";
    private final String invalidCredentials = "Invalid username or password.";
    private final long tokenDurationInMillis = 1000 * 60 * 60 * 24 * 14;    // 14 days
    private final String JwtSecretKey = "secret"; // TODO: Change this to something more secure.
}
