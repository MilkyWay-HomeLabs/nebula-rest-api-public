package org.derleta.nebula.user.adapter.in.rest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.derleta.nebula.shared.security.TokenProvider;
import org.derleta.nebula.user.adapter.in.rest.dto.request.ProfileUpdateRequest;
import org.derleta.nebula.user.adapter.in.rest.dto.request.UserSettingsRequest;
import org.derleta.nebula.user.adapter.in.rest.dto.response.NebulaUserResponse;
import org.derleta.nebula.user.adapter.in.rest.dto.response.UserSettingsResponse;
import org.derleta.nebula.user.adapter.in.rest.mapper.UserRestMapper;
import org.derleta.nebula.user.application.port.in.GetUserUseCase;
import org.derleta.nebula.user.application.port.in.UpdateUserProfileUseCase;
import org.derleta.nebula.user.application.port.in.UpdateUserSettingsUseCase;
/** Inbound REST adapter for user operations. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public final class UserController {
    public static final String DEFAULT_PATH = "users";
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final UpdateUserSettingsUseCase updateUserSettingsUseCase;
    private final TokenProvider tokenProvider;
    /** Returns user data for the owner of the access token. Returns 403 for invalid token. */
    @GetMapping(value = "/" + DEFAULT_PATH, produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<NebulaUserResponse> getUserData(
            @CookieValue("accessToken") String accessToken) {
        if (tokenProvider.isValid(accessToken)) {
            long userId = tokenProvider.getUserId(accessToken);
            var response = UserRestMapper.toResponse(getUserUseCase.getUser(userId));
            return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }
    /** Updates user profile. Token must belong to the same user. Returns 403 otherwise. */
    @PatchMapping(value = "/" + DEFAULT_PATH + "/profile", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<NebulaUserResponse> updateUserProfile(
            @CookieValue("accessToken") String accessToken,
            @Valid @RequestBody ProfileUpdateRequest profileData) {
        if (tokenProvider.isValid(accessToken, profileData.getUserId())) {
            var response = UserRestMapper.toResponse(
                    updateUserProfileUseCase.updateProfile(UserRestMapper.toCommand(profileData)));
            return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }
    /** Updates user settings. Token must belong to the same user. Returns 403 otherwise. */
    @PutMapping(value = "/" + DEFAULT_PATH + "/settings", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<UserSettingsResponse> updateUserSettings(
            @CookieValue("accessToken") String accessToken,
            @Valid @RequestBody UserSettingsRequest request) {
        if (tokenProvider.isValid(accessToken, request.userId())) {
            var response = UserRestMapper.toSettingsResponse(
                    updateUserSettingsUseCase.updateSettings(UserRestMapper.toSettings(request)));
            return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }
}
