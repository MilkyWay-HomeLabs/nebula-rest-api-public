package org.derleta.nebula.userachievement.application.port.in;

/** Query parameters for paginated user-achievement retrieval. */
public record UserAchievementPageQuery(
        int page,
        int size,
        String sortBy,
        String sortOrder,
        long userId,
        int level,
        String filterType) {
}

