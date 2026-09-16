package org.derleta.nebula.game.adapter.out.persistence.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.game.domain.builder.impl.GameBuilderImpl;
import org.derleta.nebula.game.adapter.out.persistence.builder.impl.GameEntityBuilderImpl;
import org.derleta.nebula.game.adapter.out.persistence.entity.GameEntity;
import org.derleta.nebula.game.domain.model.Game;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for mapping between GameEntity and Game objects.
 * This class provides methods to convert database entities (GameEntity) to business
 * models (Game) and create Game objects from individual attributes.
 * <p>
 * The class is designed to be non-instantiable and provides only static methods
 * for mapping operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameMapper {

    /**
     * Converts a list of GameEntity objects to a list of Game objects.
     * This method maps each GameEntity in the provided list to a corresponding Game object.
     *
     * @param entities the list of GameEntity objects to be converted
     * @return a list of Game objects constructed from the provided list of GameEntity objects
     */
    public static List<Game> toGames(final List<GameEntity> entities) {
        return entities.stream().map(GameMapper::toGame).collect(Collectors.toList());
    }

    /**
     * Converts a GameEntity object to a Game object.
     * This method maps the fields of the GameEntity instance to the corresponding attributes
     * of a Game object using a builder implementation.
     *
     * @param entity the GameEntity object to be converted
     * @return a Game object constructed from the provided GameEntity object
     */
    public static Game toGame(final GameEntity entity) {
        return new GameBuilderImpl()
                .id(entity.getId())
                .name(entity.getName())
                .enable(entity.getEnable() != null && entity.getEnable())
                .iconUrl(entity.getIconUrl())
                .pageUrl(entity.getPageUrl())
                .build();
    }

    /**
     * Converts individual attributes into a Game object.
     * This method utilizes the GameBuilderImpl to create and populate a Game object
     * with the provided attributes.
     *
     * @param id      the unique identifier of the game
     * @param name    the name of the game
     * @param enable  a flag indicating whether the game is enabled
     * @param iconUrl the URL of the game's icon
     * @param pageUrl the URL of the game's page
     * @return a Game object constructed with the specified attributes
     */
    public static Game toGame(Integer id, String name, Boolean enable, String iconUrl, String pageUrl) {
        return new GameBuilderImpl()
                .id(id)
                .name(name)
                .enable(enable != null && enable)
                .iconUrl(iconUrl)
                .pageUrl(pageUrl)
                .build();
    }

    /**
     * Converts a Game object to a GameEntity object.
     * This method maps the relevant properties of the Game record to the corresponding fields
     * of a GameEntity instance.
     *
     * @param item the Game object to be converted
     * @return a GameEntity object that corresponds to the provided Game object
     */
    public static GameEntity toEntity(final Game item) {
        return new GameEntityBuilderImpl()
                .id(item.id())
                .name(item.name())
                .enable(item.enable())
                .iconUrl(item.iconUrl())
                .pageUrl(item.pageUrl())
                .build();
    }

}
