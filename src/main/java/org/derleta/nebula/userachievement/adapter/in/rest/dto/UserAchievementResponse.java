package org.derleta.nebula.userachievement.adapter.in.rest.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.derleta.nebula.userachievement.domain.model.Achievement;

/** REST response DTO for a single user achievement (HATEOAS-aware). */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserAchievementResponse extends RepresentationModel<UserAchievementResponse> {

    private long userId;
    private int achievementId;
    private int value;
    private int level;
    private String progress;
    private Achievement achievement;
}

