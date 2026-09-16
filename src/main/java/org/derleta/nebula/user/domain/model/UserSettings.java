package org.derleta.nebula.user.domain.model;

public record UserSettings(
        long userId,
        UserSettingsGeneral general,
        UserSettingsSound sound) {
}

