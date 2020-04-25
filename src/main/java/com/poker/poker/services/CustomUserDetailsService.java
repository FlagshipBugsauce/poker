package com.poker.poker.services;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.repositories.UserRepository;
import com.poker.poker.validation.exceptions.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@AllArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;
    private AppConstants appConstants;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserDocument user = userRepository.findUserDocumentByEmail(s);
        if (user == null) {
            log.error("User with email of {} could not be found.", s);
            throw new BadRequestException(
                    appConstants.getInvalidCredentialsErrorType(),
                    appConstants.getInvalidCredentialsDescription()
            );
        }
        return new User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
