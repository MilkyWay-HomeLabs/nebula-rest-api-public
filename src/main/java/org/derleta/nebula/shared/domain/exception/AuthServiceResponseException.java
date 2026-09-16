package org.derleta.nebula.shared.domain.exception;

import java.io.IOException;

/**
 * IOException raised when the external authentication service returns a non-2xx
 * HTTP status. Unlike a plain {@link IOException}, it preserves the upstream HTTP
 * status code so callers can distinguish a genuine gateway failure (connectivity,
 * 5xx) from an authorization rejection (4xx) and translate it into an appropriate
 * response for the client.
 */
public class AuthServiceResponseException extends IOException {

    private final int statusCode;

    public AuthServiceResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
