package org.derleta.nebula.game.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.derleta.nebula.game.application.port.in.CheckAdminRoleUseCase;
import org.derleta.nebula.shared.security.TokenProvider;

/** Application service that delegates JWT admin-role checking to TokenProvider. */
@Service
@RequiredArgsConstructor
public class GameAuthorizationService implements CheckAdminRoleUseCase {

    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    private final TokenProvider tokenProvider;

    @Override
    public boolean notContainsAdminRole(String jwtToken) {
        String token = normalize(jwtToken);

        if (token == null || token.isEmpty()) {
            return true;
        }
        if (tokenProvider.isValid(token)) {
            var roles = tokenProvider.getRoles(token);
            return roles.stream().noneMatch(role -> ADMIN_ROLE.equals(role.getRoleName()));
        }
        return true;
    }

    private String normalize(String token) {
        if (token == null) {
            return null;
        }
        if (token.startsWith("Bearer ")) {
            return token.substring(7).trim();
        }
        return token.trim();
    }
}

