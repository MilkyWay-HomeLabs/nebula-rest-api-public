package org.derleta.nebula.user.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import org.derleta.nebula.theme.domain.model.Theme;

/** REST request DTO for general user settings. */
public record UserSettingsGeneralRequest(
        @NotNull long userId,
        @NotNull Theme theme) {
}

