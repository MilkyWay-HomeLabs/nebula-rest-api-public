package org.derleta.nebula.userachievement.domain.model;

import java.util.List;

public record Achievement(int id, String name, int minValue, int maxValue, String description, String iconUrl,
                          List<AchievementLevel> levels) {
}
