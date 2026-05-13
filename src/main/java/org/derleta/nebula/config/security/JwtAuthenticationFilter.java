package org.derleta.nebula.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.derleta.nebula.shared.security.TokenProvider;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);

        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (!tokenProvider.isValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = tokenProvider.getUserId(token);
            String email = tokenProvider.getEmail(token);

            List<SimpleGrantedAuthority> authorities = tokenProvider.getRoles(token).stream()
                    .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                    .toList();

            var principal = new JwtUserPrincipal(userId, email);

            var authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    token,
                    authorities
            );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ignored) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String accessTokenFromCookie = getCookieValue(request);
        if (accessTokenFromCookie != null && !accessTokenFromCookie.isBlank()) {
            return accessTokenFromCookie;
        }

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && !authorizationHeader.isBlank()) {
            if (authorizationHeader.startsWith("Bearer ")) {
                return authorizationHeader.substring(7).trim();
            }
            return authorizationHeader.trim();
        }

        return null;
    }

    private String getCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if ("accessToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
