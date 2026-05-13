package org.derleta.nebula.domain.mapper;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsEntity;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsGeneralEntity;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsSoundEntity;
import org.derleta.nebula.theme.domain.model.Theme;
import org.derleta.nebula.user.domain.model.UserSettings;
import org.derleta.nebula.user.domain.model.UserSettingsGeneral;
import org.derleta.nebula.user.domain.model.UserSettingsSound;

import org.derleta.nebula.user.adapter.out.persistence.mapper.UserSettingsMapper;
import org.derleta.nebula.user.adapter.out.persistence.mapper.UserSettingsGeneralMapper;
import org.derleta.nebula.user.adapter.out.persistence.mapper.UserSettingsSoundMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class UserSettingsMapperTest {

    @Test
    void toSetting_validEntity_returnsUserSettings() {
        // Arrange
        UserSettingsEntity entity = new UserSettingsEntity();
        entity.setId(1000L);

        UserSettingsGeneralEntity generalEntity = new UserSettingsGeneralEntity();
        entity.setGeneral(generalEntity);

        UserSettingsSoundEntity soundEntity = new UserSettingsSoundEntity();
        entity.setSound(soundEntity);

        Theme theme = new Theme(1, "Dark");
        UserSettingsGeneral general = new UserSettingsGeneral(1000L, theme);
        UserSettingsSound sound = new UserSettingsSound(1000L, false, true, 80, 70, 60, 50);

        try (MockedStatic<UserSettingsGeneralMapper> generalMapperMock = Mockito.mockStatic(UserSettingsGeneralMapper.class);
             MockedStatic<UserSettingsSoundMapper> soundMapperMock = Mockito.mockStatic(UserSettingsSoundMapper.class)) {

            // Mock the static method calls
            generalMapperMock.when(() -> UserSettingsGeneralMapper.toSetting(any()))
                    .thenReturn(general);
            soundMapperMock.when(() -> UserSettingsSoundMapper.toSetting(any()))
                    .thenReturn(sound);

            // Act
            UserSettings result = UserSettingsMapper.toSetting(entity);

            // Assert
            assertNotNull(result);
            assertEquals(1000L, result.userId());
            assertEquals(general, result.general());
            assertEquals(sound, result.sound());

            generalMapperMock.verify(() -> UserSettingsGeneralMapper.toSetting(generalEntity));
            soundMapperMock.verify(() -> UserSettingsSoundMapper.toSetting(soundEntity));
        }
    }

    @Test
    void toSetting_nullEntity_throwsNullPointerException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> UserSettingsMapper.toSetting(null)
        );
    }

    @Test
    void toSetting_entityWithNullFields_returnsUserSettingsWithNullFields() {
        // Arrange
        UserSettingsEntity entity = new UserSettingsEntity();
        entity.setId(1000L);

        try (MockedStatic<UserSettingsGeneralMapper> generalMapperMock = Mockito.mockStatic(UserSettingsGeneralMapper.class);
             MockedStatic<UserSettingsSoundMapper> soundMapperMock = Mockito.mockStatic(UserSettingsSoundMapper.class)) {

            // Mock the static method calls
            generalMapperMock.when(() -> UserSettingsGeneralMapper.toSetting(any()))
                    .thenReturn(null);
            soundMapperMock.when(() -> UserSettingsSoundMapper.toSetting(any()))
                    .thenReturn(null);

            // Act
            UserSettings result = UserSettingsMapper.toSetting(entity);

            // Assert
            assertNotNull(result);
            assertEquals(1000L, result.userId());
            assertNull(result.general());
            assertNull(result.sound());

            generalMapperMock.verify(() -> UserSettingsGeneralMapper.toSetting(null));
            soundMapperMock.verify(() -> UserSettingsSoundMapper.toSetting(null));
        }
    }


    @Test
    void toEntity_validUserSettings_returnsUserSettingsEntity() {
        // Arrange
        Theme theme = new Theme(1, "Dark");
        UserSettingsGeneral general = new UserSettingsGeneral(1000L, theme);
        UserSettingsSound sound = new UserSettingsSound(1000L, false, true, 80, 70, 60, 50);
        UserSettings userSettings = new UserSettings(1000L, general, sound);

        UserSettingsGeneralEntity generalEntity = new UserSettingsGeneralEntity();
        UserSettingsSoundEntity soundEntity = new UserSettingsSoundEntity();

        try (MockedStatic<UserSettingsGeneralMapper> generalMapperMock = Mockito.mockStatic(UserSettingsGeneralMapper.class);
             MockedStatic<UserSettingsSoundMapper> soundMapperMock = Mockito.mockStatic(UserSettingsSoundMapper.class)) {

            // Mock the static method calls
            generalMapperMock.when(() -> UserSettingsGeneralMapper.toEntity(any()))
                    .thenReturn(generalEntity);
            soundMapperMock.when(() -> UserSettingsSoundMapper.toEntity(any()))
                    .thenReturn(soundEntity);

            // Act
            UserSettingsEntity result = UserSettingsMapper.toEntity(userSettings);

            // Assert
            assertNotNull(result);
            assertEquals(1000L, result.getId());
            assertEquals(generalEntity, result.getGeneral());
            assertEquals(soundEntity, result.getSound());

            generalMapperMock.verify(() -> UserSettingsGeneralMapper.toEntity(general));
            soundMapperMock.verify(() -> UserSettingsSoundMapper.toEntity(sound));
        }
    }

    @Test
    void toEntity_nullUserSettings_throwsNullPointerException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> UserSettingsMapper.toEntity(null)
        );
    }

    @Test
    void toEntity_userSettingsWithNullFields_returnsUserSettingsEntityWithNullFields() {
        // Arrange
        UserSettings userSettings = new UserSettings(1000L, null, null);

        try (MockedStatic<UserSettingsGeneralMapper> generalMapperMock = Mockito.mockStatic(UserSettingsGeneralMapper.class);
             MockedStatic<UserSettingsSoundMapper> soundMapperMock = Mockito.mockStatic(UserSettingsSoundMapper.class)) {

            // Mock the static method calls
            generalMapperMock.when(() -> UserSettingsGeneralMapper.toEntity(any()))
                    .thenReturn(null);
            soundMapperMock.when(() -> UserSettingsSoundMapper.toEntity(any()))
                    .thenReturn(null);

            // Act
            UserSettingsEntity result = UserSettingsMapper.toEntity(userSettings);

            // Assert
            assertNotNull(result);
            assertEquals(1000L, result.getId());
            assertNull(result.getGeneral());
            assertNull(result.getSound());

            generalMapperMock.verify(() -> UserSettingsGeneralMapper.toEntity(null));
            soundMapperMock.verify(() -> UserSettingsSoundMapper.toEntity(null));
        }
    }

}
