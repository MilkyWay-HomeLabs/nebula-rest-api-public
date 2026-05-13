package org.derleta.nebula.user.application.port.in;

import org.derleta.nebula.user.domain.model.NebulaUser;

/** Input port — fetches a single user by ID. */
public interface GetUserUseCase {

    NebulaUser getUser(long userId);
}

