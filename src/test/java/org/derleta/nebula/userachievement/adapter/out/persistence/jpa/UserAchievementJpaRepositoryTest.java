package org.derleta.nebula.userachievement.adapter.out.persistence.jpa;

import jakarta.persistence.EntityManager;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserEntity;
import org.derleta.nebula.user.adapter.out.persistence.jpa.UserDataJpaRepository;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.AchievementEntity;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.UserAchievementEntity;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.id.UserAchievementId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class UserAchievementJpaRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserAchievementJpaRepository userAchievementJpaRepository;

    @Autowired
    private UserDataJpaRepository userRepository;

    @Autowired
    private AchievementJpaRepository achievementRepository;

    @Test
    @Transactional
    void findAll_withSpecification_shouldReturnFilteredResults() {
        Long userId = 15L;
        UserEntity user = userRepository.findById(userId).orElseThrow();
        AchievementEntity achievement = achievementRepository.findById(1).orElseThrow();

        UserAchievementId id = new UserAchievementId();
        id.setUserId(userId);
        id.setAchievementId(achievement.getId());

        UserAchievementEntity entity = new UserAchievementEntity();
        entity.setId(id);
        entity.setUser(user);
        entity.setAchievement(achievement);
        entity.setProgress(5000);
        entity.setLevel(1);
        entity.setValue(100);

        userAchievementJpaRepository.save(entity);
        userAchievementJpaRepository.flush();
        entityManager.clear();

        Specification<UserAchievementEntity> spec =
                (root, query, cb) -> cb.equal(root.get("id").get("userId"), userId);
        Pageable pageable = PageRequest.of(0, 10);

        Page<UserAchievementEntity> result = userAchievementJpaRepository.findAll(spec, pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.getContent().stream()
                .allMatch(ua -> ua.getId().getUserId().equals(userId)));
    }

    @Test
    @Transactional
    void findAllByUserId_existingUserId_shouldReturnUserAchievements() {
        Long userId = 15L;
        UserEntity user = userRepository.findById(userId).orElseThrow();
        AchievementEntity achievement1 = achievementRepository.findById(1).orElseThrow();
        AchievementEntity achievement2 = achievementRepository.findById(2).orElseThrow();

        UserAchievementId id1 = new UserAchievementId();
        id1.setUserId(userId);
        id1.setAchievementId(achievement1.getId());
        UserAchievementEntity entity1 = new UserAchievementEntity();
        entity1.setId(id1);
        entity1.setUser(user);
        entity1.setAchievement(achievement1);
        entity1.setProgress(5000);
        entity1.setLevel(1);
        entity1.setValue(100);

        UserAchievementId id2 = new UserAchievementId();
        id2.setUserId(userId);
        id2.setAchievementId(achievement2.getId());
        UserAchievementEntity entity2 = new UserAchievementEntity();
        entity2.setId(id2);
        entity2.setUser(user);
        entity2.setAchievement(achievement2);
        entity2.setProgress(7500);
        entity2.setLevel(2);
        entity2.setValue(200);

        userAchievementJpaRepository.save(entity1);
        userAchievementJpaRepository.save(entity2);
        userAchievementJpaRepository.flush();
        entityManager.clear();

        List<UserAchievementEntity> result = userAchievementJpaRepository.findAllByUserId(userId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(ua -> ua.getId().getUserId().equals(userId)));
        assertTrue(result.stream().anyMatch(ua -> ua.getId().getAchievementId().equals(achievement1.getId())));
        assertTrue(result.stream().anyMatch(ua -> ua.getId().getAchievementId().equals(achievement2.getId())));
    }

    @Test
    @Transactional
    void findAllByUserId_nonExistingUserId_shouldReturnEmptyList() {
        List<UserAchievementEntity> result = userAchievementJpaRepository.findAllByUserId(9999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @Transactional
    void findByUserIdAndAchievementId_existing_shouldReturnUserAchievement() {
        Long userId = 15L;
        UserEntity user = userRepository.findById(userId).orElseThrow();
        AchievementEntity achievement = achievementRepository.findById(1).orElseThrow();

        UserAchievementId id = new UserAchievementId();
        id.setUserId(userId);
        id.setAchievementId(achievement.getId());
        UserAchievementEntity entity = new UserAchievementEntity();
        entity.setId(id);
        entity.setUser(user);
        entity.setAchievement(achievement);
        entity.setProgress(5000);
        entity.setLevel(1);
        entity.setValue(100);

        userAchievementJpaRepository.save(entity);
        userAchievementJpaRepository.flush();
        entityManager.clear();

        Optional<UserAchievementEntity> result =
                userAchievementJpaRepository.findByUserIdAndAchievementId(userId, achievement.getId());

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId().getUserId());
        assertEquals(achievement.getId(), result.get().getId().getAchievementId());
        assertEquals(5000, result.get().getProgress());
        assertEquals(1, result.get().getLevel());
        assertEquals(100, result.get().getValue());
    }

    @Test
    @Transactional
    void findByUserIdAndAchievementId_nonExisting_shouldReturnEmpty() {
        Optional<UserAchievementEntity> result1 =
                userAchievementJpaRepository.findByUserIdAndAchievementId(9999L, 1);
        Optional<UserAchievementEntity> result2 =
                userAchievementJpaRepository.findByUserIdAndAchievementId(15L, 9999);

        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
    }
}

