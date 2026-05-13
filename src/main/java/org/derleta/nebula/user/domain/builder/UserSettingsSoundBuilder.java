package org.derleta.nebula.user.domain.builder;

import org.derleta.nebula.user.domain.model.UserSettingsSound;

public interface UserSettingsSoundBuilder {

    UserSettingsSound build();

    UserSettingsSoundBuilder userId(long userId);

    UserSettingsSoundBuilder muted(boolean muted);

    UserSettingsSoundBuilder battleCry(boolean battleCry);

    UserSettingsSoundBuilder volumeMaster(int volumeMaster);

    UserSettingsSoundBuilder volumeMusic(int volumeMusic);

    UserSettingsSoundBuilder volumeEffects(int volumeEffects);

    UserSettingsSoundBuilder volumeVoices(int volumeVoices);

}
