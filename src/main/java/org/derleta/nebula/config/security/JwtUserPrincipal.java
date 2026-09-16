package org.derleta.nebula.config.security;

public record JwtUserPrincipal(Long userId, String email) {
}
