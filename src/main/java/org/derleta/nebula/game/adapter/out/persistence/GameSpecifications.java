package org.derleta.nebula.game.adapter.out.persistence;

import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;
import org.derleta.nebula.game.adapter.out.persistence.entity.GameEntity;

/**
 * A utility class providing specifications for querying {@link GameEntity}
 * objects based on various criteria.
 */
@EqualsAndHashCode
public final class GameSpecifications {

    public static Specification<GameEntity> hasAllFilters(final String name, final boolean enable) {
        return hasName(name).and(isEnable(enable));
    }

    public static Specification<GameEntity> hasName(final String name) {
        return (root, query, cb) -> {
            String namePattern = (name == null || name.isEmpty()) ? "%%" : "%" + name + "%";
            return cb.like(root.get("name"), namePattern);
        };
    }

    public static Specification<GameEntity> isEnable(final boolean enable) {
        return (root, query, cb) -> cb.equal(root.get("enable"), enable);
    }
}

