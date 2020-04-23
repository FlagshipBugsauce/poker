package com.poker.poker.services;

import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.AuthRequestModel;
import com.poker.poker.models.AuthResponseModel;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private CustomUserDetailsService customUserDetailsService;

    public AuthResponseModel authenticate(AuthRequestModel authRequestModel) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequestModel.getEmail(),
                    authRequestModel.getPassword()
            ));
        } catch (BadCredentialsException e) {
            // TODO: Replace this string literal with something from a constants file
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Incorrect username or password");
        }

        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(authRequestModel.getEmail());
        final String jwt = jwtService.generateToken(userDetails);

        // TODO: Return the actual user document once database is implemented.
        return new AuthResponseModel(jwt);
    }
}
