package org.derleta.nebula.userachievement.domain.builder.impl;

import org.derleta.nebula.userachievement.domain.builder.AchievementBuilder;
import org.derleta.nebula.userachievement.domain.model.Achievement;
import org.derleta.nebula.userachievement.domain.model.AchievementLevel;

import java.util.List;

public final class AchievementBuilderImpl implements AchievementBuilder {

    private int id;
    private String name;
    private int minValue;
    private int maxValue;
    private String description;
    private String iconUrl;
    private List<AchievementLevel> levels;

    @Override
    public Achievement build() {
        return new Achievement(id, name, minValue, maxValue, description, iconUrl, levels);
    }

    @Override
    public AchievementBuilder id(int id) {
        this.id = id;
        return this;
    }

    @Override
    public AchievementBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public AchievementBuilder minValue(int minValue) {
        this.minValue = minValue;
        return this;
    }

    @Override
    public AchievementBuilder maxValue(int maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    @Override
    public AchievementBuilder description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public AchievementBuilder iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    @Override
    public AchievementBuilder levels(List<AchievementLevel> levels) {
        this.levels = levels;
        return this;
    }

}
