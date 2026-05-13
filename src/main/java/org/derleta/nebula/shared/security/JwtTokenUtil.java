package org.derleta.nebula.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.derleta.nebula.shared.security.model.TokenDataBuilderImpl;
import org.derleta.nebula.shared.security.model.Role;
import org.derleta.nebula.shared.security.model.TokenData;
import org.derleta.nebula.shared.domain.exception.TokenExpiredException;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for managing JSON Web Tokens (JWTs) in the application context.
 * This class provides methods for parsing, validating, and extracting information
 * from JWTs, as well as constructs related to token claims.
 * <p>
 * It uses a secret key defined in the application properties for HMAC-SHA signing.
 */
@Component
public class JwtTokenUtil {

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    /**
     * Retrieves token data from a JWT token.
     * The method checks if the token is expired and returns a TokenData object
     * containing the token's validity status, user ID, email, roles, and token.
     *
     * @param token the JWT token from which to retrieve data
     * @return a {@code TokenData} object containing the extracted details if the token is valid,
     *         or {@code null} if the token is expired
     */
    public TokenData getTokenData(String token) {
        if (isTokenExpired(token)) {
            throw new TokenExpiredException("Access token has expired");
        }
        return new TokenDataBuilderImpl().valid(true)
                .email(getEmail(token))
                .userId(getUserId(token))
                .roles(getRoles(token))
                .token(token)
                .build();
    }

    /**
     * Checks if the given JWT token is expired.
     * This method extracts the expiration claim from the token and compares it
     * with the current date and time to determine if the token has expired.
     *
     * @param token the JWT token to be checked for expiration
     * @return {@code true} if the token is expired, {@code false} otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            final Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e){
            return true;
        }
    }

    /**
     * Retrieves the user's roles from a given JWT token.
     * This method parses the token, extracts the roles claim,
     * and converts each role object to a {@code Role} instance.
     * If no roles are found, an empty set is returned.
     *
     * @param token the JWT token from which to extract the roles
     * @return a {@code Set} of {@code Role} objects representing the roles defined in the token
     */
    public Set<Role> getRoles(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        List<?> roles = claims.get("roles", List.class);

        if (roles == null || roles.isEmpty()) return Collections.emptySet();

        return roles.stream()
                .filter(Objects::nonNull)
                .map(this::convertToRole)
                .collect(Collectors.toSet());
    }

    /**
     * Converts a given object into a {@code Role} instance.
     * The input object must be a {@code Map} containing the fields "id" and "name".
     *
     * @param roleObject the object to be converted to a {@code Role}.
     *                   Must be a {@code Map} with keys "id" (an {@code Integer}) and "name" (a {@code String}).
     * @return a {@code Role} instance created from the provided {@code Map}.
     * @throws IllegalArgumentException if the provided object is not a {@code Map} or has an invalid format.
     */
    private Role convertToRole(Object roleObject) {
        if (!(roleObject instanceof Map<?, ?> roleMap)) {
            throw new IllegalArgumentException("Invalid role format");
        }
        Integer id = (Integer) roleMap.get("id");
        String name = Objects.toString(roleMap.get("name"), "");
        return new Role(id, name);
    }

    /**
     * Extracts the email address from the provided JWT token.
     * The email address is assumed to be part of the token's subject, separated by a comma.
     *
     * @param token the JWT token from which the email is to be extracted
     * @return the extracted email address as a {@code String}
     */
    public String getEmail(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        String subject = claims.getSubject();
        if (subject != null && !subject.isBlank()) {
            String[] parts = subject.split(",", 2);
            if (parts.length > 1 && !parts[1].isBlank()) {
                return parts[1];
            }
        }

        String emailClaim = claims.get("email", String.class);
        if (emailClaim != null && !emailClaim.isBlank()) {
            return emailClaim;
        }

        return "";
    }

    /**
     * Extracts the user ID from the provided JWT token.
     * The user ID is assumed to be the first element in the token's subject, separated by a comma.
     *
     * @param token the JWT token from which to extract the user ID
     * @return the extracted user ID as a {@code Long}
     */
    public Long getUserId(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return Long.valueOf(claims.getSubject().split(",")[0]);
    }

    /**
     * Extracts all claims from a given JWT token.
     *
     * @param token the JWT token from which to extract the claims
     * @return a {@code Claims} object containing all claims extracted from the token
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(getPublicSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    /**
     * Retrieves the public signing key used for verifying JWT tokens.
     *
     * @return a SecretKey derived from the application's secret key configured for HMAC-SHA.
     */
    private SecretKey getPublicSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

}
