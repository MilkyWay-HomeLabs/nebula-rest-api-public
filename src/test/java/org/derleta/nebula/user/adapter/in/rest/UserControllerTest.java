package org.derleta.nebula.user.adapter.in.rest;
import org.derleta.nebula.gender.domain.model.Gender;
import org.derleta.nebula.nationality.domain.model.Nationality;
import org.derleta.nebula.nationality.domain.model.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.derleta.nebula.game.domain.model.*;
import org.derleta.nebula.theme.domain.model.*;
import org.derleta.nebula.user.domain.model.*;
import org.derleta.nebula.userachievement.domain.model.*;
import org.derleta.nebula.shared.domain.exception.TokenExpiredException;
import org.derleta.nebula.shared.security.TokenProvider;
import org.derleta.nebula.user.adapter.in.rest.dto.request.ProfileUpdateRequest;
import org.derleta.nebula.user.adapter.in.rest.dto.request.UserSettingsRequest;
import org.derleta.nebula.user.adapter.in.rest.dto.response.NebulaUserResponse;
import org.derleta.nebula.user.adapter.in.rest.dto.response.UserSettingsResponse;
import org.derleta.nebula.user.adapter.in.rest.mapper.UserRestMapper;
import org.derleta.nebula.user.application.port.in.command.UpdateProfileCommand;
import org.derleta.nebula.user.application.port.in.GetUserUseCase;
import org.derleta.nebula.user.application.port.in.UpdateUserProfileUseCase;
import org.derleta.nebula.user.application.port.in.UpdateUserSettingsUseCase;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private GetUserUseCase getUserUseCase;
    @Mock
    private UpdateUserProfileUseCase updateUserProfileUseCase;
    @Mock
    private UpdateUserSettingsUseCase updateUserSettingsUseCase;
    @Mock
    private TokenProvider tokenProvider;
    @InjectMocks
    private UserController userController;
    private final String validToken = "valid-token";
    private final Long userId = 1000L;
    private NebulaUser nebulaUser;
    private NebulaUserResponse nebulaUserResponse;
    private ProfileUpdateRequest profileUpdateRequest;
    private UserSettingsRequest userSettingsRequest;
    private UserSettings userSettings;
    private UserSettingsResponse userSettingsResponse;
    private UpdateProfileCommand updateProfileCommand;
    @BeforeEach
    void setUp() {
        Gender gender = new Gender(1, "Male");
        Region region = new Region(1, "Europe");
        Nationality nationality = new Nationality(1, "Polish", "PL", region);
        Theme theme = new Theme(1, "Dark");
        UserSettingsGeneral general = new UserSettingsGeneral(userId, theme);
        UserSettingsSound sound = new UserSettingsSound(userId, false, true, 80, 70, 60, 50);
        userSettings = new UserSettings(userId, general, sound);
        nebulaUser = new NebulaUser(
                userId, "testuser", "test@example.com", "John", "Doe",
                30, Date.valueOf(LocalDate.of(1993, 1, 1)),
                gender, nationality, userSettings,
                Collections.emptyList(), Collections.emptyList());
        nebulaUserResponse = NebulaUserResponse.builder()
                .id(userId).login("testuser").email("test@example.com")
                .firstName("John").lastName("Doe").age(30)
                .birthDate(Date.valueOf(LocalDate.of(1993, 1, 1)))
                .gender(gender).nationality(nationality).settings(userSettings)
                .games(Collections.emptyList()).achievements(Collections.emptyList())
                .build();
        profileUpdateRequest = ProfileUpdateRequest.builder()
                .userId(userId).firstName("John").lastName("Doe")
                .birthdate(Date.valueOf(LocalDate.of(1993, 1, 1)))
                .nationalityId(1).genderId(1).build();
        updateProfileCommand = new UpdateProfileCommand(
                userId, "John", "Doe",
                Date.valueOf(LocalDate.of(1993, 1, 1)), 1, 1);
        userSettingsRequest = new UserSettingsRequest(userId, null, null);
        userSettingsResponse = UserSettingsResponse.builder()
                .userId(userId).general(general).sound(sound).build();
    }
    @Test
    void getUserData_validToken_returnsNebulaUserResponse() {
        try (MockedStatic<UserRestMapper> mapper = Mockito.mockStatic(UserRestMapper.class)) {
            when(tokenProvider.isValid(validToken)).thenReturn(true);
            when(tokenProvider.getUserId(validToken)).thenReturn(userId);
            when(getUserUseCase.getUser(userId)).thenReturn(nebulaUser);
            mapper.when(() -> UserRestMapper.toResponse(nebulaUser)).thenReturn(nebulaUserResponse);
            ResponseEntity<NebulaUserResponse> response = userController.getUserData(validToken);
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(nebulaUserResponse, response.getBody());
            verify(tokenProvider).isValid(validToken);
            verify(tokenProvider).getUserId(validToken);
            verify(getUserUseCase).getUser(userId);
        }
    }
    @Test
    void getUserData_invalidToken_returnsForbidden() {
        when(tokenProvider.isValid("bad")).thenReturn(false);
        ResponseEntity<NebulaUserResponse> response = userController.getUserData("bad");
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(getUserUseCase, never()).getUser(anyLong());
    }
    @Test
    void getUserData_expiredToken_throwsTokenExpiredException() {
        when(tokenProvider.isValid("expired")).thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));
        assertThrows(TokenExpiredException.class, () -> userController.getUserData("expired"));
        verify(getUserUseCase, never()).getUser(anyLong());
    }
    @Test
    void updateUserProfile_validToken_returnsUpdatedProfile() {
        try (MockedStatic<UserRestMapper> mapper = Mockito.mockStatic(UserRestMapper.class)) {
            when(tokenProvider.isValid(validToken, userId)).thenReturn(true);
            mapper.when(() -> UserRestMapper.toCommand(profileUpdateRequest)).thenReturn(updateProfileCommand);
            when(updateUserProfileUseCase.updateProfile(updateProfileCommand)).thenReturn(nebulaUser);
            mapper.when(() -> UserRestMapper.toResponse(nebulaUser)).thenReturn(nebulaUserResponse);
            ResponseEntity<NebulaUserResponse> response = userController.updateUserProfile(validToken, profileUpdateRequest);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(nebulaUserResponse, response.getBody());
        }
    }
    @Test
    void updateUserProfile_invalidToken_returnsForbidden() {
        when(tokenProvider.isValid("bad", userId)).thenReturn(false);
        ResponseEntity<NebulaUserResponse> response = userController.updateUserProfile("bad", profileUpdateRequest);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(updateUserProfileUseCase, never()).updateProfile(any());
    }
    @Test
    void updateUserProfile_expiredToken_throwsTokenExpiredException() {
        when(tokenProvider.isValid(eq("expired"), anyLong())).thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));
        assertThrows(TokenExpiredException.class,
                () -> userController.updateUserProfile("expired", profileUpdateRequest));
        verify(updateUserProfileUseCase, never()).updateProfile(any());
    }
    @Test
    void updateUserSettings_validToken_returnsUpdatedSettings() {
        try (MockedStatic<UserRestMapper> mapper = Mockito.mockStatic(UserRestMapper.class)) {
            when(tokenProvider.isValid(validToken, userId)).thenReturn(true);
            mapper.when(() -> UserRestMapper.toSettings(userSettingsRequest)).thenReturn(userSettings);
            when(updateUserSettingsUseCase.updateSettings(userSettings)).thenReturn(userSettings);
            mapper.when(() -> UserRestMapper.toSettingsResponse(userSettings)).thenReturn(userSettingsResponse);
            ResponseEntity<UserSettingsResponse> response = userController.updateUserSettings(validToken, userSettingsRequest);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(userSettingsResponse, response.getBody());
        }
    }
    @Test
    void updateUserSettings_invalidToken_returnsForbidden() {
        when(tokenProvider.isValid("bad", userId)).thenReturn(false);
        ResponseEntity<UserSettingsResponse> response = userController.updateUserSettings("bad", userSettingsRequest);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(updateUserSettingsUseCase, never()).updateSettings(any());
    }
    @Test
    void updateUserSettings_expiredToken_throwsTokenExpiredException() {
        when(tokenProvider.isValid(eq("expired"), anyLong())).thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));
        assertThrows(TokenExpiredException.class,
                () -> userController.updateUserSettings("expired", userSettingsRequest));
        verify(updateUserSettingsUseCase, never()).updateSettings(any());
    }
}
