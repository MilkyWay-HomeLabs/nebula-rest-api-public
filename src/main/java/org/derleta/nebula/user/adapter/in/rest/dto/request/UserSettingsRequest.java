package org.derleta.nebula.user.adapter.in.rest.dto.request;
import com.fasterxml.jackson.annotation.JsonProperty;
/** REST request DTO for user settings update. */
public record UserSettingsRequest(
        @JsonProperty("userId") long userId,
        @JsonProperty("general") UserSettingsGeneralRequest general,
        @JsonProperty("sound") UserSettingsSoundRequest sound) {
}
