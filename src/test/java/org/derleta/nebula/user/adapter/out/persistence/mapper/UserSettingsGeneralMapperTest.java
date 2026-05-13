package org.derleta.nebula.user.adapter.out.persistence.mapper;

import org.derleta.nebula.theme.adapter.out.persistence.entity.ThemeEntity;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsGeneralEntity;
import org.derleta.nebula.user.domain.model.UserSettingsGeneral;
import org.derleta.nebula.theme.domain.model.Theme;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserSettingsGeneralMapperTest {

    @Test
    void toSetting_mapsEntityToModel() {
        ThemeEntity themeEntity = new ThemeEntity(17, "Default");
        UserSettingsGeneralEntity entity = new UserSettingsGeneralEntity(1L, themeEntity);
        UserSettingsGeneral result = UserSettingsGeneralMapper.toSetting(entity);
        assertEquals(1L, result.userId());
        assertNotNull(result.theme());
        assertEquals(17, result.theme().id());
        assertEquals("Default", result.theme().name());
    }

    @Test
    void toEntity_mapsModelToEntity() {
        Theme theme = new Theme(5, "Dark");
        UserSettingsGeneral general = new UserSettingsGeneral(2L, theme);
        UserSettingsGeneralEntity result = UserSettingsGeneralMapper.toEntity(general);
        assertEquals(2L, result.getId());
        assertNotNull(result.getTheme());
        assertEquals(5, result.getTheme().getId());
        assertEquals("Dark", result.getTheme().getName());
    }
}
