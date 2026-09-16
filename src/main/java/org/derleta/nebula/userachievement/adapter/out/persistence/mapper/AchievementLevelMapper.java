package org.derleta.nebula.userachievement.adapter.out.persistence.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.userachievement.domain.builder.impl.AchievementLevelBuilderImpl;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.AchievementLevelEntity;
import org.derleta.nebula.userachievement.domain.model.AchievementLevel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for mapping between AchievementLevelEntity and AchievementLevel objects.
 * This class provides methods to convert single entity objects and collections of entities
 * to their corresponding model representations. The class is designed to ensure that
 * the mapping is centralized, reusable, and consistent throughout the application.
 * <p>
 * The class is not instantiable and provides only static methods for mapping operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AchievementLevelMapper {

    /**
     * Converts a list of AchievementLevelEntity objects to a list of AchievementLevel objects.
     * This method maps each entity in the input list to its corresponding model representation.
     *
     * @param entities the list of AchievementLevelEntity objects to be converted
     * @return a list of AchievementLevel objects corresponding to the input entities
     */
    public static List<AchievementLevel> toAchievementLevels(final List<AchievementLevelEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream().map(AchievementLevelMapper::toAchievementLevel).collect(Collectors.toList());
    }

    /**
     * Converts an AchievementLevelEntity object to an AchievementLevel object.
     * This method maps the fields of the given AchievementLevelEntity instance
     * to the equivalent fields of an AchievementLevel object using a builder.
     * Throws an IllegalArgumentException if the provided entity is null or lacks a valid ID.
     *
     * @param entity an AchievementLevelEntity object to be converted
     * @return an AchievementLevel object constructed from the provided entity
     * @throws IllegalArgumentException if the entity is null or its ID is invalid
     */
    public static AchievementLevel toAchievementLevel(final AchievementLevelEntity entity) {
        if (entity == null) throw new IllegalArgumentException("AchievementLevelEntity cannot be null");
        if (entity.getId() == null) throw new IllegalArgumentException("AchievementLevelEntity must have a valid ID");
        return new AchievementLevelBuilderImpl()
                .level(entity.getId().getLevel())
                .value(entity.getValue())
                .build();
    }

}
