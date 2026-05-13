package org.derleta.nebula.user.adapter.out.persistence.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.game.adapter.out.persistence.mapper.GameMapper;
import org.derleta.nebula.gender.adapter.out.persistence.mapper.GenderPersistenceMapper;
import org.derleta.nebula.nationality.adapter.out.persistence.mapper.NationalityPersistenceMapper;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserEntity;
import org.derleta.nebula.user.domain.builder.impl.NebulaUserBuilderImpl;
import org.derleta.nebula.user.domain.model.NebulaUser;
import org.derleta.nebula.userachievement.adapter.out.persistence.mapper.UserAchievementMapper;

/**
 * Utility class for mapping UserEntity objects to NebulaUser objects.
 * This class is designed as a final utility class and cannot be instantiated.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NebulaUserMapper {

    /**
     * Converts a UserEntity object to a NebulaUser object.
     *
     * @param entity the UserEntity object to be converted
     * @return a NebulaUser object constructed from the provided UserEntity
     */
    public static NebulaUser toUser(final UserEntity entity) {
        return new NebulaUserBuilderImpl()
                .id(entity.getId())
                .login(entity.getLogin())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .age(entity.getAge() != null ? entity.getAge() : 0)
                .birthDate(entity.getBirthDate())
                .gender(GenderPersistenceMapper.toDomain(entity.getGender()))
                .nationality(NationalityPersistenceMapper.toDomain(entity.getNationality()))
                .settings(UserSettingsMapper.toSetting(entity.getSettings()))
                .games(GameMapper.toGames(entity.getGames()))
                .achievements(UserAchievementMapper.toUserAchievements(entity.getAchievements()))
                .build();
    }

}
