package org.derleta.nebula.shared.domain.exception;

/**
 * Raised when the external authentication service rejects an access-token refresh
 * with an HTTP 401 (for example, the refresh token has already been rotated, was
 * revoked, or is otherwise no longer active). This signals that the client must
 * re-authenticate and should be surfaced to the caller as 401 UNAUTHORIZED rather
 * than 502 BAD_GATEWAY, which would incorrectly imply an upstream outage.
 */
public class AuthServiceUnauthorizedException extends RuntimeException {
    public AuthServiceUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
