package org.derleta.nebula.userachievement.adapter.in.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.derleta.nebula.userachievement.domain.model.Achievement;
import org.derleta.nebula.userachievement.domain.model.AchievementLevel;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;
import org.derleta.nebula.shared.domain.exception.TokenExpiredException;
import org.derleta.nebula.shared.security.TokenProvider;
import org.derleta.nebula.userachievement.adapter.in.rest.dto.UserAchievementResponse;
import org.derleta.nebula.userachievement.application.port.in.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAchievementControllerTest {

    @Mock
    private GetUserAchievementUseCase getUserAchievementUseCase;

    @Mock
    private GetUserAchievementsListUseCase getUserAchievementsListUseCase;

    @Mock
    private GetUserAchievementsPageUseCase getUserAchievementsPageUseCase;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private UserAchievementController controller;

    private final String validToken = "valid-token";
    private final Long userId = 1000L;
    private final Integer achievementId = 1;
    private UserAchievement userAchievement;
    private List<UserAchievement> userAchievements;

    @BeforeEach
    void setUp() {
        List<AchievementLevel> levels = Arrays.asList(
                new AchievementLevel(1, 25),
                new AchievementLevel(2, 50)
        );
        Achievement achievement = new Achievement(
                achievementId, "Achievement 1", 0, 100,
                "Description 1", "https://example.com/icon1.png", levels);
        userAchievement = new UserAchievement(userId, achievementId, 30, 1, "30,00%", achievement);

        Achievement achievement2 = new Achievement(
                2, "Achievement 2", 0, 200,
                "Description 2", "https://example.com/icon2.png", levels);
        UserAchievement userAchievement2 = new UserAchievement(userId, 2, 60, 2, "60,00%", achievement2);
        userAchievements = Arrays.asList(userAchievement, userAchievement2);
    }

    // ---- get ----------------------------------------------------------------

    @Test
    void get_validTokenAndMatchingUserId_returnsOk() {
        when(tokenProvider.isValid(validToken)).thenReturn(true);
        when(tokenProvider.getUserId(validToken)).thenReturn(userId);
        when(getUserAchievementUseCase.getUserAchievement(userId, achievementId))
                .thenReturn(userAchievement);

        ResponseEntity<UserAchievementResponse> response =
                controller.get(userId, achievementId, validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getUserId());
        verify(getUserAchievementUseCase).getUserAchievement(userId, achievementId);
    }

    @Test
    void get_validTokenButDifferentUserId_returnsUnauthorized() {
        when(tokenProvider.isValid(validToken)).thenReturn(true);
        when(tokenProvider.getUserId(validToken)).thenReturn(userId);

        ResponseEntity<UserAchievementResponse> response =
                controller.get(2000L, achievementId, validToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(getUserAchievementUseCase, never()).getUserAchievement(anyLong(), anyInt());
    }

    @Test
    void get_invalidToken_returnsForbidden() {
        when(tokenProvider.isValid("bad")).thenReturn(false);

        ResponseEntity<UserAchievementResponse> response =
                controller.get(userId, achievementId, "bad");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void get_expiredToken_throwsTokenExpiredException() {
        when(tokenProvider.isValid("exp")).thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));

        assertThrows(TokenExpiredException.class,
                () -> controller.get(userId, achievementId, "exp"));
    }

    // ---- getList ------------------------------------------------------------

    @Test
    void getList_validToken_returnsOk() {
        when(tokenProvider.isValid(validToken)).thenReturn(true);
        when(tokenProvider.getUserId(validToken)).thenReturn(userId);
        when(getUserAchievementsListUseCase.getUserAchievementsList(userId))
                .thenReturn(userAchievements);

        ResponseEntity<List<UserAchievementResponse>> response =
                controller.getList(validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(getUserAchievementsListUseCase).getUserAchievementsList(userId);
    }

    @Test
    void getList_invalidToken_returnsForbidden() {
        when(tokenProvider.isValid("bad")).thenReturn(false);

        ResponseEntity<List<UserAchievementResponse>> response = controller.getList("bad");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(getUserAchievementsListUseCase, never()).getUserAchievementsList(anyLong());
    }

    @Test
    void getList_expiredToken_throwsTokenExpiredException() {
        when(tokenProvider.isValid("exp")).thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));

        assertThrows(TokenExpiredException.class, () -> controller.getList("exp"));
    }

    // ---- getPage ------------------------------------------------------------

    @Test
    void getPage_validToken_returnsOk() {
        Page<UserAchievement> page = new PageImpl<>(userAchievements, PageRequest.of(0, 10), 2);
        when(tokenProvider.isValid(validToken)).thenReturn(true);
        when(tokenProvider.getUserId(validToken)).thenReturn(userId);
        when(getUserAchievementsPageUseCase.getUserAchievementsPage(any(UserAchievementPageQuery.class)))
                .thenReturn(page);

        ResponseEntity<Page<UserAchievementResponse>> response =
                controller.getPage(0, 10, "achievementId", "asc", 5, "less or equal", validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getTotalElements());
        verify(getUserAchievementsPageUseCase)
                .getUserAchievementsPage(any(UserAchievementPageQuery.class));
    }

    @Test
    void getPage_invalidToken_returnsForbidden() {
        when(tokenProvider.isValid("bad")).thenReturn(false);

        ResponseEntity<Page<UserAchievementResponse>> response =
                controller.getPage(0, 10, "achievementId", "asc", 5, "less or equal", "bad");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(getUserAchievementsPageUseCase, never())
                .getUserAchievementsPage(any());
    }

    @Test
    void getPage_expiredToken_throwsTokenExpiredException() {
        when(tokenProvider.isValid("exp")).thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));

        assertThrows(TokenExpiredException.class,
                () -> controller.getPage(0, 10, "achievementId", "asc", 5, "less or equal", "exp"));
    }
}

