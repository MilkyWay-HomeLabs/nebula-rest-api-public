package org.derleta.nebula.user.domain.builder.impl;

import org.derleta.nebula.user.domain.builder.UserSettingsGeneralBuilder;
import org.derleta.nebula.theme.domain.model.Theme;
import org.derleta.nebula.user.domain.model.UserSettingsGeneral;

public final class UserSettingsGeneralBuilderImpl implements UserSettingsGeneralBuilder {

    private long userId;
    private Theme theme;

    @Override
    public UserSettingsGeneralBuilder userId(long userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public UserSettingsGeneralBuilder theme(Theme theme) {
        this.theme = theme;
        return this;
    }

    @Override
    public UserSettingsGeneral build() {
        return new UserSettingsGeneral(userId, theme);
    }

}
