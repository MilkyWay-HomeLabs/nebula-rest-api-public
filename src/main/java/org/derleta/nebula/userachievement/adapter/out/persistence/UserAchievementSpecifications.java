package org.derleta.nebula.userachievement.adapter.out.persistence;

import org.derleta.nebula.userachievement.adapter.out.persistence.entity.UserAchievementEntity;
import org.springframework.data.jpa.domain.Specification;

/**
 * JPA specifications for filtering UserAchievementEntity by level.
 * <p>
 * Supported filterType values:
 * "greater or equal", "less or equal", "greater", "less", "notequal",
 * anything else defaults to exact equality.
 */
public final class UserAchievementSpecifications {

    private UserAchievementSpecifications() {
    }

    public static Specification<UserAchievementEntity> hasAllFilters(int level, String filterType) {
        String filter = (filterType == null) ? "equal" : filterType.toLowerCase();
        return switch (filter) {
            case "greater or equal" -> hasGreaterOrEqualLevel(level);
            case "less or equal"    -> hasLessOrEqualLevel(level);
            case "greater"          -> hasGreaterLevel(level);
            case "less"             -> hasLessLevel(level);
            case "notequal"         -> hasNotEqualLevel(level);
            default                 -> hasEqualLevel(level);
        };
    }

    public static Specification<UserAchievementEntity> hasEqualLevel(int level) {
        return (root, query, cb) -> cb.equal(root.get("level"), level);
    }

    public static Specification<UserAchievementEntity> hasNotEqualLevel(int level) {
        return (root, query, cb) -> cb.notEqual(root.get("level"), level);
    }

    public static Specification<UserAchievementEntity> hasGreaterLevel(int level) {
        return (root, query, cb) -> cb.greaterThan(root.get("level"), level);
    }

    public static Specification<UserAchievementEntity> hasGreaterOrEqualLevel(int level) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("level"), level);
    }

    public static Specification<UserAchievementEntity> hasLessLevel(int level) {
        return (root, query, cb) -> cb.lessThan(root.get("level"), level);
    }

    public static Specification<UserAchievementEntity> hasLessOrEqualLevel(int level) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("level"), level);
    }
}

