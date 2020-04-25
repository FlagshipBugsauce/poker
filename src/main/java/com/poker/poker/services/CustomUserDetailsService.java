package com.poker.poker.services;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, appConstants.getInvalidCredentials());
        }
        return new User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
