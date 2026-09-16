package org.derleta.nebula.game.domain.builder.impl;

import org.derleta.nebula.game.domain.builder.GameBuilder;
import org.derleta.nebula.game.domain.model.Game;

public final class GameBuilderImpl implements GameBuilder {

    private int id;
    private String name;
    private boolean enable;
    private String iconUrl;
    private String pageUrl;

    @Override
    public GameBuilder id(int id) {
        this.id = id;
        return this;
    }

    @Override
    public GameBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public GameBuilder enable(boolean enable) {
        this.enable = enable;
        return this;
    }

    @Override
    public GameBuilder iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    @Override
    public GameBuilder pageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    @Override
    public Game build() {
        return new Game(id, name, enable, iconUrl, pageUrl);
    }

}
