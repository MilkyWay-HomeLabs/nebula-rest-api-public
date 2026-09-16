package org.derleta.nebula.shared.security;

import org.derleta.nebula.shared.security.model.Role;
import org.derleta.nebula.shared.security.model.TokenData;

import java.util.Set;

public interface TokenProvider {

    TokenData getTokenData(String token);

    boolean isValid(String token);

    boolean isValid(String authorizationHeader, long userId);

    Long getUserId(String token);

    String getEmail(String token);

    Set<Role> getRoles(String token);

}

