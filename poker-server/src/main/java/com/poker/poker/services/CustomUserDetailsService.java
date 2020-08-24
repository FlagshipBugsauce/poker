package com.poker.poker.services;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.models.user.User;
import com.poker.poker.repositories.UserRepository;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final AppConstants appConstants;

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    User user = userRepository.findUserDocumentByEmail(s);
    if (user == null) {
      log.error(appConstants.getEmailCouldNotBeFound(), s);
      throw appConstants.getBadPasswordException();
    }
    return new org.springframework.security.core.userdetails.User(
        user.getEmail(), user.getPassword(), new ArrayList<>());
  }
}
