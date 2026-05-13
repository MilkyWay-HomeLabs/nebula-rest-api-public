package org.derleta.nebula.user.adapter.out.persistence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsEntity;
import org.derleta.nebula.user.adapter.out.persistence.mapper.NebulaUserMapper;
import org.derleta.nebula.user.adapter.out.persistence.mapper.UserSettingsMapper;
import org.derleta.nebula.user.domain.model.NebulaUser;
import org.derleta.nebula.user.domain.model.UserSettings;
import org.derleta.nebula.user.adapter.out.persistence.jpa.UserDataJpaRepository;
import org.derleta.nebula.user.adapter.out.persistence.jpa.UserSettingsJpaRepository;
import org.derleta.nebula.user.application.port.in.command.UpdateProfileCommand;
import org.derleta.nebula.user.application.port.out.UserRepositoryPort;
import java.util.Optional;
/** Outbound persistence adapter implementing UserRepositoryPort. */
@Component
@RequiredArgsConstructor
public class UserJpaAdapter implements UserRepositoryPort {
    private final UserDataJpaRepository userRepository;
    private final UserSettingsJpaRepository userSettingsRepository;
    @Override
    @Transactional(readOnly = true)
    public Optional<NebulaUser> findById(long userId) {
        return userRepository.findById(userId).map(NebulaUserMapper::toUser);
    }
    @Override
    @Transactional
    public NebulaUser updateProfile(UpdateProfileCommand command) {
        userRepository.updateUserDetails(
                command.userId(), command.firstName(), command.lastName(),
                command.birthdate(), command.nationalityId(), command.genderId());
        return NebulaUserMapper.toUser(userRepository.getReferenceById(command.userId()));
    }
    @Override
    @Transactional
    public UserSettings updateSettings(UserSettings userSettings) {
        UserSettingsEntity entity = UserSettingsMapper.toEntity(userSettings);
        UserSettingsEntity saved = userSettingsRepository.save(entity);
        userRepository.updateUserUpdatedAt(userSettings.userId());
        return UserSettingsMapper.toSetting(saved);
    }
}
