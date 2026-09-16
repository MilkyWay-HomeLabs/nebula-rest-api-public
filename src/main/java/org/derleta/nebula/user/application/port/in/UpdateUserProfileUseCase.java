package org.derleta.nebula.user.application.port.in;

import org.derleta.nebula.user.domain.model.NebulaUser;
import org.derleta.nebula.user.application.port.in.command.UpdateProfileCommand;

/** Input port — updates user profile data. */
public interface UpdateUserProfileUseCase {

    NebulaUser updateProfile(UpdateProfileCommand command);
}

