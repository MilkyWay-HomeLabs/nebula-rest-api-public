package org.derleta.nebula.user.adapter.in.rest.dto.response;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.derleta.nebula.user.domain.model.UserSettingsGeneral;
import org.derleta.nebula.user.domain.model.UserSettingsSound;
/** REST response DTO for user settings. */
@Getter
@Builder
@ToString
public class UserSettingsResponse {
    long userId;
    UserSettingsGeneral general;
    UserSettingsSound sound;
}
