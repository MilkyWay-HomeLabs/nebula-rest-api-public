package org.derleta.nebula.userachievement.domain.builder.impl;

import org.derleta.nebula.userachievement.domain.builder.NebulaUserAchievementBuilder;
import org.derleta.nebula.userachievement.domain.model.AchievementLevel;
import org.derleta.nebula.userachievement.domain.model.NebulaUserAchievement;

import java.util.List;

public final class NebulaUserAchievementBuilderImpl implements NebulaUserAchievementBuilder {

    private int id;
    private int minValue;
    private int maxValue;
    private int value;
    private int level;
    private String name;
    private String progress;
    private String description;
    private String iconUrl;
    private List<AchievementLevel> levels;

    @Override
    public NebulaUserAchievement build() {
        return new NebulaUserAchievement(id, name, minValue, maxValue, value, level, progress, description, iconUrl, levels);
    }

    @Override
    public NebulaUserAchievementBuilder id(int id) {
        this.id = id;
        return this;
    }

    @Override
    public NebulaUserAchievementBuilder minValue(int minValue) {
        this.minValue = minValue;
        return this;
    }

    @Override
    public NebulaUserAchievementBuilder maxValue(int maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    @Override
    public NebulaUserAchievementBuilder value(int value) {
        this.value = value;
        return this;
    }

    @Override
    public NebulaUserAchievementBuilder level(int level) {
        this.level = level;
        return this;
    }

    @Override
    public NebulaUserAchievementBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public NebulaUserAchievementBuilder progress(String progress) {
        this.progress = progress;
        return this;
    }

    @Override
    public NebulaUserAchievementBuilder description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public NebulaUserAchievementBuilder iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    @Override
    public NebulaUserAchievementBuilder levels(List<AchievementLevel> levels) {
        this.levels = levels;
        return this;
    }

}
