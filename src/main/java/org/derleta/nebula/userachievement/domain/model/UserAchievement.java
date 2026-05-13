package org.derleta.nebula.userachievement.domain.model;

public record UserAchievement(long userId, int achievementId, int value, int level, String progress,
                              Achievement achievement) {
}
