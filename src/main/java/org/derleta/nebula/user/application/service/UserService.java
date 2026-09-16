package org.derleta.nebula.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.derleta.nebula.user.domain.model.NebulaUser;
import org.derleta.nebula.user.domain.model.UserSettings;
import org.derleta.nebula.user.application.port.in.GetUserUseCase;
import org.derleta.nebula.user.application.port.in.UpdateUserProfileUseCase;
import org.derleta.nebula.user.application.port.in.UpdateUserSettingsUseCase;
import org.derleta.nebula.user.application.port.in.command.UpdateProfileCommand;
import org.derleta.nebula.user.application.port.out.UserRepositoryPort;
import org.derleta.nebula.user.domain.exception.UserNotFoundException;

/** Application service implementing all user use cases. */
@Service
@RequiredArgsConstructor
public class UserService implements
        GetUserUseCase,
        UpdateUserProfileUseCase,
        UpdateUserSettingsUseCase {

    private final UserRepositoryPort userRepository;

    @Transactional(readOnly = true)
    @Override
    public NebulaUser getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + userId + " not found"));
    }

    @Transactional
    @Override
    public NebulaUser updateProfile(UpdateProfileCommand command) {
        return userRepository.updateProfile(command);
    }

    @Transactional
    @Override
    public UserSettings updateSettings(UserSettings userSettings) {
        validateUserIdsInSettings(userSettings);
        return userRepository.updateSettings(userSettings);
    }

    /** Ensures all user IDs within the settings object are consistent. */
    private void validateUserIdsInSettings(UserSettings userSettings) {
        Long userId = userSettings.userId();
        Long generalUserId = userSettings.general().userId();
        Long soundUserId = userSettings.sound().userId();
        if (!userId.equals(generalUserId) || !userId.equals(soundUserId)) {
            throw new IllegalArgumentException("User IDs in settings do not match!");
        }
    }
}

