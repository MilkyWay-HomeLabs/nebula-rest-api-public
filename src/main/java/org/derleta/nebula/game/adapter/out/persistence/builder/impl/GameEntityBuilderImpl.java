package org.derleta.nebula.game.adapter.out.persistence.builder.impl;

import org.derleta.nebula.game.adapter.out.persistence.builder.GameEntityBuilder;
import org.derleta.nebula.game.adapter.out.persistence.entity.GameEntity;

public final class GameEntityBuilderImpl implements GameEntityBuilder {

    private int id;
    private String name;
    private boolean enable;
    private String iconUrl;
    private String pageUrl;

    @Override
    public GameEntityBuilder id(int id) {
        this.id = id;
        return this;
    }

    @Override
    public GameEntityBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public GameEntityBuilder enable(boolean enable) {
        this.enable = enable;
        return this;
    }

    @Override
    public GameEntityBuilder iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;    }

    @Override
    public GameEntityBuilder pageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    @Override
    public GameEntity build() {
        GameEntity entity = new GameEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setEnable(enable);
        entity.setIconUrl(iconUrl);
        entity.setPageUrl(pageUrl);
        return entity;
    }

}
