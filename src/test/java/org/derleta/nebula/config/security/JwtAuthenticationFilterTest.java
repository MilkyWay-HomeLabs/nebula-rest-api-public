package org.derleta.nebula.config.security;

import jakarta.servlet.FilterChain;
import org.derleta.nebula.shared.security.TokenProvider;
import org.derleta.nebula.shared.security.model.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private static final String COOKIE_TOKEN = "cookie-token";
    private static final String HEADER_TOKEN = "header-token";

    private TokenProvider tokenProvider;
    private JwtAuthenticationFilter filter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        tokenProvider = mock(TokenProvider.class);
        filter = new JwtAuthenticationFilter(tokenProvider);
        filterChain = mock(FilterChain.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_whenAuthenticationAlreadyExists_shouldSkipTokenResolution() throws Exception {
        Authentication existingAuthentication = new UsernamePasswordAuthenticationToken("existing-user", null);
        SecurityContextHolder.getContext().setAuthentication(existingAuthentication);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertSame(existingAuthentication, SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(tokenProvider);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_whenNoTokenPresent_shouldLeaveContextEmpty() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(tokenProvider);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_whenValidAccessTokenCookiePresent_shouldAuthenticateUser() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("accessToken", COOKIE_TOKEN));
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenProvider.isValid(COOKIE_TOKEN)).thenReturn(true);
        when(tokenProvider.getUserId(COOKIE_TOKEN)).thenReturn(42L);
        when(tokenProvider.getEmail(COOKIE_TOKEN)).thenReturn("user@example.com");
        when(tokenProvider.getRoles(COOKIE_TOKEN)).thenReturn(Set.of(new Role(1, "ROLE_USER")));

        filter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, authentication);
        assertEquals(COOKIE_TOKEN, authentication.getCredentials());
        assertEquals(1, authentication.getAuthorities().size());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_USER".equals(authority.getAuthority())));
        assertInstanceOf(JwtUserPrincipal.class, authentication.getPrincipal());

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        assertEquals(42L, principal.userId());
        assertEquals("user@example.com", principal.email());

        verify(tokenProvider).isValid(COOKIE_TOKEN);
        verify(tokenProvider).getUserId(COOKIE_TOKEN);
        verify(tokenProvider).getEmail(COOKIE_TOKEN);
        verify(tokenProvider).getRoles(COOKIE_TOKEN);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_whenBearerHeaderPresentAndNoCookie_shouldAuthenticateUsingHeaderToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + HEADER_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenProvider.isValid(HEADER_TOKEN)).thenReturn(true);
        when(tokenProvider.getUserId(HEADER_TOKEN)).thenReturn(7L);
        when(tokenProvider.getEmail(HEADER_TOKEN)).thenReturn("admin@example.com");
        when(tokenProvider.getRoles(HEADER_TOKEN)).thenReturn(Set.of(new Role(2, "ROLE_ADMIN")));

        filter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(HEADER_TOKEN, authentication.getCredentials());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())));

        verify(tokenProvider).isValid(HEADER_TOKEN);
        verify(tokenProvider).getUserId(HEADER_TOKEN);
        verify(tokenProvider).getEmail(HEADER_TOKEN);
        verify(tokenProvider).getRoles(HEADER_TOKEN);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_whenCookieAndHeaderPresent_shouldPreferCookieToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("accessToken", COOKIE_TOKEN));
        request.addHeader("Authorization", "Bearer " + HEADER_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenProvider.isValid(COOKIE_TOKEN)).thenReturn(true);
        when(tokenProvider.getUserId(COOKIE_TOKEN)).thenReturn(13L);
        when(tokenProvider.getEmail(COOKIE_TOKEN)).thenReturn("cookie@example.com");
        when(tokenProvider.getRoles(COOKIE_TOKEN)).thenReturn(Set.of());

        filter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(COOKIE_TOKEN, authentication.getCredentials());

        verify(tokenProvider).isValid(COOKIE_TOKEN);
        verify(tokenProvider, never()).isValid(HEADER_TOKEN);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_whenTokenIsInvalid_shouldNotAuthenticate() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("accessToken", COOKIE_TOKEN));
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenProvider.isValid(COOKIE_TOKEN)).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenProvider).isValid(COOKIE_TOKEN);
        verify(tokenProvider, never()).getUserId(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_whenTokenProviderThrows_shouldClearContextAndContinue() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("accessToken", COOKIE_TOKEN));
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenProvider.isValid(COOKIE_TOKEN)).thenThrow(new RuntimeException("boom"));

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenProvider).isValid(COOKIE_TOKEN);
        verify(filterChain).doFilter(request, response);
    }
}

