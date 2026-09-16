package org.derleta.nebula.userachievement.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.UserAchievementEntity;
import org.derleta.nebula.userachievement.adapter.out.persistence.mapper.UserAchievementMapper;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;
import org.derleta.nebula.userachievement.adapter.out.persistence.jpa.UserAchievementJpaRepository;
import org.derleta.nebula.userachievement.application.port.in.UserAchievementPageQuery;
import org.derleta.nebula.userachievement.application.port.out.UserAchievementRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Outbound persistence adapter implementing UserAchievementRepositoryPort. */
@Component
@RequiredArgsConstructor
public class UserAchievementJpaAdapter implements UserAchievementRepositoryPort {

    private final UserAchievementJpaRepository repository;

    @Override
    public Optional<UserAchievement> findByUserIdAndAchievementId(long userId, int achievementId) {
        return repository.findByUserIdAndAchievementId(userId, achievementId)
                .map(UserAchievementMapper::toUserAchievement);
    }

    @Override
    public List<UserAchievement> findAllByUserId(long userId) {
        return repository.findAllByUserId(userId)
                .stream()
                .map(UserAchievementMapper::toUserAchievement)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserAchievement> findAllPaged(UserAchievementPageQuery query) {
        Pageable pageable = PageRequest.of(
                query.page(), query.size(),
                Sort.by(Sort.Direction.fromString(query.sortOrder()), query.sortBy()));
        Specification<UserAchievementEntity> spec =
                UserAchievementSpecifications.hasAllFilters(query.level(), query.filterType());
        Page<UserAchievementEntity> entityPage = repository.findAll(spec, pageable);
        List<UserAchievement> achievements = entityPage.stream()
                .map(UserAchievementMapper::toUserAchievement)
                .collect(Collectors.toList());
        return PageableExecutionUtils.getPage(
                achievements, entityPage.getPageable(), entityPage::getTotalElements);
    }
}

