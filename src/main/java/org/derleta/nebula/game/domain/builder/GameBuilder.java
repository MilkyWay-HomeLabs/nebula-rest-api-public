package org.derleta.nebula.game.domain.builder;

import org.derleta.nebula.game.domain.model.Game;

public interface GameBuilder {

    Game build();

    GameBuilder id(int id);

    GameBuilder name(String name);

    GameBuilder enable(boolean enable);

    GameBuilder iconUrl(String iconUrl);

    GameBuilder pageUrl(String pageUrl);

}
