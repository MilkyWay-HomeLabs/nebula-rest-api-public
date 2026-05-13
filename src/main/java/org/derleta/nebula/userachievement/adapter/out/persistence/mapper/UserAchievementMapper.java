package org.derleta.nebula.userachievement.adapter.out.persistence.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.userachievement.domain.builder.impl.UserAchievementBuilderImpl;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.UserAchievementEntity;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A utility class for mapping between UserAchievementEntity and UserAchievement objects.
 * This class provides methods to convert database entity instances to their corresponding
 * business model representations while ensuring centralized and consistent mapping logic.
 * <p>
 * The class is final and has a private constructor to prevent instantiation, providing only
 * static methods for mapping operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserAchievementMapper {

    /**
     * Converts a list of UserAchievementEntity objects to a list of UserAchievement objects.
     * This method maps each UserAchievementEntity in the provided list to a corresponding UserAchievement object.
     *
     * @param entities the list of UserAchievementEntity objects to be converted
     * @return a list of UserAchievement objects constructed from the provided list of UserAchievementEntity objects
     */
    public static List<UserAchievement> toUserAchievements(final List<UserAchievementEntity> entities) {
        return entities.stream().map(UserAchievementMapper::toUserAchievement).collect(Collectors.toList());
    }

    /**
     * Converts a UserAchievementEntity object to a UserAchievement object.
     * This method maps the properties of the provided entity to an instance of the UserAchievement record.
     *
     * @param entity the UserAchievementEntity object to be converted
     * @return a UserAchievement object corresponding to the provided entity
     */
    public static UserAchievement toUserAchievement(final UserAchievementEntity entity) {
        return new UserAchievementBuilderImpl()
                .userId(entity.getId().getUserId())
                .achievementId(entity.getId().getAchievementId())
                .progress(entity.getProgress())
                .level(entity.getLevel())
                .value(entity.getValue())
                .achievement(AchievementMapper.toAchievement(entity.getAchievement()))
                .build();
    }

}
