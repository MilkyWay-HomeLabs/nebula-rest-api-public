package org.derleta.nebula.userachievement.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.derleta.nebula.shared.security.TokenProvider;
import org.derleta.nebula.userachievement.adapter.in.rest.dto.UserAchievementResponse;
import org.derleta.nebula.userachievement.adapter.in.rest.mapper.UserAchievementRestMapper;
import org.derleta.nebula.userachievement.application.port.in.*;

import java.util.List;
import java.util.Objects;

/** Inbound REST adapter for user-achievement operations. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public final class UserAchievementController {

    private static final int DEFAULT_PAGE_SIZE = 10;
    public static final String DEFAULT_PATH = "users/achievements";

    private final GetUserAchievementUseCase getUserAchievementUseCase;
    private final GetUserAchievementsListUseCase getUserAchievementsListUseCase;
    private final GetUserAchievementsPageUseCase getUserAchievementsPageUseCase;
    private final TokenProvider tokenProvider;

    /**
     * Retrieves a single user achievement by userId and achievementId.
     * Returns 401 when the token userId does not match the path userId,
     * 403 when the token is invalid.
     */
    @GetMapping(value = "/" + DEFAULT_PATH + "/{userId}/{achievementId}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<UserAchievementResponse> get(
            @PathVariable Long userId,
            @PathVariable Integer achievementId,
            @CookieValue("accessToken") String accessToken) {
        if (tokenProvider.isValid(accessToken)) {
            if (Objects.equals(userId, tokenProvider.getUserId(accessToken))) {
                var response = UserAchievementRestMapper.toResponse(
                        getUserAchievementUseCase.getUserAchievement(userId, achievementId));
                return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.OK);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    /**
     * Retrieves all achievements for the user identified by the access token.
     * Returns 403 when the token is invalid.
     */
    @GetMapping(value = "/" + DEFAULT_PATH + "/list", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<List<UserAchievementResponse>> getList(
            @CookieValue("accessToken") String accessToken) {
        if (tokenProvider.isValid(accessToken)) {
            long userId = tokenProvider.getUserId(accessToken);
            var response = getUserAchievementsListUseCase.getUserAchievementsList(userId)
                    .stream()
                    .map(UserAchievementRestMapper::toResponse)
                    .toList();
            return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    /**
     * Retrieves a paginated, filtered list of user achievements.
     * Returns 403 when the token is invalid.
     *
     * @param filterType supported values: greater, less, greater or equal, less or equal, notequal (default: less or equal)
     */
    @GetMapping(value = "/" + DEFAULT_PATH, produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Page<UserAchievementResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "achievementId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false, defaultValue = "5") Integer level,
            @RequestParam(required = false, defaultValue = "less or equal") String filterType,
            @CookieValue("accessToken") String accessToken) {
        if (tokenProvider.isValid(accessToken)) {
            long userId = tokenProvider.getUserId(accessToken);
            UserAchievementPageQuery query = new UserAchievementPageQuery(
                    page, size, sortBy, sortOrder, userId, level, filterType);
            var responsePage = UserAchievementRestMapper.toPageResponse(
                    getUserAchievementsPageUseCase.getUserAchievementsPage(query));
            return new ResponseEntity<>(responsePage, new HttpHeaders(), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }
}

