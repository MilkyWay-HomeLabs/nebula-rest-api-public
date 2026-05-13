package org.derleta.nebula.token.application.port.in;

import org.derleta.nebula.shared.adapter.in.rest.dto.Response;

/** Input port: refreshes the access token using the provided refresh token. */
public interface RefreshAccessTokenUseCase {

    /**
     * Refreshes the access token.
     *
     * @param refreshToken the current refresh token
     * @return the response containing new token cookies or an error payload
     */
    Response refreshAccess(String refreshToken);
}

