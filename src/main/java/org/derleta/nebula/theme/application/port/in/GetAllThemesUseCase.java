package org.derleta.nebula.theme.application.port.in;

import org.derleta.nebula.theme.domain.model.Theme;

import java.util.List;

/** Input port — retrieve all available themes. */
public interface GetAllThemesUseCase {
    List<Theme> getAllThemes();
}

