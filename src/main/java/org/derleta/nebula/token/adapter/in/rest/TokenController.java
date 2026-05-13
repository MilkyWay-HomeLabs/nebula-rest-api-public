package org.derleta.nebula.token.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import org.derleta.nebula.account.adapter.in.rest.dto.response.AccessResponse;
import org.derleta.nebula.shared.adapter.in.rest.dto.Response;
import org.derleta.nebula.shared.domain.exception.TokenExpiredException;
import org.derleta.nebula.shared.security.TokenProvider;
import org.derleta.nebula.shared.security.model.Role;
import org.derleta.nebula.token.adapter.in.rest.dto.response.TokenDataResponse;
import org.derleta.nebula.token.adapter.in.rest.mapper.TokenRestMapper;
import org.derleta.nebula.token.application.port.in.RefreshAccessTokenUseCase;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * Inbound REST adapter exposing JWT token operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public final class TokenController {

    public static final String DEFAULT_PATH = "token";

    private final TokenProvider tokenProvider;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    /**
     * Returns token metadata for a valid access token.
     * Returns 401 UNAUTHORIZED if the token is invalid.
     */
    @GetMapping(value = "/" + DEFAULT_PATH, produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<TokenDataResponse> get(@CookieValue("accessToken") String accessToken) {
        if (tokenProvider.isValid(accessToken)) {
            return ResponseEntity.ok(TokenRestMapper.toResponse(tokenProvider.getTokenData(accessToken)));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    /**
     * Returns {@code true} if the token is valid; 401 with {@code false} otherwise.
     */
    @GetMapping(value = "/" + DEFAULT_PATH + "/valid", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Boolean> isValid(@CookieValue("accessToken") String accessToken) {
        boolean result = tokenProvider.isValid(accessToken);
        if (result) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }

    /**
     * Returns the set of roles from the token.
     * Returns 400 BAD_REQUEST if the token is invalid.
     */
    @GetMapping(value = "/" + DEFAULT_PATH + "/roles", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Set<Role>> getRoles(@CookieValue("accessToken") String accessToken) {
        if (tokenProvider.isValid(accessToken)) {
            return ResponseEntity.ok(tokenProvider.getRoles(accessToken));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    /**
     * Returns the email address encoded in the token.
     * Returns 400 BAD_REQUEST if the token is invalid.
     */
    @GetMapping(value = "/" + DEFAULT_PATH + "/email", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<String> getEmail(@CookieValue("accessToken") String accessToken) {
        if (tokenProvider.isValid(accessToken)) {
            return ResponseEntity.ok(tokenProvider.getEmail(accessToken));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    /**
     * Returns the user ID encoded in the token.
     * Returns 400 BAD_REQUEST if the token is invalid.
     */
    @GetMapping(value = "/" + DEFAULT_PATH + "/id", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Long> getId(@CookieValue("accessToken") String accessToken) {
        if (tokenProvider.isValid(accessToken)) {
            return ResponseEntity.ok(tokenProvider.getUserId(accessToken));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    /**
     * Refreshes the access token using the refresh token cookie.
     * Returns 400 BAD_REQUEST if the token is invalid or refresh fails.
     *
     * @throws TokenExpiredException if the refresh token has expired
     */
    @PostMapping(value = "/" + DEFAULT_PATH + "/refresh/access", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Response> refreshAccess(@CookieValue("refreshToken") String refreshToken) {
        if (tokenProvider.isValid(refreshToken)) {
            return getResponseForRefreshAccess(refreshAccessTokenUseCase.refreshAccess(refreshToken));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    // Cookies are sourced from the external auth-service Set-Cookie response headers,
    // not from user-controlled input. Body contains only boolean and JSON-encoded String
    // fields; cookiesHeaders is @JsonIgnore. No real XSS risk.
    @SuppressWarnings("JvmTaintAnalysis")
    private ResponseEntity<Response> getResponseForRefreshAccess(Response response) {
        if (response instanceof AccessResponse instance) {
            Map<String, String> cookies = instance.getCookiesHeaders();

            ResponseCookie accessCookie = buildCookie("accessToken", cookies.get("accessToken"));
            ResponseCookie refreshCookie = buildCookie("refreshToken", cookies.get("refreshToken"));

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(instance);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    private ResponseCookie buildCookie(String name, String rawCookieString) {
        // rawCookieString is a full "name=value; attrs" string from external auth service
        // Extract only the token value part
        String value = rawCookieString != null
                ? rawCookieString.split(";")[0].split("=", 2)[1]
                : "";
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .build();
    }


}

