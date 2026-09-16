package org.derleta.nebula.userachievement.adapter.out.persistence.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.userachievement.domain.builder.impl.AchievementBuilderImpl;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.AchievementEntity;
import org.derleta.nebula.userachievement.domain.model.Achievement;

/**
 * Utility class for mapping between AchievementEntity and Achievement objects.
 * This class provides methods to convert database entity instances to their corresponding
 * business model representations. It ensures centralized and consistent mapping logic
 * across the application.
 * <p>
 * The class is not instantiable and provides only static methods for mapping operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AchievementMapper {

    /**
     * Converts an AchievementEntity object to an Achievement object.
     * This method maps the properties of the provided entity to an instance of the Achievement record.
     *
     * @param entity the AchievementEntity object to be converted
     * @return an Achievement object corresponding to the provided entity
     */
    public static Achievement toAchievement(final AchievementEntity entity) {
        return new AchievementBuilderImpl()
                .id(entity.getId())
                .name(entity.getName())
                .minValue(entity.getMinValue() == null ? 0 : entity.getMinValue())
                .maxValue(entity.getMaxValue() == null ? 0 : entity.getMaxValue())
                .description(entity.getDescription())
                .iconUrl(entity.getIconUrl())
                .levels(AchievementLevelMapper.toAchievementLevels(entity.getLevels()))
                .build();
    }

}
