package org.derleta.nebula.token.adapter.in.rest;

import org.derleta.nebula.account.adapter.in.rest.dto.response.AccessResponse;
import org.derleta.nebula.shared.adapter.in.rest.dto.Response;
import org.derleta.nebula.shared.security.model.Role;
import org.derleta.nebula.shared.security.model.TokenData;
import org.derleta.nebula.shared.domain.exception.TokenExpiredException;
import org.derleta.nebula.shared.security.TokenProvider;
import org.derleta.nebula.token.adapter.in.rest.dto.response.TokenDataResponse;
import org.derleta.nebula.token.adapter.in.rest.mapper.TokenRestMapper;
import org.derleta.nebula.token.application.port.in.RefreshAccessTokenUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenControllerTest {

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    @InjectMocks
    private TokenController tokenController;

    private final String validToken = "valid-token";
    private TokenData tokenData;
    private TokenDataResponse tokenDataResponse;
    private Set<Role> roles;

    @BeforeEach
    void setUp() {
        roles = new HashSet<>();
        roles.add(new Role(1, "USER"));
        roles.add(new Role(2, "ADMIN"));

        tokenData = new TokenData(true, 1000L, "test@example.com", validToken, roles);
        tokenDataResponse = TokenDataResponse.builder()
                .valid(true).userId(1000L)
                .email("test@example.com").token(validToken).roles(roles)
                .build();
    }

    // ------------------------------------------------------------------ get

    @Test
    void get_validToken_returnsTokenDataResponse() {
        try (MockedStatic<TokenRestMapper> mapper = Mockito.mockStatic(TokenRestMapper.class)) {
            when(tokenProvider.isValid(validToken)).thenReturn(true);
            when(tokenProvider.getTokenData(validToken)).thenReturn(tokenData);
            mapper.when(() -> TokenRestMapper.toResponse(tokenData)).thenReturn(tokenDataResponse);

            ResponseEntity<TokenDataResponse> response = tokenController.get(validToken);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(tokenDataResponse, response.getBody());
            verify(tokenProvider).getTokenData(validToken);
        }
    }

    @Test
    void get_invalidToken_returnsUnauthorized() {
        when(tokenProvider.isValid(validToken)).thenReturn(false);

        ResponseEntity<TokenDataResponse> response = tokenController.get(validToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void get_expiredToken_throwsTokenExpiredException() {
        when(tokenProvider.isValid("expired")).thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));

        assertThrows(TokenExpiredException.class, () -> tokenController.get("expired"));
        verify(tokenProvider, never()).getTokenData(any());
    }

    // ------------------------------------------------------------------ isValid

    @Test
    void isValid_validToken_returnsTrue() {
        when(tokenProvider.isValid(validToken)).thenReturn(true);

        ResponseEntity<Boolean> response = tokenController.isValid(validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());
    }

    @Test
    void isValid_invalidToken_returnsUnauthorized() {
        when(tokenProvider.isValid("invalid")).thenReturn(false);

        ResponseEntity<Boolean> response = tokenController.isValid("invalid");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(Boolean.FALSE, response.getBody());
    }

    @Test
    void isValid_expiredToken_throwsTokenExpiredException() {
        when(tokenProvider.isValid("expired")).thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));

        assertThrows(TokenExpiredException.class, () -> tokenController.isValid("expired"));
    }

    // ------------------------------------------------------------------ getRoles

    @Test
    void getRoles_validToken_returnsRoles() {
        when(tokenProvider.isValid(validToken)).thenReturn(true);
        when(tokenProvider.getRoles(validToken)).thenReturn(roles);

        ResponseEntity<Set<Role>> response = tokenController.getRoles(validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roles, response.getBody());
    }

    @Test
    void getRoles_invalidToken_returnsBadRequest() {
        when(tokenProvider.isValid("invalid")).thenReturn(false);

        ResponseEntity<Set<Role>> response = tokenController.getRoles("invalid");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(tokenProvider, never()).getRoles(any());
    }

    @Test
    void getRoles_expiredToken_throwsTokenExpiredException() {
        when(tokenProvider.isValid("expired")).thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));

        assertThrows(TokenExpiredException.class, () -> tokenController.getRoles("expired"));
    }

    // ------------------------------------------------------------------ getEmail

    @Test
    void getEmail_validToken_returnsEmail() {
        when(tokenProvider.isValid(validToken)).thenReturn(true);
        when(tokenProvider.getEmail(validToken)).thenReturn("test@example.com");

        ResponseEntity<String> response = tokenController.getEmail(validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test@example.com", response.getBody());
    }

    @Test
    void getEmail_invalidToken_returnsBadRequest() {
        when(tokenProvider.isValid("invalid")).thenReturn(false);

        ResponseEntity<String> response = tokenController.getEmail("invalid");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(tokenProvider, never()).getEmail(any());
    }

    @Test
    void getEmail_expiredToken_throwsTokenExpiredException() {
        when(tokenProvider.isValid("expired")).thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));

        assertThrows(TokenExpiredException.class, () -> tokenController.getEmail("expired"));
    }

    // ------------------------------------------------------------------ getId

    @Test
    void getId_validToken_returnsUserId() {
        when(tokenProvider.isValid(validToken)).thenReturn(true);
        when(tokenProvider.getUserId(validToken)).thenReturn(1000L);

        ResponseEntity<Long> response = tokenController.getId(validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1000L, response.getBody());
    }

    @Test
    void getId_invalidToken_returnsBadRequest() {
        when(tokenProvider.isValid("invalid")).thenReturn(false);

        ResponseEntity<Long> response = tokenController.getId("invalid");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(tokenProvider, never()).getUserId(any());
    }

    @Test
    void getId_expiredToken_throwsTokenExpiredException() {
        when(tokenProvider.isValid("expired")).thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));

        assertThrows(TokenExpiredException.class, () -> tokenController.getId("expired"));
    }

    // ------------------------------------------------------------------ refreshAccess

    @Test
    void refreshAccess_validToken_returnsOkWithCookies() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("accessToken", "accessToken=new-access; Path=/; HttpOnly");
        cookies.put("refreshToken", "refreshToken=new-refresh; Path=/; HttpOnly");

        AccessResponse accessResponse = spy(new AccessResponse(Map.of("accessToken", "new"), true, "ACCESS_REFRESHED"));
        doReturn(cookies).when(accessResponse).getCookiesHeaders();

        when(tokenProvider.isValid(validToken)).thenReturn(true);
        when(refreshAccessTokenUseCase.refreshAccess(validToken)).thenReturn(accessResponse);

        ResponseEntity<Response> response = tokenController.refreshAccess(validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((AccessResponse) response.getBody()).isSuccess());
    }

    @Test
    void refreshAccess_invalidToken_returnsBadRequest() {
        when(tokenProvider.isValid("invalid")).thenReturn(false);

        ResponseEntity<Response> response = tokenController.refreshAccess("invalid");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(refreshAccessTokenUseCase, never()).refreshAccess(any());
    }

    @Test
    void refreshAccess_nullToken_returnsBadRequest() {
        when(tokenProvider.isValid(null)).thenReturn(false);

        ResponseEntity<Response> response = tokenController.refreshAccess(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void refreshAccess_expiredToken_throwsTokenExpiredException() {
        when(tokenProvider.isValid("expired")).thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));

        assertThrows(TokenExpiredException.class, () -> tokenController.refreshAccess("expired"));
        verify(refreshAccessTokenUseCase, never()).refreshAccess(any());
    }
}

