package org.derleta.nebula.userachievement.domain.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.userachievement.domain.builder.impl.NebulaUserAchievementBuilderImpl;
import org.derleta.nebula.userachievement.domain.model.NebulaUserAchievement;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A utility class for mapping UserAchievement objects to NebulaUserAchievement objects.
 * This class provides methods to perform conversions at both individual object level
 * and list level.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NebulaUserAchievementMapper {

    /**
     * Converts a list of UserAchievement objects into a list of NebulaUserAchievement objects.
     * This method uses the toItem method to map each UserAchievement in the provided list
     * to a corresponding NebulaUserAchievement object.
     *
     * @param items the list of UserAchievement objects to be converted
     * @return a list of NebulaUserAchievement objects constructed from the provided list of UserAchievement objects
     */
    public static List<NebulaUserAchievement> toList(final List<UserAchievement> items) {
        return items.stream().map(NebulaUserAchievementMapper::toItem).collect(Collectors.toList());
    }

    /**
     * Converts a given UserAchievement object into a NebulaUserAchievement object using a builder pattern.
     *
     * @param item the UserAchievement object to be converted
     * @return a new NebulaUserAchievement instance containing the mapped properties from the input UserAchievement
     */
    public static NebulaUserAchievement toItem(final UserAchievement item) {
        return new NebulaUserAchievementBuilderImpl()
                .id(item.achievementId())
                .minValue(item.achievement().minValue())
                .maxValue(item.achievement().maxValue())
                .value(item.value())
                .level(item.level())
                .progress(item.progress())
                .name(item.achievement().name())
                .description(item.achievement().description())
                .iconUrl(item.achievement().iconUrl())
                .levels(item.achievement().levels())
                .build();
    }

}
