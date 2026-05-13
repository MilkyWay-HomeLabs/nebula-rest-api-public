package org.derleta.nebula.account.adapter.out.persistence;

import org.derleta.nebula.userachievement.adapter.out.persistence.entity.id.UserAchievementId;

/**
 * Utility class for creating and managing composite identifiers.
 * This class provides methods for constructing instances of composite IDs
 * based on specific input parameters.
 * <p>
 * This class is designed as a utility class and cannot be instantiated.
 */
public final class IdUtil {

    private IdUtil() {}

    public static UserAchievementId getUserAchievementId(final long userId, final int achievementId) {
        UserAchievementId id = new UserAchievementId();
        id.setUserId(userId);
        id.setAchievementId(achievementId);
        return id;
    }

}
