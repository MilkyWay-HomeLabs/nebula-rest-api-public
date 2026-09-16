package org.derleta.nebula.shared.security.model;

import java.util.Set;

public interface TokenDataBuilder {

    TokenData build();

    TokenDataBuilder valid(boolean valid);

    TokenDataBuilder userId(long userId);

    TokenDataBuilder email(String email);

    TokenDataBuilder token(String token);

    TokenDataBuilder roles(Set<Role> roles);

}
