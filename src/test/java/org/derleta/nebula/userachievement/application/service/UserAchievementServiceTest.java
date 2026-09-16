package org.derleta.nebula.userachievement.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.derleta.nebula.userachievement.domain.model.Achievement;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;
import org.derleta.nebula.userachievement.application.port.in.UserAchievementPageQuery;
import org.derleta.nebula.userachievement.application.port.out.UserAchievementRepositoryPort;
import org.derleta.nebula.userachievement.domain.exception.UserAchievementNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAchievementServiceTest {

    @Mock
    private UserAchievementRepositoryPort userAchievementRepository;

    @InjectMocks
    private UserAchievementService service;

    private final Long userId = 123L;
    private final Integer achievementId = 456;
    private UserAchievement testUserAchievement;

    @BeforeEach
    void setUp() {
        Achievement achievement = new Achievement(
                achievementId, "Test Achievement", 0, 100,
                "Test description", "https://example.com/icon.png",
                Collections.emptyList());
        testUserAchievement = new UserAchievement(userId, achievementId, 75, 2, "75,00%", achievement);
    }

    // ---- getUserAchievement -------------------------------------------------

    @Test
    void getUserAchievement_found_returnsUserAchievement() {
        when(userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId))
                .thenReturn(Optional.of(testUserAchievement));

        UserAchievement result = service.getUserAchievement(userId, achievementId);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(achievementId, result.achievementId());
        verify(userAchievementRepository).findByUserIdAndAchievementId(userId, achievementId);
    }

    @Test
    void getUserAchievement_notFound_throwsNotFoundException() {
        when(userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId))
                .thenReturn(Optional.empty());

        assertThrows(UserAchievementNotFoundException.class,
                () -> service.getUserAchievement(userId, achievementId));
    }

    // ---- getUserAchievementsList --------------------------------------------

    @Test
    void getUserAchievementsList_returnsListOfUserAchievements() {
        when(userAchievementRepository.findAllByUserId(userId))
                .thenReturn(Collections.singletonList(testUserAchievement));

        List<UserAchievement> result = service.getUserAchievementsList(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.getFirst().userId());
        verify(userAchievementRepository).findAllByUserId(userId);
    }

    @Test
    void getUserAchievementsList_emptyList_returnsEmptyList() {
        when(userAchievementRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        List<UserAchievement> result = service.getUserAchievementsList(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---- getUserAchievementsPage --------------------------------------------

    @Test
    void getUserAchievementsPage_returnsPage() {
        UserAchievementPageQuery query =
                new UserAchievementPageQuery(0, 10, "achievementId", "asc", userId, 2, "less or equal");
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserAchievement> expectedPage =
                new PageImpl<>(Collections.singletonList(testUserAchievement), pageable, 1);
        when(userAchievementRepository.findAllPaged(query)).thenReturn(expectedPage);

        Page<UserAchievement> result = service.getUserAchievementsPage(query);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userAchievementRepository).findAllPaged(query);
    }
}

