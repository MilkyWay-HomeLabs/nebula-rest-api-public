package org.derleta.nebula.game.adapter.in.rest.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.game.domain.model.Game;
import org.derleta.nebula.game.adapter.in.rest.dto.GameResponse;

/** Pure static mapper between domain Game and REST GameResponse. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameRestMapper {

    public static GameResponse toResponse(Game game) {
        return GameResponse.builder()
                .id(game.id())
                .name(game.name())
                .enable(game.enable())
                .iconUrl(game.iconUrl())
                .pageUrl(game.pageUrl())
                .build();
    }
}

