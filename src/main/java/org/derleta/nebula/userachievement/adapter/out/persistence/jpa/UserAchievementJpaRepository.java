package org.derleta.nebula.userachievement.adapter.out.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.UserAchievementEntity;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.id.UserAchievementId;

import java.util.List;
import java.util.Optional;

/** Spring Data JPA repository for user-achievement persistence. */
@Repository
public interface UserAchievementJpaRepository
        extends JpaRepository<UserAchievementEntity, UserAchievementId> {

    Page<UserAchievementEntity> findAll(Specification<UserAchievementEntity> spec, Pageable pageable);

    @Query("SELECT ue FROM UserAchievementEntity ue WHERE ue.id.userId = :userId")
    List<UserAchievementEntity> findAllByUserId(Long userId);

    @Query("SELECT ue FROM UserAchievementEntity ue " +
            "WHERE ue.id.userId = :userId AND ue.id.achievementId = :achievementId")
    Optional<UserAchievementEntity> findByUserIdAndAchievementId(Long userId, Integer achievementId);
}

