package org.derleta.nebula.account.adapter.out.authservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.derleta.nebula.shared.adapter.in.rest.dto.ResponseWithCookieHeaders;

import java.util.Map;

/**
 * Outbound DTO — carries the token data returned by the auth service after
 * successful token generation.
 * <p>
 * Implements {@link ResponseWithCookieHeaders} so that {@code HttpAuthClient}
 * can attach the {@code Set-Cookie} headers (accessToken / refreshToken) received
 * in the HTTP response.
 * <p>
 * NOTE: The dependency on {@code controller.response.ResponseWithCookieHeaders} is
 * intentional and temporary. Once the shared infrastructure package is introduced,
 * both the interface and this DTO should be relocated there.
 * <p>
 * Replaces the inbound {@code JwtTokenResponse} that was incorrectly used inside
 * the outbound adapter.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public final class AuthTokenResponse implements ResponseWithCookieHeaders {

    @JsonIgnore
    private Map<String, String> cookiesHeaders;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @Override
    public void setCookiesHeaders(Map<String, String> headers) {
        cookiesHeaders = headers;
    }

    @Override
    public Map<String, String> getCookiesHeaders() {
        return cookiesHeaders;
    }
}

