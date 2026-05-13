package org.derleta.nebula.user.application.port.in;

import org.derleta.nebula.user.domain.model.UserSettings;

/** Input port — updates user settings. */
public interface UpdateUserSettingsUseCase {

    UserSettings updateSettings(UserSettings userSettings);
}

