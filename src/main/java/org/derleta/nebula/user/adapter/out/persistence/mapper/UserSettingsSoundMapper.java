package org.derleta.nebula.user.adapter.out.persistence.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.user.domain.builder.impl.UserSettingsSoundBuilderImpl;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsSoundEntity;
import org.derleta.nebula.user.domain.model.UserSettingsSound;


/**
 * Utility class for mapping between UserSettingsSound, UserSettingsSoundEntity,
 * and UserSettingsSoundRequest objects. This class provides methods to convert
 * data between these representations to facilitate data transfer and persistence.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserSettingsSoundMapper {

    private static final int DEFAULT_VOLUME = 100;
    private static final int MAX_VOLUME = 100;
    private static final int MIN_VOLUME = 0;

    /**
     * Converts a UserSettingsSoundEntity object to a UserSettingsSound object.
     * This method maps the fields of the provided UserSettingsSoundEntity
     * instance to the corresponding attributes of a UserSettingsSound object
     * using a builder implementation.
     *
     * @param entity the UserSettingsSoundEntity object to be converted
     * @return a UserSettingsSound object constructed from the provided UserSettingsSoundEntity object
     */
    public static UserSettingsSound toSetting(final UserSettingsSoundEntity entity) {
        return new UserSettingsSoundBuilderImpl()
                .userId(entity.getId())
                .muted(entity.getMuted() != null ? entity.getMuted() : false)
                .battleCry(entity.getBattleCry() != null ? entity.getBattleCry() : true)
                .volumeMaster(getVolume(entity.getVolumeMaster()))
                .volumeMusic(getVolume(entity.getVolumeMusic()))
                .volumeVoices(getVolume(entity.getVolumeVoices()))
                .volumeEffects(getVolume(entity.getVolumeEffects()))
                .build();
    }


    /**
     * Converts a UserSettingsSound object to a UserSettingsSoundEntity object.
     * This method maps the fields of the provided UserSettingsSound record
     * to the corresponding attributes of a new UserSettingsSoundEntity instance.
     *
     * @param item the UserSettingsSound record to be converted
     * @return a UserSettingsSoundEntity object containing the mapped data
     */
    public static UserSettingsSoundEntity toEntity(final UserSettingsSound item) {
        UserSettingsSoundEntity entity = new UserSettingsSoundEntity();
        entity.setId(item.userId());
        entity.setMuted(item.muted());
        entity.setBattleCry(item.battleCry());
        entity.setVolumeMaster(item.volumeMaster());
        entity.setVolumeMusic(item.volumeMusic());
        entity.setVolumeVoices(item.volumeVoices());
        entity.setVolumeEffects(item.volumeEffects());
        return entity;
    }

    /**
     * Determines and returns the appropriate volume value based on the provided input.
     * If the input volume is null, the default volume is returned.
     * If the input volume is below the minimum allowed value, the minimum volume is returned.
     * If the input volume exceeds the maximum allowed value, the maximum volume is returned.
     * Otherwise, the input volume is returned unchanged.
     *
     * @param volume the input volume value, which could be null
     * @return the valid volume value within the defined range, or the default volume if the input is null
     */
    private static int getVolume(Integer volume){
        if (volume == null) {
            return DEFAULT_VOLUME;
        } else if (volume < MIN_VOLUME) {
            return MIN_VOLUME;
        } else if (volume > MAX_VOLUME) {
            return MAX_VOLUME;
        } else {
            return volume;
        }
    }

}
