package org.derleta.nebula.userachievement.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;
import org.derleta.nebula.userachievement.application.port.in.*;
import org.derleta.nebula.userachievement.application.port.out.UserAchievementRepositoryPort;
import org.derleta.nebula.userachievement.domain.exception.UserAchievementNotFoundException;

import java.util.List;

/** Application service implementing all user-achievement use cases. */
@Service
@RequiredArgsConstructor
public class UserAchievementService implements
        GetUserAchievementUseCase,
        GetUserAchievementsListUseCase,
        GetUserAchievementsPageUseCase {

    private final UserAchievementRepositoryPort userAchievementRepository;

    @Override
    public UserAchievement getUserAchievement(long userId, int achievementId) {
        return userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId)
                .orElseThrow(() -> new UserAchievementNotFoundException(
                        "UserAchievement not found for userId: " + userId
                                + " and achievementId: " + achievementId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserAchievement> getUserAchievementsList(long userId) {
        return userAchievementRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserAchievement> getUserAchievementsPage(UserAchievementPageQuery query) {
        return userAchievementRepository.findAllPaged(query);
    }
}

