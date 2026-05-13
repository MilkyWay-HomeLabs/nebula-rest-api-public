package org.derleta.nebula.userachievement.adapter.out.persistence;

import org.derleta.nebula.userachievement.adapter.out.persistence.entity.AchievementEntity;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.UserAchievementEntity;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.id.UserAchievementId;
import org.derleta.nebula.userachievement.adapter.out.persistence.jpa.UserAchievementJpaRepository;
import org.derleta.nebula.userachievement.application.port.in.UserAchievementPageQuery;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAchievementJpaAdapterTest {

    @Mock
    private UserAchievementJpaRepository repository;

    @InjectMocks
    private UserAchievementJpaAdapter adapter;

    private UserAchievementEntity buildEntity(long userId, int achievementId) {
        UserAchievementId id = new UserAchievementId();
        id.setUserId(userId);
        id.setAchievementId(achievementId);
        AchievementEntity achievement = new AchievementEntity();
        achievement.setId(achievementId);
        achievement.setName("Test Achievement");
        achievement.setMinValue(0);
        achievement.setMaxValue(100);
        achievement.setDescription("desc");
        achievement.setIconUrl("icon.png");
        achievement.setLevels(java.util.List.of());
        UserAchievementEntity e = new UserAchievementEntity();
        e.setId(id);
        e.setAchievement(achievement);
        e.setProgress(5000);
        e.setLevel(2);
        e.setValue(100);
        return e;
    }

    @Test
    void findByUserIdAndAchievementId_found_returnsUserAchievement() {
        UserAchievementEntity entity = buildEntity(1L, 10);
        when(repository.findByUserIdAndAchievementId(1L, 10))
                .thenReturn(Optional.of(entity));

        Optional<UserAchievement> result = adapter.findByUserIdAndAchievementId(1L, 10);

        assertTrue(result.isPresent());
        verify(repository).findByUserIdAndAchievementId(1L, 10);
    }

    @Test
    void findByUserIdAndAchievementId_notFound_returnsEmpty() {
        when(repository.findByUserIdAndAchievementId(anyLong(), anyInt()))
                .thenReturn(Optional.empty());

        assertTrue(adapter.findByUserIdAndAchievementId(99L, 99).isEmpty());
    }

    @Test
    void findAllByUserId_returnsMappedList() {
        when(repository.findAllByUserId(1L))
                .thenReturn(List.of(buildEntity(1L, 1), buildEntity(1L, 2)));

        List<UserAchievement> result = adapter.findAllByUserId(1L);

        assertEquals(2, result.size());
    }

    @Test
    void findAllByUserId_empty_returnsEmptyList() {
        when(repository.findAllByUserId(anyLong())).thenReturn(List.of());
        assertTrue(adapter.findAllByUserId(42L).isEmpty());
    }

    @Test
    void findAllPaged_returnsPagedResult() {
        UserAchievementPageQuery query = new UserAchievementPageQuery(0, 10, "level", "ASC", 1L, 0, null);
        List<UserAchievementEntity> entities = List.of(buildEntity(1L, 1));
        Page<UserAchievementEntity> page = new PageImpl<>(entities, PageRequest.of(0, 10), 1);

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<UserAchievement> result = adapter.findAllPaged(query);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }
}

