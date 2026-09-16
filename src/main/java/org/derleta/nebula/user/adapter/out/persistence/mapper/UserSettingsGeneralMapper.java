package org.derleta.nebula.user.adapter.out.persistence.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.theme.adapter.out.persistence.mapper.ThemeMapper;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsGeneralEntity;
import org.derleta.nebula.user.domain.builder.impl.UserSettingsGeneralBuilderImpl;
import org.derleta.nebula.user.domain.model.UserSettingsGeneral;


/**
 * Utility class for mapping between {@link UserSettingsGeneralEntity} and {@link UserSettingsGeneral}.
 * The class serves as an abstraction layer for transforming objects between different layers
 * of the application by providing static, reusable methods.
 * <p>
 * This class is non-instantiable and marked as final to prevent subclassing.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserSettingsGeneralMapper {

    /**
     * Converts a {@link UserSettingsGeneralEntity} object to a {@link UserSettingsGeneral} object.
     * This method maps the attributes of the given entity to the corresponding model
     * using a builder implementation for transformation.
     *
     * @param entity the {@link UserSettingsGeneralEntity} object to be transformed
     * @return a {@link UserSettingsGeneral} object constructed from the provided entity
     */
    public static UserSettingsGeneral toSetting(final UserSettingsGeneralEntity entity) {
        return new UserSettingsGeneralBuilderImpl()
                .userId(entity.getId())
                .theme(ThemeMapper.toTheme(entity.getTheme()))
                .build();
    }


    /**
     * Converts a UserSettingsGeneral object to a UserSettingsGeneralEntity object.
     * This method maps the properties of the provided UserSettingsGeneral record to
     * the corresponding fields of a new UserSettingsGeneralEntity instance.
     *
     * @param item the UserSettingsGeneral object to be converted
     * @return a UserSettingsGeneralEntity object constructed from the provided UserSettingsGeneral object
     */
    public static UserSettingsGeneralEntity toEntity(final UserSettingsGeneral item) {
        UserSettingsGeneralEntity entity = new UserSettingsGeneralEntity();
        entity.setId(item.userId());
        entity.setTheme(
                ThemeMapper.toEntity(item.theme())
        );
        return entity;
    }

}
