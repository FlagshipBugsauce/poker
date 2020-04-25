package com.poker.poker.services;

import com.poker.poker.config.constants.AppConstants;
import com.poker.poker.models.AuthRequestModel;
import com.poker.poker.models.AuthResponseModel;
import com.poker.poker.validation.exceptions.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private CustomUserDetailsService customUserDetailsService;
    private AppConstants appConstants;

    public AuthResponseModel authenticate(AuthRequestModel authRequestModel) {
        try {
            log.info("Attempting to authenticate user {}.", authRequestModel.getEmail());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequestModel.getEmail(),
                    authRequestModel.getPassword()
            ));
        } catch (BadCredentialsException e) {
            log.error(
                    "Authentication of user {} failed because the password provided is invalid.",
                    authRequestModel.getEmail()
            );
            throw new BadRequestException(
                    appConstants.getInvalidCredentialsErrorType(),
                    appConstants.getInvalidCredentialsDescription()
            );
        }

        log.info("Authentication of user {} was successful.", authRequestModel.getEmail());
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(authRequestModel.getEmail());
        final String jwt = jwtService.generateToken(userDetails);

        return new AuthResponseModel(jwt);
    }
}
