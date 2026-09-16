package org.derleta.nebula.user.adapter.out.persistence.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.user.domain.builder.impl.UserSettingsBuilderImpl;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsEntity;
import org.derleta.nebula.user.domain.model.UserSettings;


/**
 * Utility class for mapping between {@link UserSettingsEntity} and {@link UserSettings}.
 * This class provides methods for converting entities from the persistence layer, request objects from the API layer,
 * and business model objects to appropriate formats for processing and storage.
 * <p>
 * The class is designed as a final utility class with private constructor and static methods
 * to ensure non-instantiability and ease of access to mapping functions.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserSettingsMapper {

    /**
     * Converts a {@link UserSettingsEntity} object to a {@link UserSettings} object.
     * This method maps the attributes of the given entity to the corresponding
     * model using a builder pattern to construct the final object.
     *
     * @param entity the {@link UserSettingsEntity} object to be converted
     * @return a {@link UserSettings} object constructed from the provided entity
     */
    public static UserSettings toSetting(final UserSettingsEntity entity) {
        return new UserSettingsBuilderImpl()
                .userId(entity.getId())
                .userSettingsGeneral(UserSettingsGeneralMapper.toSetting(entity.getGeneral()))
                .userSettingsSound(UserSettingsSoundMapper.toSetting(entity.getSound()))
                .build();
    }


    /**
     * Converts a UserSettings record to a UserSettingsEntity object.
     * Maps the attributes of the UserSettings instance to a new UserSettingsEntity instance,
     * including its nested sound and general settings.
     *
     * @param item the UserSettings object to be converted
     * @return a UserSettingsEntity object constructed from the provided UserSettings instance
     */
    public static UserSettingsEntity toEntity(final UserSettings item) {
        UserSettingsEntity entity = new UserSettingsEntity();
        entity.setId(item.userId());
        entity.setSound(UserSettingsSoundMapper.toEntity(item.sound()));
        entity.setGeneral(UserSettingsGeneralMapper.toEntity(item.general()));
        return entity;
    }

}
