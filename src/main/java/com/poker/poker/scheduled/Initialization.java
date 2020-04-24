package com.poker.poker.scheduled;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.documents.ServerStateDocument;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.enums.UserGroup;
import com.poker.poker.repositories.ServerStateRepository;
import com.poker.poker.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@AllArgsConstructor
@Component
public class Initialization {
    private ServerStateRepository serverStateRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AppConstants appConstants;

    // TODO: Find a better way of initializing the server. Running this every minute seems bad.
    /**
     * This method runs every minute and checks whether the server has been initialized by checking the server state
     * repository. This collection should either be empty, or contain a single document. If it is empty, this means that
     * the server needs to be "initialized". Anything that needs to be done once after the server spins up for the first
     * time can be performed in this method. For example - creating an admin user so that it's possible to log in using
     * the client (or postman), without manually inserting a user to the database.
     */
    @Scheduled(cron = "0 0/1 * * * ?")      // Runs at the start of every minute
    public void initialize() {
        // Check if the server has been initialized.
        ServerStateDocument serverState = serverStateRepository
                .findServerStateById(new UUID(0, 0));
        if (serverState != null && serverState.isInitialized()) {
            return;
        }

        // Let's initialize the server
        // TODO: Replace this with proper logging.
        System.out.println(appConstants.getRunningInitializationMessage());

        // First thing we need to do is create an admin user
        userRepository.save(new UserDocument(
                new UUID(0, 0),     // 0-UUID for admin
                appConstants.getDefaultAdminEmail(),
                passwordEncoder.encode(appConstants.getDefaultAdminPassword()),
                UserGroup.Administrator
        ));

        serverStateRepository.save(new ServerStateDocument(
                new UUID(0, 0),     // 0-UUID for server state
                true
        ));
        // Now we have an admin user and the server state record has been added.
    }
}
