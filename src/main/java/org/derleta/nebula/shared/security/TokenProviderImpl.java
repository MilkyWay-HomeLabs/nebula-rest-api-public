package org.derleta.nebula.shared.security;

import lombok.RequiredArgsConstructor;
import org.derleta.nebula.shared.domain.exception.TokenExpiredException;
import org.derleta.nebula.shared.security.model.Role;
import org.derleta.nebula.shared.security.model.TokenData;
import org.derleta.nebula.token.domain.types.TokenResponseType;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * An implementation of the {@link TokenProvider} interface, responsible for handling
 * operations related to JWT tokens, such as extracting information from tokens,
 * validating tokens, and managing token-related data.
 * <p>
 * This class utilizes a {@link JwtTokenUtil} instance to perform the underlying
 * operations for interacting with JWT tokens.
 */
@Service
@RequiredArgsConstructor
public class TokenProviderImpl implements TokenProvider {

    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Retrieves the token data associated with the provided JWT token.
     *
     * @param token the JWT token from which to extract the token data
     * @return a {@code TokenData} object containing details such as validity, user ID, email, token, and roles
     */
    @Override
    public TokenData getTokenData(String token) {
        return jwtTokenUtil.getTokenData(token);
    }

    /**
     * Validates the provided JWT token for its validity.
     *
     * @param token the JWT token to be checked
     * @return true if the token is not expired; false is token null or empty or throw an exception if the token is expired
     */
    @Override
    public boolean isValid(String token) {
        if (token == null || token.isEmpty()) return false;
        if (jwtTokenUtil.isTokenExpired(token))
            throw new TokenExpiredException(TokenResponseType.TOKEN_EXPIRED.name());
        else return true;
    }

    /**
     * Validates whether the given JWT token is not expired and corresponds to the provided user ID.
     *
     * @param jwtToken the JWT token to validate
     * @param userId   the user ID to check against the ID contained in the token
     * @return true if the token is not expired and the user ID matches the ID in the token; false if token empty or null,
     * throw an exception if the token is expired, or if the user ID does not match the ID in the token.
     */
    @Override
    public boolean isValid(String jwtToken, long userId) {
        if (jwtToken == null || jwtToken.isEmpty()) return false;
        if (jwtTokenUtil.isTokenExpired(jwtToken))
            throw new TokenExpiredException(TokenResponseType.ACCESS_TOKEN_EXPIRED.name());
        long userIdInToken = getUserId(jwtToken);
        return userId == userIdInToken;
    }

    /**
     * Extracts the user ID from the provided JWT token.
     *
     * @param token the JWT token from which to extract the user ID
     * @return the extracted user ID as a Long
     */
    @Override
    public Long getUserId(String token) {
        return jwtTokenUtil.getUserId(token);
    }

    /**
     * Retrieves the roles associated with the given JWT token.
     *
     * @param token the JWT token from which to extract the roles
     * @return a {@code Set} of {@code Role} objects representing the roles available in the token
     */
    @Override
    public Set<Role> getRoles(String token) {
        return jwtTokenUtil.getRoles(token);
    }

    /**
     * Retrieves the email address from the given JWT token.
     *
     * @param token the JWT token from which to extract the email address
     * @return the extracted email address as a String
     */
    @Override
    public String getEmail(String token) {
        return jwtTokenUtil.getEmail(token);
    }

}

