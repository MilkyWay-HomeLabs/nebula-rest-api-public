package org.derleta.nebula.userachievement.adapter.in.rest.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.hateoas.Link;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;
import org.derleta.nebula.userachievement.adapter.in.rest.UserAchievementController;
import org.derleta.nebula.userachievement.adapter.in.rest.dto.UserAchievementResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/** Maps domain objects to REST DTOs and adds HATEOAS self-links. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserAchievementRestMapper {

    /** Converts a domain UserAchievement to a response DTO with a self-link. */
    public static UserAchievementResponse toResponse(UserAchievement item) {
        UserAchievementResponse response = UserAchievementResponse.builder()
                .userId(item.userId())
                .achievementId(item.achievementId())
                .value(item.value())
                .level(item.level())
                .progress(item.progress())
                .achievement(item.achievement())
                .build();
        Link selfLink = linkTo(UserAchievementController.class)
                .slash(UserAchievementController.DEFAULT_PATH)
                .slash(response.getUserId())
                .slash(response.getAchievementId())
                .withSelfRel();
        response.add(selfLink);
        return response;
    }

    /** Converts a page of domain objects to a page of response DTOs. */
    public static Page<UserAchievementResponse> toPageResponse(Page<UserAchievement> page) {
        List<UserAchievementResponse> content = page.getContent().stream()
                .map(UserAchievementRestMapper::toResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }
}

