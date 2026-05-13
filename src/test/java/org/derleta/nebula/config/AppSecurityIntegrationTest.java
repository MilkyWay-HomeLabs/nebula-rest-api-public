package org.derleta.nebula.config;

import org.derleta.nebula.config.security.JwtAuthenticationFilter;
import org.derleta.nebula.health.adapter.in.rest.HealthController;
import org.derleta.nebula.health.application.port.in.CheckHealthUseCase;
import org.derleta.nebula.health.application.port.in.GetAppVersionUseCase;
import org.derleta.nebula.shared.security.TokenProvider;
import org.derleta.nebula.shared.security.model.Role;
import org.derleta.nebula.user.adapter.in.rest.UserController;
import org.derleta.nebula.user.application.port.in.GetUserUseCase;
import org.derleta.nebula.user.application.port.in.UpdateUserProfileUseCase;
import org.derleta.nebula.user.application.port.in.UpdateUserSettingsUseCase;
import org.derleta.nebula.user.application.port.in.command.UpdateProfileCommand;
import org.derleta.nebula.user.domain.model.NebulaUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {HealthController.class, UserController.class})
@Import({AppConfig.class, JwtAuthenticationFilter.class})
@TestPropertySource(properties = "cors.allowed-origin-patterns=https://milkyway.test")
class AppSecurityIntegrationTest {

    private static final String VALID_TOKEN = "valid-token";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckHealthUseCase checkHealthUseCase;

    @MockitoBean
    private GetAppVersionUseCase getAppVersionUseCase;

    @MockitoBean
    private GetUserUseCase getUserUseCase;

    @MockitoBean
    private UpdateUserProfileUseCase updateUserProfileUseCase;

    @MockitoBean
    private UpdateUserSettingsUseCase updateUserSettingsUseCase;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Test
    void hello_shouldRemainPublic() throws Exception {
        when(checkHealthUseCase.check()).thenReturn("hello");

        mockMvc.perform(get("/api/v1/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello"));
    }

    @Test
    void users_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void users_withValidAccessTokenCookie_shouldReturn200() throws Exception {
        NebulaUser user = new NebulaUser(
                1L,
                "tester",
                "tester@example.com",
                "Test",
                "User",
                0,
                null,
                null,
                null,
                null,
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(tokenProvider.isValid(VALID_TOKEN)).thenReturn(true);
        when(tokenProvider.getUserId(VALID_TOKEN)).thenReturn(1L);
        when(tokenProvider.getEmail(VALID_TOKEN)).thenReturn("tester@example.com");
        when(tokenProvider.getRoles(VALID_TOKEN)).thenReturn(Set.of(new Role(1, "ROLE_USER")));
        when(getUserUseCase.getUser(1L)).thenReturn(user);

        mockMvc.perform(get("/api/v1/users")
                        .cookie(new jakarta.servlet.http.Cookie("accessToken", VALID_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.login").value("tester"));
    }

    @Test
    void updateProfile_withValidTokenButWithoutCsrf_shouldReturn403() throws Exception {
        when(tokenProvider.isValid(VALID_TOKEN)).thenReturn(true);
        when(tokenProvider.getUserId(VALID_TOKEN)).thenReturn(1L);
        when(tokenProvider.getEmail(VALID_TOKEN)).thenReturn("tester@example.com");
        when(tokenProvider.getRoles(VALID_TOKEN)).thenReturn(Set.of(new Role(1, "ROLE_USER")));

        mockMvc.perform(patch("/api/v1/users/profile")
                        .cookie(new jakarta.servlet.http.Cookie("accessToken", VALID_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 1,
                                  "firstName": "Test",
                                  "lastName": "User",
                                  "nationalityId": 1,
                                  "genderId": 1
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));

        verify(updateUserProfileUseCase, never()).updateProfile(any());
    }

    @Test
    void updateProfile_withValidTokenAndCsrf_shouldReturn200() throws Exception {
        NebulaUser updatedUser = new NebulaUser(
                1L,
                "tester",
                "tester@example.com",
                "Updated",
                "User",
                0,
                null,
                null,
                null,
                null,
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(tokenProvider.isValid(VALID_TOKEN)).thenReturn(true);
        when(tokenProvider.getUserId(VALID_TOKEN)).thenReturn(1L);
        when(tokenProvider.getEmail(VALID_TOKEN)).thenReturn("tester@example.com");
        when(tokenProvider.getRoles(VALID_TOKEN)).thenReturn(Set.of(new Role(1, "ROLE_USER")));
        when(tokenProvider.isValid(VALID_TOKEN, 1L)).thenReturn(true);
        when(updateUserProfileUseCase.updateProfile(any(UpdateProfileCommand.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/v1/users/profile")
                        .with(csrf().asHeader())
                        .cookie(new jakarta.servlet.http.Cookie("accessToken", VALID_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 1,
                                  "firstName": "Updated",
                                  "lastName": "User",
                                  "nationalityId": 1,
                                  "genderId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Updated"));

        verify(updateUserProfileUseCase).updateProfile(any(UpdateProfileCommand.class));
        verify(tokenProvider).isValid(eq(VALID_TOKEN), eq(1L));
    }
}

