package org.derleta.nebula.theme.domain.builder;

import org.derleta.nebula.theme.domain.model.Theme;

public interface ThemeBuilder {

    Theme build();

    ThemeBuilder id(int id);

    ThemeBuilder name(String name);

}
