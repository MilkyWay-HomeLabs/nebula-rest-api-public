package org.derleta.nebula.user.domain.builder;

import org.derleta.nebula.user.domain.model.UserSettings;
import org.derleta.nebula.user.domain.model.UserSettingsGeneral;
import org.derleta.nebula.user.domain.model.UserSettingsSound;

public interface UserSettingsBuilder {

    UserSettings build();

    UserSettingsBuilder userId(long userId);

    UserSettingsBuilder userSettingsGeneral(UserSettingsGeneral userSettingsGeneral);

    UserSettingsBuilder userSettingsSound(UserSettingsSound userSettingsSound);

}
