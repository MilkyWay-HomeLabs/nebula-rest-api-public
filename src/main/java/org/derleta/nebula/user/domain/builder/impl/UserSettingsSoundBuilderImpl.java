package org.derleta.nebula.user.domain.builder.impl;

import org.derleta.nebula.user.domain.builder.UserSettingsSoundBuilder;
import org.derleta.nebula.user.domain.model.UserSettingsSound;

public final class UserSettingsSoundBuilderImpl implements UserSettingsSoundBuilder {

    long userId;
    boolean muted;
    boolean battleCry;
    int volumeMaster;
    int volumeMusic;
    int volumeEffects;
    int volumeVoices;

    @Override
    public UserSettingsSound build() {
        return new UserSettingsSound(userId, muted, battleCry, volumeMaster, volumeMusic, volumeEffects, volumeVoices);
    }

    @Override
    public UserSettingsSoundBuilder userId(long userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public UserSettingsSoundBuilder muted(boolean muted) {
        this.muted = muted;
        return this;
    }

    @Override
    public UserSettingsSoundBuilder battleCry(boolean battleCry) {
        this.battleCry = battleCry;
        return this;
    }

    @Override
    public UserSettingsSoundBuilder volumeMaster(int volumeMaster) {
        this.volumeMaster = volumeMaster;
        return this;
    }

    @Override
    public UserSettingsSoundBuilder volumeMusic(int volumeMusic) {
        this.volumeMusic = volumeMusic;
        return this;
    }

    @Override
    public UserSettingsSoundBuilder volumeEffects(int volumeEffects) {
        this.volumeEffects = volumeEffects;
        return this;
    }

    @Override
    public UserSettingsSoundBuilder volumeVoices(int volumeVoices) {
        this.volumeVoices = volumeVoices;
        return this;
    }

}
