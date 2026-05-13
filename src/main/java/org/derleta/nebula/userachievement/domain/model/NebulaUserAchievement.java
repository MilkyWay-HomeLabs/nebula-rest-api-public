package org.derleta.nebula.userachievement.domain.model;

import java.util.List;

public record NebulaUserAchievement(int id, String name,
                                    int minValue, int maxValue, int value, int level,
                                    String progress,
                                    String description, String iconUrl,
                                    List<AchievementLevel> levels) {
}
