package org.derleta.nebula.game.adapter.out.persistence.builder;

import org.derleta.nebula.game.adapter.out.persistence.entity.GameEntity;

public interface GameEntityBuilder {

    GameEntity build();

    GameEntityBuilder id(int id);

    GameEntityBuilder name(String name);

    GameEntityBuilder enable(boolean enable);

    GameEntityBuilder iconUrl(String iconUrl);

    GameEntityBuilder pageUrl(String pageUrl);

}
