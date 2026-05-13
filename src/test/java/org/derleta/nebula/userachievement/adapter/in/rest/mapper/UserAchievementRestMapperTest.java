package org.derleta.nebula.userachievement.adapter.in.rest.mapper;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.derleta.nebula.userachievement.domain.model.Achievement;
import org.derleta.nebula.userachievement.domain.model.AchievementLevel;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;
import org.derleta.nebula.userachievement.adapter.in.rest.dto.UserAchievementResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserAchievementRestMapperTest {

    // ---- toResponse ---------------------------------------------------------

    @Test
    void toResponse_validUserAchievement_mapsFieldsCorrectly() {
        List<AchievementLevel> levels = Arrays.asList(
                new AchievementLevel(1, 25),
                new AchievementLevel(2, 50)
        );
        Achievement achievement = new Achievement(
                1, "Achievement 1", 0, 100,
                "Description 1", "https://example.com/icon1.png", levels);
        UserAchievement userAchievement = new UserAchievement(1000L, 1, 30, 1, "30%", achievement);

        UserAchievementResponse result = UserAchievementRestMapper.toResponse(userAchievement);

        assertNotNull(result);
        assertEquals(1000L, result.getUserId());
        assertEquals(1, result.getAchievementId());
        assertEquals(30, result.getValue());
        assertEquals(1, result.getLevel());
        assertEquals("30%", result.getProgress());
        assertEquals(achievement, result.getAchievement());
        // Self-link should be present
        assertTrue(result.hasLinks(), "Response should have HATEOAS self-link");
        assertTrue(result.getLink("self").isPresent(), "Self-link should be present");
    }

    @Test
    void toResponse_nullUserAchievement_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> UserAchievementRestMapper.toResponse(null));
    }

    // ---- toPageResponse -----------------------------------------------------

    @Test
    void toPageResponse_validPage_returnsPageOfResponses() {
        List<AchievementLevel> levels = Arrays.asList(
                new AchievementLevel(1, 25),
                new AchievementLevel(2, 50)
        );
        Achievement achievement1 = new Achievement(
                1, "Achievement 1", 0, 100, "Description 1", "https://example.com/icon1.png", levels);
        Achievement achievement2 = new Achievement(
                2, "Achievement 2", 0, 200, "Description 2", "https://example.com/icon2.png", levels);

        Page<UserAchievement> page = buildPage(achievement1, achievement2);

        Page<UserAchievementResponse> result = UserAchievementRestMapper.toPageResponse(page);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());

        List<UserAchievementResponse> content = result.getContent();
        assertEquals(2, content.size());

        UserAchievementResponse r1 = content.getFirst();
        assertEquals(1000L, r1.getUserId());
        assertEquals(1, r1.getAchievementId());
        assertEquals(30, r1.getValue());
        assertEquals(1, r1.getLevel());
        assertEquals("30%", r1.getProgress());
        assertEquals(achievement1, r1.getAchievement());

        UserAchievementResponse r2 = content.get(1);
        assertEquals(1000L, r2.getUserId());
        assertEquals(2, r2.getAchievementId());
        assertEquals(60, r2.getValue());
        assertEquals(2, r2.getLevel());
        assertEquals("60%", r2.getProgress());
        assertEquals(achievement2, r2.getAchievement());
    }

    @Test
    void toPageResponse_emptyPage_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserAchievement> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        Page<UserAchievementResponse> result = UserAchievementRestMapper.toPageResponse(page);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void toPageResponse_nullPage_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> UserAchievementRestMapper.toPageResponse(null));
    }

    // ---- helpers ------------------------------------------------------------

    @NotNull
    private static Page<UserAchievement> buildPage(Achievement achievement1, Achievement achievement2) {
        UserAchievement ua1 = new UserAchievement(1000L, 1, 30, 1, "30%", achievement1);
        UserAchievement ua2 = new UserAchievement(1000L, 2, 60, 2, "60%", achievement2);
        Pageable pageable = PageRequest.of(0, 10);
        return new PageImpl<>(Arrays.asList(ua1, ua2), pageable, 2);
    }
}

