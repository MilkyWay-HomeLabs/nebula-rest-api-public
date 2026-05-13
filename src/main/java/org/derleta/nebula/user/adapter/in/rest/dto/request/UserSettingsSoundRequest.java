package org.derleta.nebula.user.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotNull;

/** REST request DTO for sound user settings. */
public record UserSettingsSoundRequest(
        @NotNull long userId,
        boolean muted,
        boolean battleCry,
        int volumeMaster,
        int volumeMusic,
        int volumeEffects,
        int volumeVoices) {
}

