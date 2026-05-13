package org.derleta.nebula.account.domain.model;

/**
 * Domain result of a successful token generation. Tokens are full Set-Cookie header values.
 */
public record TokenResult(String username, String email,
                          String accessTokenCookie, String refreshTokenCookie) {
}
