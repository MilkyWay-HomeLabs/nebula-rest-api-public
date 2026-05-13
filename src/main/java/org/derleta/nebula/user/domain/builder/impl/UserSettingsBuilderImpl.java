package org.derleta.nebula.user.domain.builder.impl;

import org.derleta.nebula.user.domain.builder.UserSettingsBuilder;
import org.derleta.nebula.user.domain.model.UserSettings;
import org.derleta.nebula.user.domain.model.UserSettingsGeneral;
import org.derleta.nebula.user.domain.model.UserSettingsSound;

public final class UserSettingsBuilderImpl implements UserSettingsBuilder {

    private long userId;
    private UserSettingsGeneral userSettingsGeneral;
    private UserSettingsSound userSettingsSound;

    @Override
    public UserSettingsBuilder userId(long userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public UserSettingsBuilder userSettingsGeneral(UserSettingsGeneral userSettingsGeneral) {
        this.userSettingsGeneral = userSettingsGeneral;
        return this;
    }

    @Override
    public UserSettingsBuilder userSettingsSound(UserSettingsSound userSettingsSound) {
        this.userSettingsSound = userSettingsSound;
        return this;
    }

    @Override
    public UserSettings build() {
        return new UserSettings(userId, userSettingsGeneral, userSettingsSound);
    }

}
