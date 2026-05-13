package org.derleta.nebula.user.adapter.out.persistence.mapper;

import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsSoundEntity;
import org.derleta.nebula.user.domain.model.UserSettingsSound;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserSettingsSoundMapperTest {

    @Test
    void toSetting_mapsAllFieldsFromEntity() {
        UserSettingsSoundEntity e = new UserSettingsSoundEntity(1L, true, false, 80, 60, 70, 50);
        UserSettingsSound result = UserSettingsSoundMapper.toSetting(e);
        assertEquals(1L, result.userId());
        assertTrue(result.muted());
        assertFalse(result.battleCry());
        assertEquals(80, result.volumeMaster());
        assertEquals(60, result.volumeMusic());
        assertEquals(50, result.volumeVoices());
        assertEquals(70, result.volumeEffects());
    }

    @Test
    void toSetting_nullMutedAndBattleCry_usesDefaults() {
        UserSettingsSoundEntity e = new UserSettingsSoundEntity(2L, null, null, null, null, null, null);
        UserSettingsSound result = UserSettingsSoundMapper.toSetting(e);
        assertFalse(result.muted());
        assertTrue(result.battleCry());
        assertEquals(100, result.volumeMaster());
        assertEquals(100, result.volumeMusic());
    }

    @Test
    void toSetting_volumeBelowMin_clampsToZero() {
        UserSettingsSoundEntity e = new UserSettingsSoundEntity(3L, false, true, -5, -1, -100, -50);
        UserSettingsSound result = UserSettingsSoundMapper.toSetting(e);
        assertEquals(0, result.volumeMaster());
        assertEquals(0, result.volumeMusic());
    }

    @Test
    void toSetting_volumeAboveMax_clampsTo100() {
        UserSettingsSoundEntity e = new UserSettingsSoundEntity(4L, false, true, 200, 150, 101, 999);
        UserSettingsSound result = UserSettingsSoundMapper.toSetting(e);
        assertEquals(100, result.volumeMaster());
        assertEquals(100, result.volumeMusic());
        assertEquals(100, result.volumeVoices());
        assertEquals(100, result.volumeEffects());
    }

    @Test
    void toEntity_mapsAllFieldsFromModel() {
        UserSettingsSound sound = new UserSettingsSound(5L, true, false, 75, 50, 60, 40);
        UserSettingsSoundEntity result = UserSettingsSoundMapper.toEntity(sound);
        assertEquals(5L, result.getId());
        assertTrue(result.getMuted());
        assertFalse(result.getBattleCry());
        assertEquals(75, result.getVolumeMaster());
    }
}
