package org.derleta.nebula.user.adapter.out.persistence;

import org.derleta.nebula.gender.adapter.out.persistence.entity.GenderJpaEntity;
import org.derleta.nebula.nationality.adapter.out.persistence.entity.NationalityJpaEntity;
import org.derleta.nebula.nationality.adapter.out.persistence.entity.RegionJpaEntity;
import org.derleta.nebula.theme.adapter.out.persistence.entity.ThemeEntity;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserEntity;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsEntity;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsGeneralEntity;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsSoundEntity;
import org.derleta.nebula.user.adapter.out.persistence.jpa.UserDataJpaRepository;
import org.derleta.nebula.user.adapter.out.persistence.jpa.UserSettingsJpaRepository;
import org.derleta.nebula.user.application.port.in.command.UpdateProfileCommand;
import org.derleta.nebula.user.domain.model.NebulaUser;
import org.derleta.nebula.user.domain.model.UserSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserJpaAdapterTest {

    @Mock
    private UserDataJpaRepository userRepository;

    @Mock
    private UserSettingsJpaRepository userSettingsRepository;

    @InjectMocks
    private UserJpaAdapter adapter;

    private UserEntity buildFullEntity(long id) {
        GenderJpaEntity gender = new GenderJpaEntity(1, "Male");
        RegionJpaEntity region = new RegionJpaEntity(1, "Europe");
        NationalityJpaEntity nationality = new NationalityJpaEntity(1, "Polish", "POL", region);

        ThemeEntity theme = new ThemeEntity(17, "Default");
        UserSettingsGeneralEntity general = new UserSettingsGeneralEntity(id, theme);
        UserSettingsSoundEntity sound = new UserSettingsSoundEntity(id, false, true, 100, 100, 100, 100);
        UserSettingsEntity settings = new UserSettingsEntity(id, general, sound);

        UserEntity entity = new UserEntity();
        entity.setId(id);
        entity.setEmail("user@test.com");
        entity.setLogin("testuser");
        entity.setFirstName("John");
        entity.setLastName("Doe");
        entity.setAge(30);
        entity.setBirthDate(Date.valueOf(LocalDate.of(1993, 1, 1)));
        entity.setGender(gender);
        entity.setNationality(nationality);
        entity.setSettings(settings);
        entity.setGames(Collections.emptyList());
        entity.setAchievements(Collections.emptyList());
        return entity;
    }

    @Test
    void findById_found_returnsUser() {
        UserEntity entity = buildFullEntity(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<NebulaUser> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
        assertEquals("testuser", result.get().login());
    }

    @Test
    void findById_found_withNullAge_returnsUserWithZeroAge() {
        UserEntity entity = buildFullEntity(2L);
        entity.setAge(null); // triggers the null-age branch in NebulaUserMapper
        when(userRepository.findById(2L)).thenReturn(Optional.of(entity));

        Optional<NebulaUser> result = adapter.findById(2L);

        assertTrue(result.isPresent());
        assertEquals(0, result.get().age());
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertTrue(adapter.findById(99L).isEmpty());
    }

    @Test
    void updateProfile_callsRepositoryAndReturnsUser() {
        UpdateProfileCommand cmd = new UpdateProfileCommand(
                1L, "Jane", "Smith", Date.valueOf(LocalDate.of(1990, 6, 15)), 1, 2);
        UserEntity entity = buildFullEntity(1L);
        when(userRepository.getReferenceById(1L)).thenReturn(entity);
        doNothing().when(userRepository).updateUserDetails(
                anyLong(), any(), any(), any(), anyInt(), anyInt());

        NebulaUser result = adapter.updateProfile(cmd);

        assertNotNull(result);
        verify(userRepository).updateUserDetails(1L, "Jane", "Smith",
                Date.valueOf(LocalDate.of(1990, 6, 15)), 1, 2);
    }

    @Test
    void updateSettings_savesAndReturnsSettings() {
        ThemeEntity theme = new ThemeEntity(17, "Default");
        UserSettingsGeneralEntity general = new UserSettingsGeneralEntity(1L, theme);
        UserSettingsSoundEntity sound = new UserSettingsSoundEntity(1L, false, true, 100, 100, 100, 100);
        UserSettingsEntity savedEntity = new UserSettingsEntity(1L, general, sound);

        when(userSettingsRepository.save(any())).thenReturn(savedEntity);
        doNothing().when(userRepository).updateUserUpdatedAt(anyLong());

        UserSettings input = new UserSettings(1L,
                new org.derleta.nebula.user.domain.model.UserSettingsGeneral(1L,
                        new org.derleta.nebula.theme.domain.model.Theme(17, "Default")),
                new org.derleta.nebula.user.domain.model.UserSettingsSound(1L, false, true, 100, 100, 100, 100));

        UserSettings result = adapter.updateSettings(input);

        assertNotNull(result);
        verify(userSettingsRepository).save(any());
        verify(userRepository).updateUserUpdatedAt(1L);
    }
}
