package org.derleta.nebula.user.adapter.in.rest.mapper;
import org.derleta.nebula.userachievement.domain.mapper.NebulaUserAchievementMapper;
import org.derleta.nebula.game.domain.model.*;
import org.derleta.nebula.theme.domain.model.*;
import org.derleta.nebula.user.domain.model.*;
import org.derleta.nebula.userachievement.domain.model.*;
import org.derleta.nebula.gender.domain.model.Gender;
import org.derleta.nebula.nationality.domain.model.Nationality;
import org.derleta.nebula.nationality.domain.model.Region;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.derleta.nebula.user.adapter.in.rest.dto.request.ProfileUpdateRequest;
import org.derleta.nebula.user.adapter.in.rest.dto.request.UserSettingsGeneralRequest;
import org.derleta.nebula.user.adapter.in.rest.dto.request.UserSettingsRequest;
import org.derleta.nebula.user.adapter.in.rest.dto.request.UserSettingsSoundRequest;
import org.derleta.nebula.user.adapter.in.rest.dto.response.NebulaUserResponse;
import org.derleta.nebula.user.adapter.in.rest.dto.response.UserSettingsResponse;
import org.derleta.nebula.user.application.port.in.command.UpdateProfileCommand;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
class UserRestMapperTest {
    private final Long userId = 1000L;
    @Test
    void toCommand_validRequest_returnsUpdateProfileCommand() {
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .userId(userId).firstName("John").lastName("Doe")
                .birthdate(Date.valueOf(LocalDate.of(1993, 1, 1)))
                .nationalityId(1).genderId(2).build();
        UpdateProfileCommand command = UserRestMapper.toCommand(request);
        assertNotNull(command);
        assertEquals(userId, command.userId());
        assertEquals("John", command.firstName());
        assertEquals("Doe", command.lastName());
        assertEquals(1, command.nationalityId());
        assertEquals(2, command.genderId());
    }
    @Test
    void toSettings_withGeneralAndSound_returnsUserSettings() {
        UserSettingsGeneralRequest generalReq = new UserSettingsGeneralRequest(userId, new Theme(1, "Dark"));
        UserSettingsSoundRequest soundReq = new UserSettingsSoundRequest(userId, false, true, 80, 70, 60, 50);
        UserSettingsRequest request = new UserSettingsRequest(userId, generalReq, soundReq);
        UserSettings result = UserRestMapper.toSettings(request);
        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertNotNull(result.general());
        assertEquals(userId, result.general().userId());
        assertNotNull(result.sound());
        assertEquals(userId, result.sound().userId());
        assertFalse(result.sound().muted());
        assertTrue(result.sound().battleCry());
    }
    @Test
    void toSettings_withNullGeneralAndSound_returnsUserSettingsWithNulls() {
        UserSettingsRequest request = new UserSettingsRequest(userId, null, null);
        UserSettings result = UserRestMapper.toSettings(request);
        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertNull(result.general());
        assertNull(result.sound());
    }
    @Test
    void toResponse_validUser_returnsNebulaUserResponseWithSelfLink() {
        Gender gender = new Gender(1, "Male");
        Region region = new Region(1, "Europe");
        Nationality nationality = new Nationality(1, "Polish", "PL", region);
        UserSettings settings = new UserSettings(userId, null, null);
        NebulaUserAchievement achievement = new NebulaUserAchievement(
                1, "Ach", 0, 100, 50, 1, "50%", "desc", "https://example.com/icon.png", null);
        NebulaUser user = new NebulaUser(
                userId, "testuser", "test@example.com", "John", "Doe",
                30, Date.valueOf(LocalDate.of(1993, 1, 1)),
                gender, nationality, settings,
                Collections.emptyList(), Collections.emptyList());
        try (MockedStatic<NebulaUserAchievementMapper> mockedMapper =
                     Mockito.mockStatic(NebulaUserAchievementMapper.class)) {
            mockedMapper.when(() -> NebulaUserAchievementMapper.toList(any()))
                    .thenReturn(List.of(achievement));
            NebulaUserResponse response = UserRestMapper.toResponse(user);
            assertNotNull(response);
            assertEquals(userId, response.getId());
            assertEquals("testuser", response.getLogin());
            assertEquals("test@example.com", response.getEmail());
            assertEquals("John", response.getFirstName());
            assertEquals("Doe", response.getLastName());
            assertEquals(30, response.getAge());
            assertEquals(gender, response.getGender());
            assertEquals(nationality, response.getNationality());
            assertFalse(response.getLinks().isEmpty());
            assertTrue(response.getLinks().stream().anyMatch(l -> l.getRel().value().equals("self")));
        }
    }
    @Test
    void toSettingsResponse_validSettings_returnsUserSettingsResponse() {
        Theme theme = new Theme(1, "Dark");
        UserSettingsGeneral general = new UserSettingsGeneral(userId, theme);
        UserSettingsSound sound = new UserSettingsSound(userId, false, true, 80, 70, 60, 50);
        UserSettings settings = new UserSettings(userId, general, sound);
        UserSettingsResponse response = UserRestMapper.toSettingsResponse(settings);
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(general, response.getGeneral());
        assertEquals(sound, response.getSound());
    }
}
