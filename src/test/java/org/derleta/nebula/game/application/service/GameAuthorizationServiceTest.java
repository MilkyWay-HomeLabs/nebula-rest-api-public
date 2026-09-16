package org.derleta.nebula.game.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.derleta.nebula.shared.security.model.Role;
import org.derleta.nebula.shared.domain.exception.TokenExpiredException;
import org.derleta.nebula.shared.security.TokenProvider;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameAuthorizationServiceTest {

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private GameAuthorizationService authorizationService;

    @Test
    void notContainsAdminRole_nullToken_returnsTrue() {
        assertTrue(authorizationService.notContainsAdminRole(null));
        verifyNoInteractions(tokenProvider);
    }

    @Test
    void notContainsAdminRole_emptyToken_returnsTrue() {
        assertTrue(authorizationService.notContainsAdminRole(""));
        verifyNoInteractions(tokenProvider);
    }

    @Test
    void notContainsAdminRole_invalidToken_returnsTrue() {
        when(tokenProvider.isValid("bad")).thenReturn(false);

        assertTrue(authorizationService.notContainsAdminRole("bad"));
        verify(tokenProvider).isValid("bad");
        verify(tokenProvider, never()).getRoles(any());
    }

    @Test
    void notContainsAdminRole_validTokenWithoutAdminRole_returnsTrue() {
        when(tokenProvider.isValid("token")).thenReturn(true);
        when(tokenProvider.getRoles("token"))
                .thenReturn(Set.of(new Role(1, "ROLE_USER"), new Role(2, "ROLE_EDITOR")));

        assertTrue(authorizationService.notContainsAdminRole("token"));
    }

    @Test
    void notContainsAdminRole_validTokenWithAdminRole_returnsFalse() {
        when(tokenProvider.isValid("admin-token")).thenReturn(true);
        when(tokenProvider.getRoles("admin-token"))
                .thenReturn(Set.of(new Role(1, "ROLE_USER"), new Role(2, "ROLE_ADMIN")));

        assertFalse(authorizationService.notContainsAdminRole("admin-token"));
    }

    @Test
    void notContainsAdminRole_expiredToken_throwsTokenExpiredException() {
        when(tokenProvider.isValid("expired")).thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));

        assertThrows(TokenExpiredException.class,
                () -> authorizationService.notContainsAdminRole("expired"));
    }
}

