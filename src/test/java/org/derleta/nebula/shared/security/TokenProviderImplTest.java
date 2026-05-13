package org.derleta.nebula.shared.security;

import org.derleta.nebula.shared.domain.exception.TokenExpiredException;
import org.derleta.nebula.shared.security.model.Role;
import org.derleta.nebula.shared.security.model.TokenData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class TokenProviderImplTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private TokenProviderImpl tokenProvider;

    private final String validToken = "valid.jwt.token";
    private final String expiredToken = "expired.jwt.token";
    private final Long userId = 123L;
    private final String email = "user@example.com";
    private Set<Role> roles;
    private TokenData tokenData;

    @BeforeEach
    void setUp() {
        roles = new HashSet<>();
        roles.add(new Role(1, "ROLE_USER"));
        roles.add(new Role(2, "ROLE_ADMIN"));
        tokenData = new TokenData(true, userId, email, validToken, roles);
    }

    @Test
    void getTokenData_shouldReturnTokenData_whenValidTokenProvided() {
        when(jwtTokenUtil.getTokenData(validToken)).thenReturn(tokenData);
        TokenData result = tokenProvider.getTokenData(validToken);
        assertNotNull(result);
        assertEquals(tokenData, result);
        verify(jwtTokenUtil, times(1)).getTokenData(validToken);
    }

    @Test
    void isValid_shouldReturnTrue_whenTokenIsNotExpired() {
        when(jwtTokenUtil.isTokenExpired(validToken)).thenReturn(false);
        boolean result = tokenProvider.isValid(validToken);
        assertTrue(result);
        verify(jwtTokenUtil, times(1)).isTokenExpired(validToken);
    }

    @Test
    void isValid_shouldReturnFalse_whenTokenIsNull() {
        boolean result = tokenProvider.isValid(null);
        assertFalse(result);
    }

    @Test
    void isValid_shouldThrowException_whenTokenIsExpired() {
        when(jwtTokenUtil.isTokenExpired(expiredToken)).thenReturn(true);
        assertThrows(TokenExpiredException.class, () -> tokenProvider.isValid(expiredToken));
        verify(jwtTokenUtil, times(1)).isTokenExpired(expiredToken);
    }

    @Test
    void isValid_withUserId_shouldReturnTrue_whenTokenIsValidAndUserIdMatches() {
        when(jwtTokenUtil.isTokenExpired(validToken)).thenReturn(false);
        when(jwtTokenUtil.getUserId(validToken)).thenReturn(userId);
        boolean result = tokenProvider.isValid(validToken, userId);
        assertTrue(result);
        verify(jwtTokenUtil, times(1)).isTokenExpired(validToken);
        verify(jwtTokenUtil, times(1)).getUserId(validToken);
    }

    @Test
    void isValid_withUserId_shouldReturnFalse_whenTokenIsExpired() {
        when(jwtTokenUtil.isTokenExpired(expiredToken)).thenReturn(true);
        assertThrows(TokenExpiredException.class, () -> tokenProvider.isValid(expiredToken, 123L));
        verify(jwtTokenUtil, times(1)).isTokenExpired(expiredToken);
    }

    @Test
    void isValid_withUserId_shouldReturnFalse_whenTokenIsNull() {
        boolean result = tokenProvider.isValid(null, 123L);
        assertFalse(result);
    }

    @Test
    void isValid_withUserId_shouldReturnFalse_whenUserIdDoesNotMatch() {
        when(jwtTokenUtil.isTokenExpired(validToken)).thenReturn(false);
        when(jwtTokenUtil.getUserId(validToken)).thenReturn(456L);
        boolean result = tokenProvider.isValid(validToken, userId);
        assertFalse(result);
        verify(jwtTokenUtil, times(1)).isTokenExpired(validToken);
        verify(jwtTokenUtil, times(1)).getUserId(validToken);
    }

    @Test
    void getUserId_shouldReturnUserId_whenValidTokenProvided() {
        when(jwtTokenUtil.getUserId(validToken)).thenReturn(userId);
        Long result = tokenProvider.getUserId(validToken);
        assertEquals(userId, result);
        verify(jwtTokenUtil, times(1)).getUserId(validToken);
    }

    @Test
    void getRoles_shouldReturnRoles_whenValidTokenProvided() {
        when(jwtTokenUtil.getRoles(validToken)).thenReturn(roles);
        Set<Role> result = tokenProvider.getRoles(validToken);
        assertEquals(roles, result);
        verify(jwtTokenUtil, times(1)).getRoles(validToken);
    }

    @Test
    void getEmail_shouldReturnEmail_whenValidTokenProvided() {
        when(jwtTokenUtil.getEmail(validToken)).thenReturn(email);
        String result = tokenProvider.getEmail(validToken);
        assertEquals(email, result);
        verify(jwtTokenUtil, times(1)).getEmail(validToken);
    }
}

