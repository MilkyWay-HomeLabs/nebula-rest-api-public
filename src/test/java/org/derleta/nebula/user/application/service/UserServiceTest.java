package org.derleta.nebula.user.application.service;

import org.derleta.nebula.user.application.port.in.command.UpdateProfileCommand;
import org.derleta.nebula.user.application.port.out.UserRepositoryPort;
import org.derleta.nebula.user.domain.exception.UserNotFoundException;
import org.derleta.nebula.user.domain.model.*;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepositoryPort userRepository;
    @InjectMocks private UserService service;

    private NebulaUser dummyUser(long id) {
        return new NebulaUser(id, "user", "u@t.com", "F", "L", 30,
                Date.valueOf(LocalDate.of(1993,1,1)), null, null,
                new UserSettings(id, null, null),
                Collections.emptyList(), Collections.emptyList());
    }

    @Test
    void getUser_found_returnsUser() {
        NebulaUser user = dummyUser(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertEquals(user, service.getUser(1L));
    }

    @Test
    void getUser_notFound_throwsUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getUser(99L));
    }

    @Test
    void updateProfile_delegatesToRepository() {
        UpdateProfileCommand cmd = new UpdateProfileCommand(1L,"F","L", Date.valueOf(LocalDate.now()),1,2);
        NebulaUser user = dummyUser(1L);
        when(userRepository.updateProfile(cmd)).thenReturn(user);
        assertEquals(user, service.updateProfile(cmd));
    }

    @Test
    void updateSettings_validIds_delegatesToRepository() {
        UserSettingsGeneral general = new UserSettingsGeneral(1L, null);
        UserSettingsSound sound = new UserSettingsSound(1L, false, true, 80, 70, 60, 50);
        UserSettings settings = new UserSettings(1L, general, sound);
        when(userRepository.updateSettings(settings)).thenReturn(settings);
        assertEquals(settings, service.updateSettings(settings));
    }

    @Test
    void updateSettings_mismatchedGeneralId_throwsIllegalArgument() {
        UserSettingsGeneral general = new UserSettingsGeneral(2L, null);
        UserSettingsSound sound = new UserSettingsSound(1L, false, true, 80, 70, 60, 50);
        UserSettings settings = new UserSettings(1L, general, sound);
        assertThrows(IllegalArgumentException.class, () -> service.updateSettings(settings));
        verifyNoInteractions(userRepository);
    }

    @Test
    void updateSettings_mismatchedSoundId_throwsIllegalArgument() {
        UserSettingsGeneral general = new UserSettingsGeneral(1L, null);
        UserSettingsSound sound = new UserSettingsSound(99L, false, true, 80, 70, 60, 50);
        UserSettings settings = new UserSettings(1L, general, sound);
        assertThrows(IllegalArgumentException.class, () -> service.updateSettings(settings));
        verifyNoInteractions(userRepository);
    }
}
