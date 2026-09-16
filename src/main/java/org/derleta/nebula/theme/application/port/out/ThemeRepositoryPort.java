package org.derleta.nebula.theme.application.port.out;

import org.derleta.nebula.theme.domain.model.Theme;

import java.util.List;
import java.util.Optional;

/** Output port — data access abstraction for themes. */
public interface ThemeRepositoryPort {
    Optional<Theme> findById(int id);
    List<Theme> findAll();
}

