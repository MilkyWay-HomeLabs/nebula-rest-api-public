package org.derleta.nebula.user.adapter.in.rest.mapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Link;
import org.derleta.nebula.userachievement.domain.mapper.NebulaUserAchievementMapper;
import org.derleta.nebula.user.domain.model.NebulaUser;
import org.derleta.nebula.user.domain.model.UserSettings;
import org.derleta.nebula.user.domain.model.UserSettingsGeneral;
import org.derleta.nebula.user.domain.model.UserSettingsSound;
import org.derleta.nebula.user.adapter.in.rest.UserController;
import org.derleta.nebula.user.adapter.in.rest.dto.request.ProfileUpdateRequest;
import org.derleta.nebula.user.adapter.in.rest.dto.request.UserSettingsRequest;
import org.derleta.nebula.user.adapter.in.rest.dto.response.NebulaUserResponse;
import org.derleta.nebula.user.adapter.in.rest.dto.response.UserSettingsResponse;
import org.derleta.nebula.user.application.port.in.command.UpdateProfileCommand;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
/** Maps REST DTOs to domain objects and vice versa, with HATEOAS link support. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserRestMapper {
    /** Converts ProfileUpdateRequest DTO to UpdateProfileCommand. */
    public static UpdateProfileCommand toCommand(ProfileUpdateRequest request) {
        return new UpdateProfileCommand(
                request.getUserId(),
                request.getFirstName(),
                request.getLastName(),
                request.getBirthdate(),
                request.getNationalityId(),
                request.getGenderId());
    }
    /** Converts UserSettingsRequest DTO to UserSettings domain model. */
    public static UserSettings toSettings(UserSettingsRequest request) {
        UserSettingsGeneral general = null;
        if (request.general() != null) {
            general = new UserSettingsGeneral(request.general().userId(), request.general().theme());
        }
        UserSettingsSound sound = null;
        if (request.sound() != null) {
            var s = request.sound();
            sound = new UserSettingsSound(s.userId(), s.muted(), s.battleCry(),
                    s.volumeMaster(), s.volumeMusic(), s.volumeEffects(), s.volumeVoices());
        }
        return new UserSettings(request.userId(), general, sound);
    }
    /** Converts NebulaUser domain model to NebulaUserResponse DTO with HATEOAS self-link. */
    public static NebulaUserResponse toResponse(NebulaUser user) {
        NebulaUserResponse response = NebulaUserResponse.builder()
                .id(user.id())
                .login(user.login())
                .email(user.email())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .age(user.age())
                .birthDate(user.birthDate())
                .gender(user.gender())
                .nationality(user.nationality())
                .settings(user.settings())
                .games(user.games())
                .achievements(NebulaUserAchievementMapper.toList(user.achievements()))
                .build();
        Link selfLink = linkTo(UserController.class)
                .slash(UserController.DEFAULT_PATH)
                .slash(response.getId())
                .withSelfRel();
        response.add(selfLink);
        return response;
    }
    /** Converts UserSettings domain model to UserSettingsResponse DTO. */
    public static UserSettingsResponse toSettingsResponse(UserSettings settings) {
        return UserSettingsResponse.builder()
                .userId(settings.userId())
                .general(settings.general())
                .sound(settings.sound())
                .build();
    }
}
