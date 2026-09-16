package org.derleta.nebula.user.adapter.out.persistence.mapper;

import org.derleta.nebula.theme.adapter.out.persistence.entity.ThemeEntity;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsEntity;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsGeneralEntity;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsSoundEntity;
import org.derleta.nebula.theme.domain.model.Theme;
import org.derleta.nebula.user.domain.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserSettingsMapperTest {

    @Test
    void toSetting_mapsEntityToModel() {
        ThemeEntity themeEntity = new ThemeEntity(17, "Default");
        UserSettingsGeneralEntity general = new UserSettingsGeneralEntity(1L, themeEntity);
        UserSettingsSoundEntity sound = new UserSettingsSoundEntity(1L, false, true, 80, 70, 60, 50);
        UserSettingsEntity entity = new UserSettingsEntity(1L, general, sound);

        UserSettings result = UserSettingsMapper.toSetting(entity);
        assertEquals(1L, result.userId());
        assertNotNull(result.general());
        assertNotNull(result.sound());
        assertEquals(80, result.sound().volumeMaster());
    }

    @Test
    void toEntity_mapsModelToEntity() {
        Theme theme = new Theme(17, "Default");
        UserSettingsGeneral general = new UserSettingsGeneral(1L, theme);
        UserSettingsSound sound = new UserSettingsSound(1L, false, true, 80, 70, 60, 50);
        UserSettings settings = new UserSettings(1L, general, sound);

        UserSettingsEntity result = UserSettingsMapper.toEntity(settings);
        assertEquals(1L, result.getId());
        assertNotNull(result.getGeneral());
        assertNotNull(result.getSound());
        assertEquals(80, result.getSound().getVolumeMaster());
    }
}
