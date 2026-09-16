package org.derleta.nebula.account.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.derleta.nebula.account.adapter.out.persistence.jpa.UserJpaRepository;
import org.derleta.nebula.account.application.port.out.AccountRepositoryPort;
import org.derleta.nebula.account.domain.model.Account;
import org.derleta.nebula.game.adapter.out.persistence.jpa.GameJpaRepository;
import org.derleta.nebula.gender.adapter.out.persistence.jpa.GenderJpaRepository;
import org.derleta.nebula.nationality.adapter.out.persistence.jpa.NationalityJpaRepository;
import org.derleta.nebula.theme.adapter.out.persistence.entity.ThemeEntity;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserEntity;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsEntity;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsGeneralEntity;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsSoundEntity;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.UserAchievementEntity;
import org.derleta.nebula.userachievement.adapter.out.persistence.jpa.AchievementJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Outbound adapter — persists Account domain objects via JPA.
 */
@Component
@RequiredArgsConstructor
public class AccountJpaAdapter implements AccountRepositoryPort {

    private final UserJpaRepository userRepository;
    private final NationalityJpaRepository nationalityRepository;
    private final GenderJpaRepository genderRepository;
    private final GameJpaRepository gameRepository;
    private final AchievementJpaRepository achievementRepository;

    @Override
    public Account save(Account account) {
        UserEntity entity = toEntity(account);
        UserEntity saved = userRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Account> findById(long id) {
        return userRepository.findById(id).map(this::toDomain);
    }

    // --- private mapping helpers ---

    private UserEntity toEntity(Account account) {
        UserEntity entity = new UserEntity();
        entity.setId(account.id());
        entity.setEmail(account.email());
        entity.setLogin(account.login());
        entity.setBirthDate(new java.sql.Date(account.birthDate().getTime()));
        entity.updateAge();
        entity.setNationality(
                nationalityRepository.findById(account.nationalityId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "No Nationality found for ID: " + account.nationalityId()))
        );
        entity.setGender(
                genderRepository.findById(account.genderId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "No Gender found for ID: " + account.genderId()))
        );
        entity.setSettings(buildSettings(account.id()));
        entity.setGames(gameRepository.findAll());
        entity.setAchievements(buildAchievements(account.id(), entity));
        return entity;
    }

    private Account toDomain(UserEntity entity) {
        return new Account(
                entity.getId(), entity.getEmail(), entity.getLogin(),
                entity.getBirthDate(), entity.getNationality().getId(), entity.getGender().getId()
        );
    }

    private UserSettingsEntity buildSettings(long userId) {
        return new UserSettingsEntity(userId,
                new UserSettingsGeneralEntity(userId, new ThemeEntity(17, "Default")),
                new UserSettingsSoundEntity(userId, false, true, 100, 100, 100, 100)
        );
    }

    private List<UserAchievementEntity> buildAchievements(long userId, UserEntity userEntity) {
        return achievementRepository.findAll().stream()
                .map(a -> new UserAchievementEntity(
                        IdUtil.getUserAchievementId(userId, a.getId()), userEntity, a, 0, 0, 0
                ))
                .collect(Collectors.toList());
    }
}