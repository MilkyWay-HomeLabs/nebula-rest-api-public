package org.derleta.nebula.userachievement.application.port.out;

import org.springframework.data.domain.Page;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;
import org.derleta.nebula.userachievement.application.port.in.UserAchievementPageQuery;

import java.util.List;
import java.util.Optional;

/** Output port — data access abstraction for user achievements. */
public interface UserAchievementRepositoryPort {

    Optional<UserAchievement> findByUserIdAndAchievementId(long userId, int achievementId);

    List<UserAchievement> findAllByUserId(long userId);

    Page<UserAchievement> findAllPaged(UserAchievementPageQuery query);
}

