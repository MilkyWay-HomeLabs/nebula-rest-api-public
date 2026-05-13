package org.derleta.nebula.game.application.port.in;

/** Query object carrying pagination, sorting and filter parameters. */
public record GamePageQuery(
        int page,
        int size,
        String sortBy,
        String sortOrder,
        String name,
        boolean enable
) {}

