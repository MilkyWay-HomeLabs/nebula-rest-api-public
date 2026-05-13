package org.derleta.nebula.userachievement.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.AchievementEntity;

@Repository
public interface AchievementJpaRepository extends JpaRepository<AchievementEntity, Integer> {
}

