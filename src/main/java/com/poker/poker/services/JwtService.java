package com.poker.poker.services;

import com.poker.poker.config.constants.AppConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@AllArgsConstructor
public class JwtService {
    private AppConstants appConstants;

    /**
     * Extracts the email baked into a JWT.
     * @param token JWT.
     * @return The email address baked into the JWT.
     */
    public String extractEmail(String token) {
        return extractClaim(cleanToken(token), Claims::getSubject);
    }

    /**
     * Extracts the expiration time baked into a JWT.
     * @param token JWT.
     * @return The expiration time baked into the JWT.
     */
    public Date extractExpiration(String token) {
        return extractClaim(cleanToken(token), Claims::getExpiration);
    }

    /**
     * Extracts a "claim" that was baked into a JWT, if possible.
     * @param token JWT.
     * @param claimsResolver Function that will resolve the claim.
     * @param <T> Data type of the claim being extracted (usually a string).
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(cleanToken(token));
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(appConstants.getJwtSecretKey()).parseClaimsJws(token).getBody();
    }

    /**
     * Determines whether a token is expired or not. Used to assist with validation.
     * @param token JWT.
     * @return True if token is expired, false otherwise.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a JWT using a UserDetails model.
     * @param userDetails A model representing user details.
     * @return A JWT string which has email and other claims, such as expiration, baked in.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Creates a JWT.
     * @param claims Map of claims.
     * @param subject Subject, which is the username/email.
     * @return A JWT string which has email and other claims, such as expiration, baked in.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + appConstants.getTokenDurationInMillis()))
                .signWith(SignatureAlgorithm.HS256, appConstants.getJwtSecretKey())
                .compact();
    }

    /**
     * Checks the email and expiration date baked into the token to determine if it is valid
     * @param token JWT.
     * @param userDetails User details.
     * @return True if the extracted email matches the email provided with the userDetails model and the token hasn't
     * expired, false otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        return extractEmail(cleanToken(token)).equals(userDetails.getUsername()) &&
                !isTokenExpired(cleanToken(token));
    }

    /**
     * Cleans the "Bearer " prefix if it is there, so that if a token is provided which has the "Bearer " prefix, it
     * wont cause any problems.
     * @param token JWT, with, or without, the "Bearer " prefix.
     * @return JWT only, with no "Bearer " prefix.
     */
    private String cleanToken(String token) {
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }
}
