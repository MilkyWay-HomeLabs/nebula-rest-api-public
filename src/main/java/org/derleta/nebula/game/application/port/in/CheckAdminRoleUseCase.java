package org.derleta.nebula.game.application.port.in;

/** Input port — verify that the caller holds the ADMIN role. */
public interface CheckAdminRoleUseCase {
    boolean notContainsAdminRole(String jwtToken);
}

