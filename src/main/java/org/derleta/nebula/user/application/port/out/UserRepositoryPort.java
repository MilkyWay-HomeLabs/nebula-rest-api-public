package org.derleta.nebula.user.application.port.out;

import org.derleta.nebula.user.domain.model.NebulaUser;
import org.derleta.nebula.user.domain.model.UserSettings;
import org.derleta.nebula.user.application.port.in.command.UpdateProfileCommand;

import java.util.Optional;

/** Output port — data access abstraction for user operations. */
public interface UserRepositoryPort {

    Optional<NebulaUser> findById(long userId);

    NebulaUser updateProfile(UpdateProfileCommand command);

    UserSettings updateSettings(UserSettings userSettings);
}

