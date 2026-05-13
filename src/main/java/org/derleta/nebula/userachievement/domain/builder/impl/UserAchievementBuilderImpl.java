package org.derleta.nebula.userachievement.domain.builder.impl;

import org.derleta.nebula.userachievement.domain.builder.UserAchievementBuilder;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;
import org.derleta.nebula.userachievement.domain.model.Achievement;

public final class UserAchievementBuilderImpl implements UserAchievementBuilder {

    private long userId;
    private int achievementId;
    private int value;
    private int level;
    private String progress;
    private Achievement achievement;

    @Override
    public UserAchievement build() {
        return new UserAchievement(userId, achievementId, value, level, progress, achievement);
    }

    @Override
    public UserAchievementBuilder userId(long userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public UserAchievementBuilder achievementId(int achievementId) {
        this.achievementId = achievementId;
        return this;
    }

    @Override
    public UserAchievementBuilder value(int value) {
        this.value = value;
        return this;
    }

    @Override
    public UserAchievementBuilder level(int level) {
        this.level = level;
        return this;
    }

    @Override
    public UserAchievementBuilder progress(int progress) {
        this.progress = getFormattedProgress(progress);
        return this;
    }

    @Override
    public UserAchievementBuilder achievement(Achievement achievement) {
        this.achievement = achievement;
        return this;
    }

    private String getFormattedProgress(int progress) {
        double percentage = progress / 100.0;
        return String.format("%.2f%%", percentage).replace('.', ',');
    }

}
