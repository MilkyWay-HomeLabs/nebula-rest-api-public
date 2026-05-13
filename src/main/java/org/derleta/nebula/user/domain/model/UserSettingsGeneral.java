package org.derleta.nebula.user.domain.model;

import org.derleta.nebula.theme.domain.model.Theme;

public record UserSettingsGeneral(long userId, Theme theme) {
}
