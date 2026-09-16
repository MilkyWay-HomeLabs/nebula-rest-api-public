package org.derleta.nebula.theme.application.port.in;

import org.derleta.nebula.theme.domain.model.Theme;

/** Input port — retrieve a single theme by ID. */
public interface GetThemeUseCase {
    Theme getTheme(int id);
}

