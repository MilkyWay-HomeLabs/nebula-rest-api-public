package org.derleta.nebula.userachievement.domain.builder.impl;

import org.derleta.nebula.userachievement.domain.builder.AchievementLevelBuilder;
import org.derleta.nebula.userachievement.domain.model.AchievementLevel;

public final class AchievementLevelBuilderImpl implements AchievementLevelBuilder {

    private int level;
    private int value;

    @Override
    public AchievementLevel build() {
        return new AchievementLevel(level, value);
    }

    @Override
    public AchievementLevelBuilder level(int level) {
        this.level = level;
        return this;
    }

    @Override
    public AchievementLevelBuilder value(int value) {
        this.value = value;
        return this;
    }

}
