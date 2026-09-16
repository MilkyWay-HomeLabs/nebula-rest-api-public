package org.derleta.nebula.user.domain.builder;

import org.derleta.nebula.theme.domain.model.Theme;
import org.derleta.nebula.user.domain.model.UserSettingsGeneral;

public interface UserSettingsGeneralBuilder {

    UserSettingsGeneral build();

    UserSettingsGeneralBuilder userId(long userId);

    UserSettingsGeneralBuilder theme(Theme theme);

}
